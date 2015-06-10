package ga.nurupeaches.imgmap.nms.v1_8_R3;

import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import net.minecraft.server.v1_8_R3.MapIcon;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapPacketImpl implements MapPacket {

	private static final List<MapIcon> DUMMY_LIST = Collections.unmodifiableList(new ArrayList<MapIcon>());
	private final short id;
	private PacketPlayOutMap packet;

	// new PacketPlayOutMap(map.getId(), map.getScale().getValue(), icons, data.buffer, 0, 0, 0, 0);
	MapPacketImpl(short id, byte[] data){
		this.id = id;

		if(Adapter.INJECTED){
			packet = new PacketPlayOutMapNoLoop(id, MapView.Scale.FARTHEST.getValue(), data, 0, 0, 128, 128);
		} else {
			packet = new PacketPlayOutMap(id, MapView.Scale.FARTHEST.getValue(), DUMMY_LIST, data, 0, 0, 128, 128);
		}
	}

	@Override
	public MapPacket setData(byte[] data){
		if(packet instanceof PacketPlayOutMapNoLoop){
			((PacketPlayOutMapNoLoop)packet).setH(data);
		}

		return this;
	}

	@Override
	public void send(Player player){
		if(!(player instanceof CraftPlayer)){
			throw new IllegalArgumentException("Player wasn't a CraftPlayer!");
		}

//		System.out.println("sending packet " + toString());
		((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.writeAndFlush(packet);
	}
	
	public String toString(){
		return "MapPacketImpl(1.8_R3){id=" + id + ",packet="+packet+"}";
	}

}