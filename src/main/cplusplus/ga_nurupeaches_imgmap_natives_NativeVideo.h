/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ga_nurupeaches_imgmap_natives_NativeVideo */

#ifndef _Included_ga_nurupeaches_imgmap_natives_NativeVideo
#define _Included_ga_nurupeaches_imgmap_natives_NativeVideo
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    initialize
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_initialize
  (JNIEnv *, jclass, jclass);

/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    _init
 * Signature: (II)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo__1init
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    _open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo__1open
  (JNIEnv *, jobject, jstring);

/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    read
 * Signature: (Lga/nurupeaches/imgmap/natives/CallbackHandler;)V
 */
JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_read
  (JNIEnv *, jobject, jobject);

/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    isStreaming
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_isStreaming
  (JNIEnv *, jobject);

/*
 * Class:     ga_nurupeaches_imgmap_natives_NativeVideo
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_ga_nurupeaches_imgmap_natives_NativeVideo_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
