#include "ga_nurupeaches_imgmap_natives_NativeVideo.h"
#include "NativeVideoImpl.h"
#include <vector>
#include <chrono>
#include <thread>
#include <sstream>
#include <unistd.h>

int GLOBAL_DDD_PID = -1;

string jstrToStr(JNIEnv* env, jstring jstr){
	string cppStr;
	const char* javaBytes = env->GetStringUTFChars(jstr, 0);
	cppStr = string(javaBytes);
	env->ReleaseStringUTFChars(jstr, javaBytes);
	return cppStr;
}

inline jstring strToJString(JNIEnv* env, string str){
	return env->NewStringUTF(str.c_str());
}

void doCallback(JNIEnv* env, jobject callback, jobject javaNV, jbyteArray arg){
	jclass klass = env->GetObjectClass(callback);
	jmethodID id = env->GetMethodID(klass, "handleData", "(Lga/nurupeaches/imgmap/natives/NativeVideo;[B)V");
	if(!id) return; // No method found.
	env->CallVoidMethod(callback, id, javaNV, arg);
}

extern "C" {

	JNIEXPORT jint JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read(JNIEnv *env, jobject thisObject, jlong pointer, jobject javaNV, jobject callback){
		NativeVideoImpl* impl = (NativeVideoImpl*)pointer;
		if(!impl->isOpen()){
			int status = impl->open();
			if(status != 0){
				return status;
			}
		}

		AVFrame* frame = impl->fetchNextFrame();
		unsigned char* buffer = impl->getBuffer();
		int bufferSize = impl->getBufferSize();
		jbyteArray arr = env->NewByteArray(bufferSize);

		avpicture_layout((AVPicture*)frame, PIX_FMT_RGB24, impl->getWidth(), impl->getHeight(), buffer, bufferSize);
		env->SetByteArrayRegion(arr, 0, bufferSize, (jbyte*)buffer);
		doCallback(env, callback, javaNV, arr);
		env->DeleteLocalRef(arr);
		return 0;
	}

	JNIEXPORT jlong JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_newNativeVideo(JNIEnv* env, jobject thisObject, jstring string, jint dW, jint dH){
  		return (jlong)(NativeVideoImpl*)new NativeVideoImpl(jstrToStr(env, string), (int)dW, (int)dH);
	}

	JNIEXPORT jstring JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_getSource(JNIEnv* env, jobject thisObject, jlong pointer){
		return strToJString(env, ((NativeVideoImpl*)pointer)->getSource());
	}

	JNIEXPORT jint JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_getWidth(JNIEnv* env, jobject thisObject, jlong pointer){
		return ((NativeVideoImpl*)pointer)->getWidth();
	}

	JNIEXPORT jint JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_getHeight(JNIEnv* env, jobject thisObject, jlong pointer){
		return ((NativeVideoImpl*)pointer)->getHeight();
	}

	JNIEXPORT jboolean JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_isStreaming(JNIEnv* env, jobject thisObject, jlong pointer){
		return ((NativeVideoImpl*)pointer)->isOpen();
	}

	JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_close(JNIEnv* env, jobject thisObject, jlong pointer){
		((NativeVideoImpl*)pointer)->close();
	}

}