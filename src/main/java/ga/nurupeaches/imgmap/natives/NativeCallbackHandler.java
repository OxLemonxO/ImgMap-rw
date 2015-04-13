package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class NativeCallbackHandler implements CallbackHandler {

	private byte[] rawImage;
	private BufferedImage image;
	private final Context context;

	public NativeCallbackHandler(Context context){
		this.context = context;
	}

	// Called by JNI
	@Override
	public void handleData(byte[] data){
		if(image == null){
			// TODO: HELP
			image = new BufferedImage(0, 0, BufferedImage.TYPE_3BYTE_BGR);
			rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		}

		System.arraycopy(data, 0, rawImage, 0, data.length);
		context.update(image);
	}

}