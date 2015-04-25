package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

import java.awt.image.BufferedImage;

public class NativeCallbackHandler implements CallbackHandler {

	private final Context context;
	private final BufferedImage image;

	public NativeCallbackHandler(BufferedImage image, Context context){
		this.context = context;
		this.image = image;
	}

	// Called by JNI
	@Override
	public void handleData(){
		context.update(image);
	}

}