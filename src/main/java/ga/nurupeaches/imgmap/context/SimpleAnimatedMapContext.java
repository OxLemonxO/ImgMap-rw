package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.ImgMapPlugin;
import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.renderer.SingleImageRenderer;
import ga.nurupeaches.imgmap.utils.IOHelper;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleAnimatedMapContext extends MassUpdateContext {

	private MapView view;
	private BufferedImage image;
	private NativeVideo video;
	private List<UUID> viewers = new ArrayList<UUID>();
	private boolean streaming = false;

	public SimpleAnimatedMapContext(String videoSource, short id){
		view = Bukkit.getMap(id);
		video = new NativeVideo(this, videoSource);
	}

	public void addViewer(UUID uuid){
		viewers.add(uuid);
	}

	public void startThreads(){
		streaming = true;

		Thread nativeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(streaming){
					video.read();
					try{
						Thread.sleep(10);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		});

		Thread uiThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(streaming){
					SimpleAnimatedMapContext.this.update();
					try{
						Thread.sleep(50);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		});

		nativeThread.start();
		uiThread.start();
	}

	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		MapUtils.clearRenderers(view);

		view.addRenderer(new SingleImageRenderer(source, IOHelper.resizeImage(image)));
		history.add(source);
	}

	@Override
	public void update(){
		if(image == null){
			return;
		}

		Iterator<UUID> iter = viewers.iterator();
		UUID uuid;
		Player player;
		image = IOHelper.resizeImage(image, 128, 128);
		MapPacket packet = Adapter.convertImageToPackets(view.getId(), image);
		while(iter.hasNext()){
			uuid = iter.next();
			if((player = Bukkit.getPlayer(uuid)) == null){
				iter.remove();
				continue;
			}

			packet.send(player);
		}
	}

	@Override
	public void massUpdate(Object... objs){
		image = (BufferedImage)objs[0];
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