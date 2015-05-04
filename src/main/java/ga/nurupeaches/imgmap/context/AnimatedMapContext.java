package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

public class AnimatedMapContext extends WatchableContext implements SingleMapContext {

	private MapView view;
	private Thread nativeThread;
	private boolean streaming = false;
	private short id;
	private NativeVideo video;

	public AnimatedMapContext(short id){
		this.id = id;
		view = Bukkit.getMap(id);
	}

	@Override
	public void startThreads(){
		streaming = true;

		nativeThread = new Thread(new Runnable(){
			public void run(){
				// Guarantees that we execute this at LEAST once.
				do {
					video.read();
					try{
						Thread.sleep(33);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				} while (streaming);
			}
		});
		nativeThread.start();
	}

	@Override
	public void stopThreads(){
		streaming = false;
		video.close();
		nativeThread.stop();
	}

	@Override
	public NativeVideo getVideo(){
		return video;
	}

	@Override
	public short getId(){
		return id;
	}

	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		if(streaming){
			stopThreads();
		}

		video = new NativeVideo(this, 128, 128);
		try{
			video.open(source);
		} catch (IOException e){
			e.printStackTrace();
		}

		MapUtils.clearRenderers(view);
		history.add(source);
	}

	@Override
	public void update(Object... params){
		if(params.length < 1){
			return;
		}

		MapPacket packet = Adapter.convertImageToPackets(id, (BufferedImage)params[0]);
		for(UUID uuid : viewers){
			packet.send(Bukkit.getPlayer(uuid));
		}
	}

}