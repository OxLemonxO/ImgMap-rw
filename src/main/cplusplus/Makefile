CXX=g++ -fPIC -pedantic -std=c++11

ifeq ($(OS),Windows_NT)
    CXX_INCLUDES=-I"C:/Program Files/Java/jdk1.8.0_31/include" -I"C:/Program Files/Java/jdk1.8.0_31/include/win32" -I"C:/FFmpeg/include"
    CXX_FFMPEG_LIBS=-L"C:/FFmpeg/lib" -L"C:/FFmpeg/bin"
    EXPORT_FILE=NativeVideo.dll
else
    CXX_INCLUDE=-I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux
    CXX_FFMPEG_LIBS=-lavformat -lavcodec -lswscale
    EXPORT_FILE=libNativeVideo.so
endif

CXXFLAGS=-Wall -g $(CXX_fFMPEG_INCLUDES)

libNativeVideo.so : NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o
	$(CXX) $(CXXFLAGS) $(CXX_FFMPEG_LIBS) -shared -o $(EXPORT_FILE) NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o

NativeVideoImpl.o : NativeVideoImpl.cpp NativeVideoImpl.h
	$(CXX) -c $(CXX_INCLUDES) NativeVideoImpl.cpp

ga_nurupeaches_imgmap_natives_NativeVideo.o : ga_nurupeaches_imgmap_natives_NativeVideo.cpp ga_nurupeaches_imgmap_natives_NativeVideo.h
	$(CXX) -c $(CXX_INCLUDES) ga_nurupeaches_imgmap_natives_NativeVideo.cpp

clean :
	rm -f libNativeVideo.so NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o