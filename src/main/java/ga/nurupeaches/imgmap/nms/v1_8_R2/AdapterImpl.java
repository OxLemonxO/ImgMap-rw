package ga.nurupeaches.imgmap.nms.v1_8_R2;

import com.google.common.collect.BiMap;
import ga.nurupeaches.imgmap.ImgMapPlugin;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.nms.ProxyBiMap;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AdapterImpl extends Adapter {

	public static final int CACHE_SIZE = 50;
	private Map<Short, MapPacket> cache = new HashMap<Short, MapPacket>(CACHE_SIZE);

	@Override
	protected MapPacket _generatePacket(short id, byte[] data) {
		if(Adapter.INJECTED){
			MapPacket packet = cache.get(id);
			if(packet == null){
				cache.put(id, packet = new MapPacketImpl(id, data));
			}
			return packet.setData(data);
		} else {
			return new MapPacketImpl(id, data);
		}
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

	@Override
	public boolean _injectPacket(){
		try{
			addPacket(EnumProtocol.PLAY, PacketPlayOutMapNoLoop.class);
		} catch (Throwable t){
			ImgMapPlugin.logger().log(Level.WARNING, "Failed to inject", t);
			return false;
		}
		return true;
	}


	private static void addPacket(EnumProtocol protocol, Class<? extends Packet> packet) throws NoSuchFieldException, IllegalAccessException {
		Field packets = EnumProtocol.class.getDeclaredField("j");
		packets.setAccessible(true);
		Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet>>> pMap = (Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet>>>)packets.get(protocol);
		ProxyBiMap newPacketMap = new ProxyBiMap<Integer, Class<? extends Packet>>(pMap.get(EnumProtocolDirection.CLIENTBOUND));
		newPacketMap.addProxy(PacketPlayOutMap.class, PacketPlayOutMapNoLoop.class);
		pMap.put(EnumProtocolDirection.CLIENTBOUND, newPacketMap);

		Field map = EnumProtocol.class.getDeclaredField("h");
		map.setAccessible(true);
		Map<Class<? extends Packet>, EnumProtocol> protocolMap = (Map)map.get(null);
		protocolMap.put(packet, protocol);
	}

}