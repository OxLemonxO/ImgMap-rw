package ga.nurupeaches.imgmap.natives;

import java.io.File;

public class NativeVideo {

	public native void read(long pointer, NativeCallbackHandler callback);
	public native long newNativeVideo(String filepath);

	private long pointer;

	public NativeVideo(File file){
		pointer = newNativeVideo(file.getAbsolutePath());
	}

}