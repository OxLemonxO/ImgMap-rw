package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;

// http://extremejava.tpk.com.br/2008/10/30/bufferedimage-and-bytebuffers/
public class NativeVideo {

	// These two methods should not be handled outside of NativeVideo
	public static native void initialize(Class<? extends CallbackHandler> klass);
    private native Object _init(int width, int height);
    private native int _open(String source);
	private native void read(CallbackHandler handler);

	public native boolean isStreaming();
	public native void close();

	private final BufferedImage frame;
	private final CallbackHandler handler;

	public NativeVideo(Context context, int width, int height){
        ByteBuffer buffer = (ByteBuffer)_init(width, height);
//		ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 3);

        frame = directBufferedImage(buffer, width, height);
		handler = new NativeCallbackHandler(frame, context);

	}

	// For when we debug this thing.
	protected NativeVideo(CallbackHandler created, int width, int height){
        ByteBuffer buffer = (ByteBuffer)_init(width, height);
//		ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 3);

        frame = directBufferedImage(buffer, width, height);
		handler = created;

	}

	public void read(){
		read(handler);
	}

	public void open(String source) throws IOException {
		int status = _open(source);
		switch(status){
			case 0:
				return; // 0 is okay!
			case 1:
				throw new IOException("Failed to open file for reading!");
			case 2:
				throw new MediaStreamException("No stream information found!");
			case 3:
				throw new MediaStreamException("No video stream found!");
			case 4:
				throw new CodecException("No codec found!");
			case 5:
				throw new CodecException("Failed to find or open a codec context!");
			case 6:
				throw new IOException("Failed to allocate proper AVFrames!");
			default:
				throw new IOException("Unknown error: " + status);
		}
	}

	public BufferedImage getFrame() {
		return frame;
	}

	private static BufferedImage directBufferedImage(final ByteBuffer buffer, int width, int height){
		DataBuffer dataBuffer = new DataBuffer(DataBuffer.TYPE_BYTE, buffer.limit()) {

			@Override
			public int getElem(int bank, int i) {
				return buffer.get(i);
			}

			@Override
			public void setElem(int bank, int i, int val){}

		};

		SampleModel sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, 3 * width, new int[]{0,1,2});
		WritableRaster raster = new WritableRaster(sm, dataBuffer, new Point()){};
		ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8},
                false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

		return new BufferedImage(colorModel, raster, false, null);
	}

}