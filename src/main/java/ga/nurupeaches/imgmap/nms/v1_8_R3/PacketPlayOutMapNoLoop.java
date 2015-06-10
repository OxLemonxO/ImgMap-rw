package ga.nurupeaches.imgmap.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;

import java.io.IOException;

public class PacketPlayOutMapNoLoop extends PacketPlayOutMap {

	private int id;
	private byte viewByte;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private byte[] data;

	public PacketPlayOutMapNoLoop(int id, byte view, byte[] elements, int minX, int minY, int maxX, int maxY) {
		super();
		this.id = id;
		this.viewByte = view;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.data = elements;
	}

	public void setH(byte[] data){
		this.data = data;
	}

	@Override
	public void b(PacketDataSerializer serializer) throws IOException {
		serializer.b(id);
		serializer.writeByte(viewByte);
		serializer.b(0); // Write 0 since there isn't a MapIcon we use.

		serializer.writeByte(maxX);
		// skip maxX check
		serializer.writeByte(maxY);
		serializer.writeByte(minX);
		serializer.writeByte(minY);
		serializer.a(data);
	}

	@Override
	public void a(PacketListenerPlayOut listener) {
		listener.a(this);
	}

}