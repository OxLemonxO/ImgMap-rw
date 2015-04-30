REM this shouldn't be allowed, but this is the only working command line for some reason.
REM open nuwen's open_distro_window.bat and run the below.
g++ -o NativeVideo.dll ga_nurupeaches_imgmap_natives_NativeVideo.cpp -I"C:/Program Files/Java/jdk1.8.0_31/include" -I"C:/Program Files/Java/jdk1.8.0_31/include/win32" -I"C:/FFmpeg/include" -L"C:/FFmpeg/x64/lib" -lavutil -lavformat -lavcodec -lswscale -std=c++11 -shared -m64
