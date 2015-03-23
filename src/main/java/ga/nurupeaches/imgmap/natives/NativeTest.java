package ga.nurupeaches.imgmap.natives;

public class NativeTest {

	public native String test(String str);

	static {
		System.load("/home/tsunko/Desktop/libNativeTest.so");
	}

	public static void main(String[] args){
		String str = new NativeTest().test("hi");
		System.out.println(str);
	}

}