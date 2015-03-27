#!/usr/bin/env bash
g++ -I/usr/lib/jvm/java-8-jdk/include -I/usr/lib/jvm/java-8-jdk/include/linux -m64 -o libNativeVideo.so -fPIC -shared ga_nurupeaches_imgmap_natives_NativeVideo.cpp
