	package ga.nurupeaches.imgmap.natives;

	import ga.nurupeaches.imgmap.utils.YTRegexHelper;

	import java.io.IOException;

	public class NativeVideoTest {

		public static final int WIDTH = 0, HEIGHT = 0;
		public static final String videoPath = YTRegexHelper.getDirectLinks("rnQBF2CIygg").get(0);
		private NativeVideo video;

		public static void main(String[] args) throws Exception {
			System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");
			NativeVideo.initialize(DebugCallbackHandler.class);

			NativeVideoTest test = new NativeVideoTest();
			test.nativeWork();
		}

		public NativeVideoTest() throws IOException {
			video = new NativeVideo(new DebugCallbackHandler(1280, 720), WIDTH, HEIGHT);
		}

		public void nativeWork() throws InterruptedException {
			try{
				video.open(videoPath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			video.read();
			new Thread().start();
			video.read(); // Returns 0 length array.
		}

	}