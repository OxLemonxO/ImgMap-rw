#ifndef __CNativeVideoImpl
#define __CNativeVideoImpl
#define BUFFER_SIZE 2048

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
	int frameFinished, i;

	AVCodecContext* codecContext;
	AVFormatContext* formatContext;

	AVPacket packet;
	int videoStreamId;
	uint8_t* buffer;

	struct SwsContext* imgConvertContext;

	bool hasOpened;
	int framePosition;

	public:
		NativeVideoImpl (string src);
		AVFrame* fetchNextFrame();
		string getSource(){ return videoSource; }
		AVCodecContext* getCodec(){ return codecContext; }
		void fillRGBData(uint8_t* data);
		int open();
		bool isOpen();
		void close();

		int getHeight();
		int getWidth();

};
#endif