package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.utils.YTRegexHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NativeVideoTest {

	public static final int WIDTH = 1280, HEIGHT = 720;
	public static final String videoPath = YTRegexHelper.getDirectLinks("CpasSV_mw2o").get(0);
	private NativeVideo video;

	private final BufferedImage image;
	private final JFrame frame = new JFrame("libNativeVideo.so - DBGPlayer");
	private final JPanel panel = new JPanel(){

		@Override
		public void paintComponent(Graphics g){
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}

		@Override
		public void repaint() {
			super.repaint();
		}
	};

	public static void main(String[] args) throws Exception {
        String libName = (System.getProperty("os.name").toLowerCase().contains("win") ? "NativeVideo.dll" : "libNativeVideo.so");
        System.load(System.getProperty("user.dir") + "/src/main/cplusplus/" + libName);

        NativeVideo.initialize(DebugCallbackHandler.class);

		NativeVideoTest test = new NativeVideoTest();
		test.showGUI();
		test.startNativeThread();
	}

	public NativeVideoTest() throws IOException {
		video = new NativeVideo(new DebugCallbackHandler(this), WIDTH, HEIGHT);
		image = video.getFrame();
	}

	public void startNativeThread() throws InterruptedException {
		try{
			video.open(videoPath);
		} catch (IOException e){
			e.printStackTrace();
		}

        while(video.isStreaming()){
            video.read();
            panel.repaint();
            Thread.sleep(33);
        }

		video.close();
	}

	public void showGUI(){
		frame.setSize(1280, 720);
		frame.setContentPane(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}