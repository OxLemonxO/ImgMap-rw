package ga.nurupeaches.imgmap.natives;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class DummyNCH extends NativeCallbackHandler {

	public DummyNCH(){
		super(null);
	}

	@Override
	public void handleData(NativeVideo video, byte[] data){
		BufferedImage image = new BufferedImage(video.getWidth(), video.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, rawImage, 0, data.length);

		BufferedImage dummy = new BufferedImage(video.getWidth(), video.getHeight(), BufferedImage.TYPE_INT_RGB);

		for(int x=0; x < image.getWidth(); x++){
			for(int y=0; y < image.getHeight(); y++){
				dummy.setRGB(x, y, image.getRGB(x, y));
			}
		}

		JFrame test = new JFrame("dbg");
		test.add(new JLabel(new ImageIcon(dummy)));
		test.setSize(video.getWidth(), video.getHeight());
		test.show();
	}

}