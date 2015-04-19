package ga.nurupeaches.imgmap.natives;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class DummyNCH extends NativeCallbackHandler {

	public DummyNCH(){
		super(null);
	}

	@Override
	public void handleData(byte[] data){
		BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_3BYTE_BGR);
		byte[] rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, rawImage, 0, rawImage.length);

		try{
			ImageIO.write(image, "png", new File("hi.png"));
		} catch (IOException e){

		}
	}

}