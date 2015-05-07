package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class AnimatedMultiMapContext extends WatchableContext implements MultiMapContext {

	private Thread nativeThread;
	private boolean streaming = false;
	private short[] ids;
	private int sizeX, sizeY;
	private NativeVideo video;

	private List<Callable<MapPacket>> tasks;
	private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public AnimatedMultiMapContext(short[] ids, int sizeX, int sizeY){
		if((sizeX * sizeY) != ids.length){
			throw new IllegalArgumentException("Given IDs doesn't match the requirement for " + sizeX + "," + sizeY);
		}

		this.ids = ids;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.tasks = new ArrayList<>(sizeX*sizeY);
		System.out.println(Arrays.toString(ids) + ",x=" + sizeX + ",y=" + sizeY);
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
	public int[] getSizes() {
		return new int[]{ sizeX, sizeY };
	}

	@Override
	public void startThreads(){
		streaming = true;

		nativeThread = new Thread(new Runnable(){
			@Override
			public void run(){
					// Guarantees that we execute this at LEAST once.
				try {
					do {
						video.read();

						Thread.sleep(33);
					} while(streaming);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
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
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		if(streaming){
			stopThreads();
		}

		video = new NativeVideo(this, sizeX * 128, sizeY * 128);
		try{
			video.open(source);
		} catch (IOException e){
			e.printStackTrace();
		}

		MapView view;
		for(short id : ids){
			view = Bukkit.getMap(id);
			if(view == null){
				view = Adapter.generateMap(Bukkit.getWorlds().get(0), id);
			}

			MapUtils.clearRenderers(view);
		}

		history.add(source);
	}

	@Override
	public synchronized void update(Object... params){
		if(params.length < 1){
			return;
		}

		final BufferedImage image = (BufferedImage)params[0];
		short id;
		for(int x=0; x < sizeX; x++){
			for(int y=0; y < sizeY; y++){
				id = ids[x + sizeX * y];
				tasks.add(new GeneratePacketCallable(id, image, x, y));
			}
		}

		try{
			List<Future<MapPacket>> packets = service.invokeAll(tasks);
			for(Future<MapPacket> futurePacket : packets){
				for(UUID uuid : viewers){
					futurePacket.get().send(Bukkit.getPlayer(uuid));
				}
			}
			tasks.clear();
		} catch (InterruptedException | ExecutionException e){
			e.printStackTrace();
		}
	}

	private class GeneratePacketCallable implements Callable<MapPacket> {

		private short id;
		private BufferedImage image;
		private int x, y;

		public GeneratePacketCallable(short id, BufferedImage src, int x, int y){
			this.id = id;
			this.image = src;
			this.x = x;
			this.y = y;
		}

		@Override
		public MapPacket call() throws Exception {
			return Adapter.convertImageToPackets(id, image.getSubimage(x * 128, y * 128, 128, 128));
		}

	}

}