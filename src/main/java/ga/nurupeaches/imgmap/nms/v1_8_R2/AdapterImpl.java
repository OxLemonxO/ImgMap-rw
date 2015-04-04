package ga.nurupeaches.imgmap.nms.v1_8_R2;

import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import net.minecraft.server.v1_8_R2.MapIcon;
import net.minecraft.server.v1_8_R2.PacketPlayOutMap;
import net.minecraft.server.v1_8_R2.World;
import net.minecraft.server.v1_8_R2.WorldMap;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

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
			packet = new PacketPlayOutMap(id, MapView.Scale.FARTHEST.getValue(),
					DUMMY_LIST, data, 0, 0, 128, 128);
		}

		public void send(Player player){
			if(!(player instanceof CraftPlayer)){
				throw new IllegalArgumentException("Player wasn't a CraftPlayer!");
			}

			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}

	}

}