#include "ga_nurupeaches_imgmap_natives_NativeVideo.h"
#include "NativeVideoImpl.h"

string jstrToStr(JNIEnv* env, jstring jstr){
	string cppStr;
	const char* javaBytes = env->GetStringUTFChars(jstr, 0);
	cppStr = string(javaBytes);
	env->ReleaseStringUTFChars(jstr, javaBytes);
	return cppStr;
}

jstring strToJString(JNIEnv* env, string str){
	return env->NewStringUTF(str.c_str());
}

void doCallback(JNIEnv* env, jobject callback, jintArray arg){
	jclass klass = env->GetObjectClass(callback);
	jmethodID id = env->GetMethodID(klass, "handleData", "([I)V");
	if(!id) return; // No method found.
	env->CallVoidMethod(callback, id, arg);
}

extern "C" {

	JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read(JNIEnv *env, jobject thisObject, jlong pointer, jobject callback){
		NativeVideoImpl* impl = (NativeVideoImpl*)pointer;
		if(!impl->isOpen()){
			impl->open();
		}

		doCallback(env, callback, env->NewIntArray(5));
	}

	JNIEXPORT jlong JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_newNativeVideo(JNIEnv* env, jobject thisObject, jstring string){
		return (jlong)(NativeVideoImpl*)new NativeVideoImpl(jstrToStr(env, string));
	}

	JNIEXPORT jstring JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_getSource(JNIEnv * env, jobject thisObject, jlong pointer){
		return strToJString(env, ((NativeVideoImpl*)pointer)->getSource());
	}

}