package ga.nurupeaches.imgmap.nms;

import org.bukkit.entity.Player;

public interface MapPacket {

	public void setData(byte[] data);

	public void send(Player player);

}