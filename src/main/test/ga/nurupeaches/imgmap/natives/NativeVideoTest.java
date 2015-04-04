package ga.nurupeaches.imgmap.natives;

public class NativeVideoTest {

	public static void main(String[] args) {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");

		String videoPath = "/home/tsunko/Videos/NichijouNativeVideo.mp4";
		NativeVideo video = new NativeVideo(new DummyNCH(), videoPath);

		assert video.getPointer() != -1;
		System.out.println("We have a pointer @ " + video.getPointer() + "!");
		assert video.getSource().equalsIgnoreCase(videoPath);
		System.out.println("Verified that our source matched our CPP's source!!!");
		video.read();
		System.out.println("WE PASSED LIBAVCODEC WOOO *champagne pop*");
	}

}