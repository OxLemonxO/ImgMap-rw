package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.utils.YTRegexHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NativeVideoTest extends JPanel {

	public static final int WIDTH = 1280, HEIGHT = 720;
	public static final String videoPath = YTRegexHelper.getDirectLinks("rnQBF2CIygg").get(0);

    private final NativeVideo video;
	private final BufferedImage image;
	private final JFrame frame = new JFrame("libNativeVideo.so - DBGPlayer");

    @Override
    public int getWidth(){
        return frame.getWidth();
    }

    @Override
    public int getHeight(){
        return frame.getHeight();
    }

    @Override
    public void paintComponent(Graphics g){
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

	public static void main(String[] args) throws Exception {
        String libName = (System.getProperty("os.name").toLowerCase().contains("win") ? "NativeVideo.dll" : "libNativeVideo.so");

        if(System.getProperty("user.dir").contains("ImgMap-rw")) {
            System.load(System.getProperty("user.dir") + "/src/main/cplusplus/" + libName);
        } else {
            System.loadLibrary("NativeVideo");
        }

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
            Thread.sleep(33, 333);
        }

		video.close();
	}

	public void showGUI(){
        frame.setSize(1280, 720);
		frame.setContentPane(this);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}