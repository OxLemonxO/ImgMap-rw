package ga.nurupeaches.imgmap.natives;

public class NativeVideo {

	private native void read(long pointer, Object callback);
	private native String getSource(long pointer);
	private native long newNativeVideo(String filepath);

	private final NativeCallbackHandler handler = new NativeCallbackHandler();
	private long pointer;

	public NativeVideo(String source){
		pointer = newNativeVideo(source);
	}

	public void read(){
		read(pointer, handler);
	}

	public String getSource(){
		String str = getSource(pointer);
		System.out.println(str);
		return str;
	}

	public long getPointer(){
		return pointer;
	}

}