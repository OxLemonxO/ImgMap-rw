CXX=g++ -fPIC
CXX_WARNING_FLAGS=-Wall -pedantic

J_ARCH = $(shell java -version)

ifeq ($(OS),Windows_NT)
	ifeq (,$(findstring 64-Bit,$(J_ARCH)))
		CXX_INCLUDES=-I"C:/Program Files/Java/jdk1.8.0_31/include" -I"C:/Program Files/Java/jdk1.8.0_31/include/win32" -I"C:/FFmpeg/include"
		CXX_LIBRARIES=-L"C:/FFmpeg/x64/lib" -lavutil -lavformat -lavcodec -lswscale
		CXX_ARCH=-m64
	else
		CXX_INCLUDES=-I"C:/Program Files (x86)/Java/jdk1.8.0_31/include" -I"C:/Program Files (x86)/Java/jdk1.8.0_31/include/win32" -I"C:/FFmpeg/include"
		CXX_LIBRARIES=-L"C:/FFmpeg/x32/lib" -lavutil -lavformat -lavcodec -lswscale
		CXX_ARCH=-m32
	endif

	EXPORT_FILE=NativeVideo.dll
else
	CXX_INCLUDES=-I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux
	CXX_LIBRARIES=-lavformat -lavcodec -lswscale -lavutil
	EXPORT_FILE=libNativeVideo.so
endif

CXX_COMPILE_FLAGS=-std=c++11

shared-library : ga_nurupeaches_imgmap_natives_NativeVideo.o
	$(CXX) -o $(EXPORT_FILE) ga_nurupeaches_imgmap_natives_NativeVideo.o $(CXX_INCLUDES) $(CXX_LIBRARIES) $(CXX_COMPILE_FLAGS) -shared

ga_nurupeaches_imgmap_natives_NativeVideo.o : ga_nurupeaches_imgmap_natives_NativeVideo.h ga_nurupeaches_imgmap_natives_NativeVideo.cpp
	$(CXX) -c $(CXX_INCLUDES) $(CXX_FFMPEG_LIBS) $(CXX_WARNING_FLAGS) $(CXX_COMPILE_FLAGS) ga_nurupeaches_imgmap_natives_NativeVideo.cpp

clean :
	rm -f $(EXPORT_FILE) ga_nurupeaches_imgmap_natives_NativeVideo.o