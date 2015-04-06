#ifndef __CNativeVideoImpl
#define __CNativeVideoImpl

#include <string>
#include <jni.h>

extern "C" {
	#include "libavcodec/avcodec.h"
	#include "libavformat/avformat.h"
	#include "libswscale/swscale.h"
}

using std::string;

class NativeVideoImpl {

	string videoSource;

	AVCodec* codec;

	AVFrame* frame;
	AVFrame* frameRGB;
	uint8_t* frameRGB_buffer; // Buffer for avcodec to use.
	int frameFinished, i;
	int destWidth, destHeight;
	unsigned char* frameBuffer; // Buffer for data going from C++ to Java
	int frameBuffer_size;

	AVCodecContext* codecContext;
	AVFormatContext* formatContext;

	AVPacket packet;
	int videoStreamId;

	struct SwsContext* imgConvertContext;

	bool hasOpened;
	int framePosition;

	public:
		NativeVideoImpl (string src);
		NativeVideoImpl (string src, int destWidth, int destHeight);
		AVFrame* fetchNextFrame();
		string getSource(){ return videoSource; }
		AVCodecContext* getCodec(){ return codecContext; }
		void setBuffer(unsigned char* buf){ frameBuffer = buf; }
		int getBufferSize(){ return frameBuffer_size; }
		unsigned char* getBuffer(){ return frameBuffer; }
		void fillRGBData(uint8_t* data);
		int open();
		bool isOpen();
		void close();

		int getHeight();
		int getWidth();

};
#endif