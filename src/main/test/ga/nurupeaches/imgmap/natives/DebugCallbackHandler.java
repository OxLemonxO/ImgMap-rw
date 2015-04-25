package ga.nurupeaches.imgmap.natives;

public class DebugCallbackHandler implements CallbackHandler {

	private NativeVideoTest test;

	public DebugCallbackHandler(NativeVideoTest test){
		this.test = test;
	}

	// Called by JNI
	@Override
	public void handleData(){
//		synchronized(test){
//			test.notify();
//		}
	}

}