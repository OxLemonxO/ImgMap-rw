package ga.nurupeaches.imgmap.natives;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class NativeVideoTest {

	// 7057
	// 7063
	// 5638

	public static final int WIDTH = 0, HEIGHT = 0;
	public static final String videoPath = "https://r19---sn-nwj7knls.googlevideo.com/videoplayback?upn=RCW_FM-Ps8U&source=youtube&fexp=900720%2C907263%2C930827%2C931346%2C932627%2C934954%2C936104%2C938028%2C9407021%2C9407115%2C9407577%2C9407775%2C9408347%2C9408468%2C9408708%2C947233%2C948124%2C948703%2C951703%2C952612%2C952637%2C957201%2C961404%2C961406&initcwndbps=1900000&itag=22&mime=video%2Fmp4&key=yt5&ratebypass=yes&expire=1429515175&ipbits=0&dur=426.829&sver=3&mm=31&signature=801541D67595EBFE249EA91BE71359503501AC7E.269C1430D39E90A1E531EFAE58AD4327E56277CF&id=o-AIdXMJcoG8oQsrSeKTrTlNTMlfpxaExDxUPo7g2D2VnP&pl=19&mt=1429493553&sparams=dur%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Cmime%2Cmm%2Cms%2Cmv%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&mv=m&ms=au&ip=198.0.203.1&requiressl=yes&title=Minecraft%3A%20Video%20Maps%20Revisited&cpn=sGtR08rqE1BQByRo";
	private JFrame frame = new JFrame("libNativeVideo.so - DBGPlayer");
	private final JPanel panel = new JPanel(){

		@Override
		 public void paintComponent(Graphics g){
			synchronized(handler.image){
				g.drawImage(handler.image, 0, 0, frame.getWidth(), frame.getHeight(), null);
			}
		 }

	};
	private DebugCallbackHandler handler = new DebugCallbackHandler(this, 1280, 720);
	private NativeVideo video;

	public static void main(String[] args) throws Exception {
		// Debugging purposes.
		System.load("/home/tsunko/Gunvarrel/ImgMap-rw/src/main/cplusplus/libNativeVideo.so");
		NativeVideo.initialize(DebugCallbackHandler.class);

		final NativeVideoTest test = new NativeVideoTest();
		test.showGUI();
		test.startNativeThread();
	}

	public NativeVideoTest() throws IOException {
		video = new NativeVideo(handler, WIDTH, HEIGHT);
	}

	public void startNativeThread() throws InterruptedException {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					video.open(videoPath);
				} catch (IOException e){
					e.printStackTrace();
				}

				while(true){
					video.read();

//					try{
//						Thread.sleep(33, 367000);
//					} catch (InterruptedException e){
//						e.printStackTrace();
//					}
				}
			}
		}).start();

		while(video.isStreaming()){
			synchronized(this){
				wait();
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					panel.repaint();
				}
			});
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