package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.MultiMapContext;

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
			if(context instanceof MultiMapContext){
				image = new BufferedImage(((MultiMapContext)context).getSizes()[0], ((MultiMapContext)context).getSizes()[1], BufferedImage.TYPE_3BYTE_BGR);
			} else {
				image = new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR);
			}
			rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		}

		System.arraycopy(data, 0, rawImage, 0, data.length);
		context.update(image);
	}

}