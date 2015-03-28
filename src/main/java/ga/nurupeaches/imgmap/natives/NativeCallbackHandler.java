package ga.nurupeaches.imgmap.natives;

import java.util.Arrays;

public class NativeCallbackHandler {

	public void handleData(int[] data){
		System.out.println("holy crap we got data " + Arrays.toString(data));
	}

}