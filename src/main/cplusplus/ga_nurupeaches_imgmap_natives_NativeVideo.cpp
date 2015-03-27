#include "ga_nurupeaches_imgmap_natives_NativeVideo.h"
#include "NativeVideoImpl.h"
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read(JNIEnv *env, jobject thisObject, jlong pointer, jobject callback){
	return;
}

JNIEXPORT jlong JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_newNativeVideo(JNIEnv *env, jobject thisObject, jstring string){
	NativeVideoImpl* impl = new NativeVideoImpl();
	return (long)impl;
}

#ifdef __cplusplus
}
#endif