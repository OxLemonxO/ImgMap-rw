package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.ImgMapPlugin;
import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import ga.nurupeaches.imgmap.renderer.EmptyRenderer;
import ga.nurupeaches.imgmap.utils.IOHelper;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleAnimatedMapContext extends MassUpdateContext {

	private MapView view;
	private BufferedImage image;
	private NativeVideo video;
	private List<UUID> viewers = new ArrayList<UUID>();
	private Thread nativeThread;
	private boolean streaming = false;
	private short id;

	// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====
	private JFrame frame = new JFrame("ImgMapDbg");
	// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====


	public SimpleAnimatedMapContext(String videoSource, short id){
		this.id = id;
		view = Bukkit.getMap(id);
		video = new NativeVideo(this, videoSource);

		// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon()));
		frame.pack();
		frame.setVisible(true);
		// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====
	}

	public void addViewer(UUID uuid){
		viewers.add(uuid);
	}

	// ugly.
	public void startThreads(){
		streaming = true;

		nativeThread = new Thread(new Runnable() {
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

		nativeThread.start();
	}

	public void stopThreads(){
		nativeThread.stop();
	}

	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		MapUtils.clearRenderers(view);
		view.addRenderer(new EmptyRenderer());
		history.add(source);
	}

	@Override
	public void update(){}

	@Override
	public void massUpdate(Object... objs){
		image = IOHelper.resizeImage((BufferedImage)objs[0], 128, 128);
		MapPacket packet = Adapter.convertImageToPackets(id, image);
		for(UUID uuid : viewers){
			packet.send(Bukkit.getPlayer(uuid));
		}

		// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		frame.getContentPane().invalidate();
		// ==== PURELY FOR DEBUGGING PURPOSES - REMOVE WHEN DONE ====
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