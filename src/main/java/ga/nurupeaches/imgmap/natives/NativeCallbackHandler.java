package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class NativeCallbackHandler {

	private final Context context;

	public NativeCallbackHandler(Context context){
		this.context = context;
	}

	// Called by JNI
	public void handleData(NativeVideo video, byte[] data){
		BufferedImage image = new BufferedImage(video.getWidth(), video.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, rawImage, 0, data.length);

		context.update(image);
	}

}