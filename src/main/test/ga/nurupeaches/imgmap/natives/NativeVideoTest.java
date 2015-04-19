package ga.nurupeaches.imgmap.natives;

import java.io.IOException;

public class NativeVideoTest {

	private NativeVideo video;
	private DebugCallbackHandler handler = new DebugCallbackHandler();

	public static void main(String[] args) throws IOException {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");
		NativeVideo.initialize(DebugCallbackHandler.class);

		NativeVideoTest test = new NativeVideoTest();
		test.startWork();
	}

	public NativeVideoTest() throws IOException {
		String videoPath = "/home/tsunko/Videos/NichijouNativeVideo.mp4";
		video = new NativeVideo(handler, 1280, 720);
		video.open(videoPath);
	}

	public void startWork(){
		while(true){
			video.read();
			new Thread().start();
			try{
				Thread.sleep(13);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

}