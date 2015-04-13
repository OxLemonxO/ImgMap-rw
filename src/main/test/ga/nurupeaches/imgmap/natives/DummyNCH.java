package ga.nurupeaches.imgmap.natives;

public class DummyNCH extends NativeCallbackHandler {

	public DummyNCH(){
		super(null);
	}

//	@Override
//	public void handleData(NativeVideo video, byte[] data){
//		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_3BYTE_BGR);
//		byte[] rawImage = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
//		System.arraycopy(data, 0, rawImage, 0, rawImage.length);
//		System.out.println(image.getSource());
//		JFrame test = new JFrame("dbg");
//		test.add(new JLabel(new ImageIcon(image)));
//		test.setSize(128, 128);
//		test.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		test.show();
//	}

}