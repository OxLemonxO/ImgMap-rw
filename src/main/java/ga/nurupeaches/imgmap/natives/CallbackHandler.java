package ga.nurupeaches.imgmap.natives;

public interface CallbackHandler {

	/**
	 * Handles information passed from C++.
	 * @param instance The NativeVideo instance.
	 * @param data Data passed from C++
	 */
	public void handleData(byte[] data);

}
