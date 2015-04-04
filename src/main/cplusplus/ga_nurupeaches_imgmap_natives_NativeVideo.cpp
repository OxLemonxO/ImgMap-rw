#include "ga_nurupeaches_imgmap_natives_NativeVideo.h"
#include "NativeVideoImpl.h"
#include <vector>

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

	JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read(JNIEnv *env, jobject thisObject, jlong pointer, jobject javaNV, jobject callback){
		NativeVideoImpl* impl = (NativeVideoImpl*)pointer;
		if(!impl->isOpen()){
			if(impl->open() != 0){
				return;
			}
		}

		AVFrame* frame = impl->fetchNextFrame();
		int bufferSize = avpicture_get_size(PIX_FMT_RGB24, impl->getCodec()->width, impl->getCodec()->height);
		jbyteArray arr = env->NewByteArray(bufferSize);
		unsigned char* buffer = new unsigned char[bufferSize];
		avpicture_layout((AVPicture*)frame, PIX_FMT_RGB24, impl->getCodec()->width, impl->getCodec()->height, buffer, bufferSize);
		env->SetByteArrayRegion(arr, 0, bufferSize, (jbyte*)buffer);
		doCallback(env, callback, javaNV, arr);
		delete[] buffer;
		env->DeleteLocalRef(arr);
	}

	JNIEXPORT jlong JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_newNativeVideo(JNIEnv* env, jobject thisObject, jstring string){
		return (jlong)(NativeVideoImpl*)new NativeVideoImpl(jstrToStr(env, string));
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

	JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_close(JNIEnv* env, jobject thisObject, jlong pointer){
		((NativeVideoImpl*)pointer)->close();
	}

}