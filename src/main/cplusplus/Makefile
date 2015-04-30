ifeq ($(OS),Windows_NT)
    CXX_INCLUDES=-I"C:/Program Files/Java/jdk1.8.0_31/include" -I"C:/Program Files/Java/jdk1.8.0_31/include/win32" -I"C:/FFmpeg/include" -L"C:/FFmpeg/x64/lib"
    CXX_FFMPEG_LIBS=-L"C:/MinGW/lib/gcc/x86_64-w64-mingw32/4.9.2" -lstdc++ -L"C:/FFmpeg/lib" -lavutil -lavformat -lavcodec -lswscale
    EXPORT_FILE=NativeVideo.dll
else
    CXX_INCLUDES=-I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux
    CXX_FFMPEG_LIBS=-lavformat -lavcodec -lswscale
    EXPORT_FILE=libNativeVideo.so
endif

CXX=g++ -o $(EXPORT_FILE) -fPIC -pedantic -std=c++11 -Wall

libNativeVideo.so : ga_nurupeaches_imgmap_natives_NativeVideo.o
	$(CXX) $(CXX_INCLUDES) $(CXX_FFMPEG_LIBS) ga_nurupeaches_imgmap_natives_NativeVideo.o -shared

ga_nurupeaches_imgmap_natives_NativeVideo.o : ga_nurupeaches_imgmap_natives_NativeVideo.cpp ga_nurupeaches_imgmap_natives_NativeVideo.h
	$(CXX) $(CXX_INCLUDES) ga_nurupeaches_imgmap_natives_NativeVideo.cpp

clean :
	rm -f libNativeVideo.so ga_nurupeaches_imgmap_natives_NativeVideo.o


J_ARCH=$(shell java -version)

ifeq($(OS),Windows_NT)
    CXX_INCLUDES=-I"C:/Program Files/Java/jdk1.8.0_31/include" -I"C:/Program Files/Java/jdk1.8.0_31/include/win32"  -I"C:/FFmpeg/include"
    CXX_LIBARIES=-L"C:/FFmpeg/x64/lib
else

endif