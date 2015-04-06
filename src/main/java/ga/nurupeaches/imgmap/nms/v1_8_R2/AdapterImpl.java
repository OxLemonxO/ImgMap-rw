package ga.nurupeaches.imgmap.nms.v1_8_R2;

import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterImpl extends Adapter {

	private static final List<MapIcon> DUMMY_LIST = Collections.unmodifiableList(new ArrayList<MapIcon>());

	@Override
	protected MapPacket _generatePacket(short id, byte[] data) {
		return new MapPacketImpl(id, data);
	}

	@Override
	protected MapView _generateMap(org.bukkit.World world, short id){
		World nmsWorld = ((CraftWorld)world).getHandle();
		String nmsInternalName = "map_" + id;
		WorldMap worldmap = new WorldMap(nmsInternalName);
		worldmap.scale = MapView.Scale.FARTHEST.getValue();
		int i = 128 * (1 << worldmap.scale);
		worldmap.centerX = Math.round((float) nmsWorld.getWorldData().c() / (float) i) * i;
		worldmap.centerZ = Math.round((float) (nmsWorld.getWorldData().e() / i)) * i;
		worldmap.map = (byte) nmsWorld.worldProvider.getDimension();
		worldmap.c();
		nmsWorld.a(nmsInternalName, worldmap);

		nmsWorld.worldMaps.a(nmsInternalName, worldmap);
		return worldmap.mapView;
	}

	public class MapPacketImpl implements MapPacket {

		private PacketPlayOutMap packet;

		// new PacketPlayOutMap(map.getId(), map.getScale().getValue(), icons, data.buffer, 0, 0, 0, 0);
		MapPacketImpl(short id, byte[] data){
			packet = new PacketPlayOutMap(id, MapView.Scale.FARTHEST.getValue(), DUMMY_LIST, data, 0, 0, 128, 128);
		}

		@Override
		public void setData(byte[] data){
//			packet.setH(data);
		}

		@Override
		public void send(Player player){
			if(!(player instanceof CraftPlayer)){
				throw new IllegalArgumentException("Player wasn't a CraftPlayer!");
			}

			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}

	}

	public class ConstructorSkipNMSMapPacket extends PacketPlayOutMap {

		private int a;
		private byte b;
		private int d;
		private int e;
		private int f;
		private int g;
		private byte[] h;

		public ConstructorSkipNMSMapPacket(int id, byte view, byte[] elements, int minX, int minY, int maxX, int maxY) {
			super();
			a = id;
			b = view;
			d = minX;
			e = minY;
			f = maxX;
			g = maxY;
			h = elements;
		}

		public void setH(byte[] data){
			this.h = data;
		}

		@Override
		public void b(PacketDataSerializer serializer) throws IOException {
			serializer.b(this.a);
			serializer.writeByte(this.b);
			serializer.b(0); // Write 0 since there isn't a MapIcon we use.

			serializer.writeByte(this.f);
			if(this.f > 0) {
				serializer.writeByte(this.g);
				serializer.writeByte(this.d);
				serializer.writeByte(this.e);
				serializer.a(this.h);
			}
		}

		@Override
		public void a(PacketListenerPlayOut listener) {
			listener.a(this);
		}

	}

}