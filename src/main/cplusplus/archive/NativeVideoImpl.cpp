#include "NativeVideoImpl.h"

// static variable to track if we've registered codecs and formats, along with initializing the networking power
static bool initialized = false;

NativeVideoImpl::NativeVideoImpl(string src, int dw = 0, int dh = 0){
	if(!initialized){
		av_register_all();
		avcodec_register_all();
		avformat_network_init();
		initialized = true;
	}

	videoSource = src;
	destWidth = dw;
	destHeight = dh;
}

int NativeVideoImpl::open(){
	// open video
	formatContext = avformat_alloc_context();
	if(avformat_open_input(&formatContext, videoSource.c_str(), NULL, NULL) != 0)
		return 1; // I/O open error

	// find basic stream information
	if(avformat_find_stream_info(formatContext, NULL) < 0)
		return 2; // No stream info

	videoStreamId = -1;
	for(int i=0; i < formatContext->nb_streams; i++){
		if(formatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO){
			videoStreamId = i;
			break;
		}
	}

	if(videoStreamId == -1)
		return 3; // No video stream

	codecContext = formatContext->streams[videoStreamId]->codec;

	if(destWidth == 0){
		destWidth = codecContext->width;
	}

	if(destHeight == 0){
		destHeight = codecContext->height;
	}

	codec = avcodec_find_decoder(codecContext->codec_id);
	if(codec == NULL)
		return 4; // No codec found

	if(avcodec_open2(codecContext, codec, NULL) < 0)
		return 5; // Failed to open codec

	frame = av_frame_alloc();
	frameRGB = av_frame_alloc();

	if(frame == NULL || frameRGB == NULL)
		return 6; // Failed to allocate frames

	imgConvertContext = sws_getContext(codecContext->width, codecContext->height, codecContext->pix_fmt,
										destWidth, destHeight, PIX_FMT_BGR24, SWS_BICUBIC,
										NULL, NULL, NULL);

	frameBuffer_size = avpicture_get_size(PIX_FMT_RGB24, destWidth, destHeight);
	frameRGB_buffer = (uint8_t*)av_malloc(frameBuffer_size * sizeof(uint8_t));
	avpicture_fill((AVPicture*)frameRGB, frameRGB_buffer, PIX_FMT_RGB24, destWidth, destHeight);
	frameBuffer = new unsigned char[frameBuffer_size];
	hasOpened = true;
	return 0;
}

bool NativeVideoImpl::isOpen(){
	return hasOpened;
}

int NativeVideoImpl::getWidth(){
 	return destWidth;
}

int NativeVideoImpl::getHeight(){
	return destHeight;
}

AVFrame* NativeVideoImpl::fetchNextFrame(){
	while(av_read_frame(formatContext, &packet) >= 0){
		if(packet.stream_index == videoStreamId){
			avcodec_decode_video2(codecContext, frame, &frameFinished, &packet);

			if(frameFinished){
				i++;

				sws_scale(imgConvertContext, (const uint8_t* const*)frame->data, frame->linesize, 0, codecContext->height,
							frameRGB->data, frameRGB->linesize);

				av_free_packet(&packet);
				break;
			}
		}

		av_free_packet(&packet);
	}

	return frameRGB;
}

void NativeVideoImpl::close(){
	av_free(frameRGB_buffer);
	av_free(frameRGB);
	av_free(frame);
	avcodec_close(codecContext);
	avformat_close_input(&formatContext);
	av_free(codec);
	av_free_packet(&packet);
	sws_freeContext(imgConvertContext);
	delete[] frameBuffer;
	hasOpened = false;
}