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
		std::vector<jbyte> javaBytes;
		int y, i, iMax = impl->getWidth()*3;

		for(y=0; y < impl->getHeight(); y++){
			for(i=0; i < iMax; i++){
				javaBytes.push_back((frame->data[0]+y*frame->linesize[0])[i]);
			}
		}

		jbyteArray arr = env->NewByteArray(javaBytes.size());
		env->SetByteArrayRegion(arr, 0, javaBytes.size(), javaBytes.data());
		doCallback(env, callback, javaNV, arr);
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

}