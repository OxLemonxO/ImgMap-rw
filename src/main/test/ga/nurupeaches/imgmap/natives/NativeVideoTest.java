package ga.nurupeaches.imgmap.natives;

import java.io.File;

public class NativeVideoTest {

	public static void main(String[] args) {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");
		new NativeVideo(new File("/home/tsunko/Videos/[DeadFish] Saenai Heroine no Sodatekata - 09 [720p][AAC].mp4"));
	}

}