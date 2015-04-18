#include "ga_nurupeaches_imgmap_natives_NativeVideo.h"

#include <unordered_map>
#include <string>
#include <stdio.h>
#include <jvmti.h>
#include <iostream>

extern "C" {
	#include "libavcodec/avcodec.h"
	#include "libavformat/avformat.h"
	#include "libswscale/swscale.h"
}

using std::string;

// Basic data structure for storing minor information
struct NativeVideoContext {
	/* Very general information. */
	bool isStreaming = false;
	int width; // Target width
	int height; // Target height
	string source; // The source file we're reading from (can be local or network)

	/* Relates to/is used by libavcodec. */
	// "Raw" objects; contextless.
	AVCodec* codec;
	AVPacket packet;

	// Frames we use to go to and from the raw H264 (or whatever we're using) to basic RGB.
	AVFrame* rawFrame;
	AVFrame* rgbFrame;
	uint8_t* rgbFrameBuffer; // Buffer for avcodec to use.
	int bufferSize; // Buffer size (without sizeof(uint8_t))
	jbyteArray javaArray; // Java array; initialized here instead of every single time we call read(). Which was totally
						  // a great design idea. Way to go me.

	// The ID of the video stream we want to get frames from. It's normally the first video stream found.
	int videoStreamId;
	// int that holds the state of a frame being finished or not.
	int frameFinished;

	// Contexts
	AVCodecContext* codecContext;
	AVFormatContext* formatContext;

	// Software scaling context.
	struct SwsContext* imgConvertContext;
};

// bool to represent whether or not we initialized avcodec and co. already.
bool initialized;

// "Global" map for relating NativeVideos (jobject) to NativeVideoContexts
//std::unordered_map<unsigned long long, NativeVideoContext*> LOOKUP_MAP;

// Caching the jmethodID
jmethodID id;

// JVMTI "global" pointer.
jvmtiEnv* jvmti = NULL;

inline void doCallback(JNIEnv* env, jobject callback, jbyteArray arr){
	env->CallVoidMethod(callback, id, arr);
}

/*
 * Converts a jstring to a std::string.
 */
inline string convString(JNIEnv* env, jstring jstr){
	const char* javaBytes = env->GetStringUTFChars(jstr, 0);
	string cStr = string(javaBytes);
	env->ReleaseStringUTFChars(jstr, javaBytes);
	return cStr;
}

inline void checkJVMTI(JNIEnv* env){
	if(jvmti == NULL){
		JavaVM* vm_ptr;
		env->GetJavaVM((JavaVM**)&vm_ptr);
		vm_ptr->GetEnv((void**)&jvmti, (jint)JVMTI_VERSION_1_0);
		jvmtiCapabilities capabilities;
		(void)memset(&capabilities, 0, sizeof(jvmtiCapabilities));
		capabilities.can_tag_objects = 1;
		jvmtiError err = jvmti->AddCapabilities(&capabilities);
	}
}

jlong getTag(JNIEnv* env, jobject obj){
	checkJVMTI(env);
	jlong tag = 0;
	jvmtiError err = jvmti->GetTag(obj, &tag);
	printf("err: %d\n", (int)err);
	if(tag == 0){
		printf("null tag\n");
		fflush(stdout);
		return 0;
	} else {
		std::cout << "got tag: " << (long int)tag << std:endl;
		return tag;
	}
}

void setTag(JNIEnv* env, jobject obj, long int tag){
	checkJVMTI(env);
	printf("setting tag: %lld\n", tag);
	fflush(stdout);
	jvmtiError err = jvmti->SetTag(obj, (jlong)tag);
	if(err != 0){

	}
}

/*
  jvmtiError GetTag(jobject object, jlong* tag_ptr);
  jvmtiError SetTag(jobject object, jlong tag);
*/

NativeVideoContext* getContext(JNIEnv* env, jobject jthis, bool throwException){
	jlong tag_ptr = getTag(env, jthis);
	if(tag_ptr == 0){
		if(throwException){
			env->ThrowNew(env->FindClass("java/io/IOException"), "Failed to find a NativeVideoContext associated with "
				"this object. Perhaps something slipped and we didn't call init(int, int) first? I don't know, but this "
				"is a long error message. Why? Because I can!");
		}

		return NULL;
	} else {
		return (NativeVideoContext*)(long int)tag_ptr;
	}
}

// For the love of god, never forget this part. C++ mangler is OP!
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_initialize(JNIEnv* env, jclass callingClass, jclass handlerClass){
	av_register_all();
	avcodec_register_all();
	avformat_network_init();

   	id = env->GetMethodID(handlerClass, "handleData", "([B)V");
   	if(!id){
		env->ThrowNew(env->FindClass("java.lang.invoke.WrongMethodTypeException"), "Failed to locate handleMethod"
			"for the given class");
		return;
   	}
}

/*
 * Natively initializes a NativeVideo.
 */
JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo__1init(JNIEnv* env, jobject jthis, jint width, jint height){
	NativeVideoContext* context = getContext(env, jthis, false);
	if(context == NULL){
		context = new NativeVideoContext();
		setTag(env, jthis, (long int)context);
	}
	context->width = width;
	context->height = height;
}

