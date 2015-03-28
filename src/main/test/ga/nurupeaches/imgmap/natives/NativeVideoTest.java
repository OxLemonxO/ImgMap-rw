package ga.nurupeaches.imgmap.natives;

public class NativeVideoTest {

	public static void main(String[] args) {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");

		String videoPath = "/home/tsunko/Videos/[DeadFish] Saenai Heroine no Sodatekata - 09 [720p][AAC].mp4";
		NativeVideo video = new NativeVideo(videoPath);

		assert video.getPointer() != -1;
		assert video.getSource().equalsIgnoreCase(videoPath);
		video.read();
	}

}