package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.UUID;

public class AnimatedMultiMapContext extends WatchableContext implements MultiMapContext {

	private Thread nativeThread;
	private boolean streaming = false;
	private short[] ids;
	private int sizeX, sizeY;
	private NativeVideo video;

	public AnimatedMultiMapContext(short[] ids, int sizeX, int sizeY){
		if((sizeX * sizeY) != ids.length){
			throw new IllegalArgumentException("Given IDs doesn't match the requirement for " + sizeX + "," + sizeY);
		}

		this.ids = ids;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	public void updateSizes(int x, int y){
		this.sizeX = x;
		this.sizeY = y;
	}

	@Override
	public void updateIds(short[] ids){
		this.ids = ids;
	}

	@Override
	public short[] getIds(){
		return ids;
	}

	@Override
	public void startThreads(){
		streaming = true;

		nativeThread = new Thread(new Runnable(){
			@Override
			public void run(){
				// Guarantees that we execute this at LEAST once.
				do {
					video.read();
				} while (streaming);
			}
		});
		nativeThread.start();
	}

	@Override
	public void stopThreads(){
		streaming = false;
		try{
			nativeThread.join(); // Wait for this thread to die.
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		video.close();
	}

	@Override
	public NativeVideo getVideo(){
		return video;
	}

	@Override
	public short getId(){
		return ids[0];
	}

	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		video = new NativeVideo(this, source, sizeX * 128, sizeY * 128);

		for(short id : ids){
			MapUtils.clearRenderers(Bukkit.getMap(id));
			if(Bukkit.getMap(id) == null){
				Adapter.generateMap(Bukkit.getWorlds().get(0), id);
			}
		}

		history.add(source);
	}

	@Override
	public void update(Object... params){
		if(params.length < 1){
			return;
		}

		BufferedImage image = (BufferedImage)params[0];
		Iterator<UUID> uuids = viewers.iterator();
		MapPacket packet;
		Player player;
		UUID uuid;
		short id;
		for(int x=0; x < sizeX; x++){
			for(int y=0; y < sizeY; y++){
				id = ids[x + sizeX * y];
				packet = Adapter.convertImageToPackets(id, image.getSubimage(x * 128, y * 128, 128, 128));

				while(uuids.hasNext()){
					uuid = uuids.next();
					if((player = Bukkit.getPlayer(uuid)) == null){
						uuids.remove();
						continue;
					}

					packet.send(player);
				}
			}
		}
	}

}