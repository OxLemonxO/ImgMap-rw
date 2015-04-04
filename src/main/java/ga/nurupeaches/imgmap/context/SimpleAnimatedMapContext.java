package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.ImgMapPlugin;
import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.utils.IOHelper;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleAnimatedMapContext extends Context {

	private MapView view;
	private BufferedImage currentFrame;
	private List<UUID> viewers = new ArrayList<UUID>();
	private Thread nativeThread;
	private boolean streaming = false;
	private short id;
	private final NativeVideo video;

	public SimpleAnimatedMapContext(String videoSource, short id){
		this.id = id;
		view = Bukkit.getMap(id);
		video = new NativeVideo(this, videoSource);
	}

	public void addViewer(UUID uuid){
		viewers.add(uuid);
	}

	// ugly.
	public void startThreads(){
		streaming = true;

		nativeThread = new Thread(new Runnable(){
			public void run(){
				synchronized(video){
					// Guarantees that we execute this at LEAST once.
					do {
						video.read();
					} while (streaming);
				}
			}
		});
		nativeThread.start();
	}

	public void stopThreads(){
		synchronized(video){
			video.close();
		}
		nativeThread.stop();
	}

	// This isn't really meant to be exposed.
	public NativeVideo _video(){
		return video;
	}

	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		MapUtils.clearRenderers(view);
		history.add(source);
	}

	@Override
	public void update(Object... params){
		if(params.length < 1){
			return;
		}

		currentFrame = (BufferedImage)params[0];
		BufferedImage resized = IOHelper.resizeImage(currentFrame, 128, 128);
		MapPacket packet = Adapter.convertImageToPackets(id, resized);
		for(UUID uuid : viewers){
			packet.send(Bukkit.getPlayer(uuid));
		}
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {
		byte[] sourceBytes = getImageSource().getBytes(ImgMapPlugin.IO_CHARSET);

		stream.writeShort(view.getId());
		stream.writeInt(sourceBytes.length);
		stream.write(sourceBytes);
	}

	@Override
	public void read(DataInputStream stream) throws IOException {
		view = Bukkit.getMap(stream.readShort());

		byte[] source = new byte[stream.readInt()];
		if(stream.read(source) != source.length){
			ImgMapPlugin.logger().log(Level.WARNING, "Non-matching bytes read to bytes expected!");
		}

		history.add(new String(source, ImgMapPlugin.IO_CHARSET));
	}

}