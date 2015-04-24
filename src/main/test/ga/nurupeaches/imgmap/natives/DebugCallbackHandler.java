package ga.nurupeaches.imgmap.natives;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class DebugCallbackHandler extends NativeCallbackHandler {

	public final BufferedImage image;
	private final byte[] rawImage;

	public DebugCallbackHandler(int x, int y){
		super(null);
		image = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);
		rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.out.println(rawImage.length);
	}

	// Called by JNI
	@Override
	public void handleData(byte[] data){
		if(data.length != rawImage.length){
			System.out.println("data.length="+data.length+";rawImage.length="+rawImage.length);
		}

		System.arraycopy(data, 0, rawImage, 0, data.length);
	}

}