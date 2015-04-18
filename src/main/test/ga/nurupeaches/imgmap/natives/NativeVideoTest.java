package ga.nurupeaches.imgmap.natives;

import java.io.IOException;

public class NativeVideoTest {

	public static void main(String[] args) throws IOException {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");
		NativeVideo.initialize(DummyNCH.class);

		String videoPath = "/home/tsunko/Videos/NichijouNativeVideo.mp4";
		NativeVideo video = new NativeVideo(new DummyNCH(), 128, 128);
		video.open(videoPath);
		video.read();
		System.out.println("WE PASSED LIBAVCODEC WOOO *champagne pop*");
	}

}