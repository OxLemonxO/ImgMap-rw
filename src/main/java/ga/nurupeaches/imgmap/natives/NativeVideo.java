package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

import java.io.IOException;

public class NativeVideo {

	// These two methods should not be handled outside of NativeVideo
	public static native void initialize(Class<? extends CallbackHandler> klass);
	private native void _init(int width, int height);
	private native int _open(String source);

	public native void read(NativeCallbackHandler handler);
	public native boolean isStreaming();
	public native void close();

	private final NativeCallbackHandler handler;

	public NativeVideo(Context context, int width, int height){
		handler = new NativeCallbackHandler(context);
		_init(width, height);
	}

	// For when we debug this thing.
	protected NativeVideo(NativeCallbackHandler created, int width, int height){
		handler = created;
		_init(width, height);
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

}