package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.MassUpdateContext;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class NativeCallbackHandler {

	private final MassUpdateContext context;

	public NativeCallbackHandler(MassUpdateContext context){
		this.context = context;
	}

	public void handleData(NativeVideo video, byte[] data){
		BufferedImage image = wrapInImage(data, video.getWidth(), video.getHeight());
		context.massUpdate(image);
	}


	public static BufferedImage wrapInImage(byte[] imgData, int w, int h) {
		DataBuffer db = new DataBufferByte(imgData, w * h * 3);

		WritableRaster raster = WritableRaster.createInterleavedRaster(
				db, w, h, w * 3, 3, new int[]{0, 1, 2}, null);

		ColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[]{8,8,8}, false, false,
				Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);

		return new BufferedImage(colorModel, raster, false, null);
	}

}