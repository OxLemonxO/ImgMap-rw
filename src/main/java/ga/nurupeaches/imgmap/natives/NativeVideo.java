package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.context.Context;

public class NativeVideo {

	private native void read(long pointer, NativeVideo video, Object callback);
	private native String getSource(long pointer);
	private native long newNativeVideo(String filepath, int width, int height);
	private native int getWidth(long pointer);
	private native int getHeight(long pointer);
	private native boolean isStreaming(long pointer);
	private native void close(long pointer);

	private final NativeCallbackHandler handler;
	private long pointer;

	public NativeVideo(Context context, String source, int width, int height){
		handler = new NativeCallbackHandler(context);
		pointer = newNativeVideo(source, width, height);
	}

	public NativeVideo(NativeCallbackHandler handler, String source, int width, int height){
		this.handler = handler;
		this.pointer = newNativeVideo(source, width, height);
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

	public boolean isStreaming(){
		return isStreaming(pointer);
	}

	public void close(){
		close(pointer);
	}

}