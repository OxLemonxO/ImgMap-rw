CXX = g++ -fPIC -pedantic -std=c++11
CXXFLAGS = -Wall -g -lavformat -lavcodec -lswscale

libNativeVideo.so : NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o
	$(CXX) $(CXXFLAGS) -shared -o libNativeVideo.so NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o

NativeVideoImpl.o : NativeVideoImpl.cpp NativeVideoImpl.h
	$(CXX) -c -I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux NativeVideoImpl.cpp

ga_nurupeaches_imgmap_natives_NativeVideo.o : ga_nurupeaches_imgmap_natives_NativeVideo.cpp ga_nurupeaches_imgmap_natives_NativeVideo.h
	$(CXX) -c -I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux ga_nurupeaches_imgmap_natives_NativeVideo.cpp

clean :
	rm -f libNativeVideo.so NativeVideoImpl.o ga_nurupeaches_imgmap_natives_NativeVideo.o