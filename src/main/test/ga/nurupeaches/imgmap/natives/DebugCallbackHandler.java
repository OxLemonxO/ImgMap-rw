package ga.nurupeaches.imgmap.natives;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class DebugCallbackHandler extends NativeCallbackHandler {

	private byte[] rawImage;
	public BufferedImage image;

	public DebugCallbackHandler(){
		super(null);
	}

	// Called by JNI
	@Override
	public void handleData(byte[] data){
		if(image == null){
			image = new BufferedImage(1280, 720, BufferedImage.TYPE_3BYTE_BGR);
			rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		}

		System.arraycopy(data, 0, rawImage, 0, rawImage.length);
	}

}