/*
 * Opens all the necessary components for "source".
 * Can return:
 * 		1 - I/O error while trying to read "source";
 			also returned along with an IOException if no NativeVideoContext was found.
 *		2 - No stream information found.
 *		3 - No video stream found.
 *		4 - No available codec to decode "source".
 *		5 - Failed to open the codec for any reason.
 *		6 - Not enough memory or general failure to allocate AVFrames.
 *		0 - Successfully opened. Not an error.
 * Also can throw:
 * 		IOException - If the NativeVideo never called init(int, int) for whatever reason.
 */
JNIEXPORT jint JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo__1open(JNIEnv* env, jobject jthis, jstring source){
		printf("doing magic\n");
		fflush(stdout);
	NativeVideoContext* context = getContext(env, jthis, true);
	if(context == NULL){
		return 1;
	}

	context->source = convString(env, source); // Set the source of the video here first; we'll need it.
	context->formatContext = avformat_alloc_context(); // Allocate a new format context.

	// open the source for format inspection
	if(avformat_open_input((AVFormatContext**)&(context->formatContext), context->source.c_str(), NULL, NULL) != 0)
		return 1; // I/O open error

	// find stream information
	if(avformat_find_stream_info(context->formatContext, NULL) < 0)
		return 2; // No stream info

	context->videoStreamId = -1; // initialize at -1 to see later if we found a stream
	for(int i=0; i < context->formatContext->nb_streams; i++){
		if(context->formatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO){
			context->videoStreamId = i;
			break;
		}
	}

	if(context->videoStreamId == -1){
		return 3; // no video stream found
	}

	// If width is 0, assume the codec's width
	if(context->width == 0){
		context->width = context->codecContext->width;
	}

	// If height is 0, assume the codec's width
	if(context->height == 0){
		context->height = context->codecContext->height;
	}

	// find the decoder for the codec
	context->codec = avcodec_find_decoder(context->codecContext->codec_id);
	if(context->codec == NULL)
		return 4; // No codec found

	// and then open it for usage
	if(avcodec_open2(context->codecContext, context->codec, NULL) < 0)
		return 5; // Failed to open codec

	// allocate our frames
	context->rawFrame = av_frame_alloc();
	context->rgbFrame = av_frame_alloc();

	// if either or null, I'm going to assume we ran out of memory.
	if(context->rawFrame == NULL || context->rgbFrame == NULL)
		return 6; // Failed to allocate frames

	int memorySpace = avpicture_get_size(PIX_FMT_RGB24, context->width, context->height);
	context->bufferSize = memorySpace;
	context->rgbFrameBuffer = (uint8_t*)av_malloc(memorySpace * sizeof(uint8_t));
	context->javaArray = env->NewByteArray(memorySpace);
	context->isStreaming = true;
	// Successful opening.
	return 0;
}

/*
 * Reads a frame; calls the callback's callback method when finished.
 */
JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read(JNIEnv* env, jobject jthis, jobject callback){
	NativeVideoContext* context = getContext(env, jthis, true);
	if(context == NULL){
		return;
	}

	while(av_read_frame(context->formatContext, &(context->packet)) >= 0){
		if(context->packet.stream_index == context->videoStreamId){
			avcodec_decode_video2(context->codecContext, context->rawFrame, &(context->frameFinished), &(context->packet));

			if(context->frameFinished){
				sws_scale(context->imgConvertContext, (const uint8_t* const*)context->rawFrame->data,
							context->rawFrame->linesize, 0, context->codecContext->height,
							context->rgbFrame->data, context->rgbFrame->linesize);

				av_free_packet(&(context->packet));
				break;
			}
		}

		av_free_packet(&(context->packet));
	}

    jbyteArray arr = context->javaArray;
    avpicture_layout((AVPicture*)context->rgbFrame, PIX_FMT_RGB24, context->width, context->height, (unsigned char*)arr, context->bufferSize);
    env->SetByteArrayRegion(arr, 0, context->bufferSize, (jbyte*)arr);
    doCallback(env, callback, arr);
}

JNIEXPORT jboolean JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_isStreaming(JNIEnv* env, jobject jthis){
	NativeVideoContext* context = getContext(env, jthis, true);
	if(context == NULL){
    	return false;
    }

	return context->isStreaming;
}

JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_close(JNIEnv* env, jobject jthis){
	NativeVideoContext* context = getContext(env, jthis, true);
	if(context == NULL){
		return;
	}

	av_free(context->rgbFrameBuffer);
	av_free(context->rgbFrame);
	av_free(context->rawFrame);
	avcodec_close(context->codecContext);
	avformat_close_input(&(context->formatContext));
	av_free(context->codec);
	av_free_packet(&(context->packet));
	sws_freeContext(context->imgConvertContext);
	env->DeleteLocalRef(context->javaArray);
	context->isStreaming = false;
	// do closing stuff
}

#ifdef __cplusplus
}
#endif
