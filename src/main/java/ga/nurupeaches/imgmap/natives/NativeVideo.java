package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

public class NativeVideo {

	private native void read(long pointer, NativeVideo video, Object callback);
	private native String getSource(long pointer);
	private native long newNativeVideo(String filepath);
	private native int getWidth(long pointer);
	private native int getHeight(long pointer);
	private native void close(long pointer);

	private final NativeCallbackHandler handler;
	private long pointer;

	protected NativeVideo(NativeCallbackHandler handler, String source){
		this.handler = handler;
		pointer = newNativeVideo(source);
	}

	public NativeVideo(Context context, String source){
		handler = new NativeCallbackHandler(context);
		pointer = newNativeVideo(source);
	}

	public void read(){
		read(pointer, this, handler);
	}

	public String getSource(){
		String str = getSource(pointer);
		System.out.println(str);
		return str;
	}

	public long getPointer(){
		return pointer;
	}

	public int getWidth(){
		return getWidth(pointer);
	}

	public int getHeight(){
		return getHeight(pointer);
	}

	public void close(){
		close(pointer);
	}

}