package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.ImgMapPlugin;
import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.renderer.SingleImageRenderer;
import ga.nurupeaches.imgmap.utils.IOHelper;
import ga.nurupeaches.imgmap.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class MultiMapContext extends Context {

	private short[] ids;
	private int sizeX;
	private int sizeY;

	public MultiMapContext(){}

	public MultiMapContext(short[] ids, int sizeX, int sizeY){
		if((sizeX * sizeY) != ids.length){
			throw new IllegalArgumentException("Given IDs doesn't match the requirement for " + sizeX + "," + sizeY);
		}

		this.ids = ids;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public void updateSizes(int x, int y){
		this.sizeX = x;
		this.sizeY = y;
	}

	public void updateIds(short[] ids){
		this.ids = ids;
	}

	public short[] getIds(){
		return ids;
	}

	// The image is bigger than 128 for MultiMapContexts
	@Override
	public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		int canvasWidth = sizeX * 128;
		int canvasHeight = sizeY * 128;
		notifiable.sendMessage(ChatColor.YELLOW + "Got canvas size " + canvasWidth + "x" + canvasHeight);

		// Check if resizing is required.
		if(image.getWidth() != canvasWidth || image.getHeight() != canvasHeight){
			notifiable.sendMessage(ChatColor.YELLOW + "Had to resize to canvas.");
			image = IOHelper.resizeImage(image, canvasWidth, canvasHeight);
			notifiable.sendMessage(ChatColor.YELLOW + "Image is now " + image.getWidth() + "x" + image.getHeight());
		}

		MapView view;
		short id;
		for(int x=0; x < sizeX; x++){
			for(int y=0; y < sizeY; y++){
				id = ids[x + sizeX * y];
				view = Bukkit.getMap(id);
				if(view == null){
					view = Adapter.generateMap(Bukkit.getWorlds().get(0), id);
				}

				MapUtils.clearRenderers(view);
				view.addRenderer(new SingleImageRenderer(source, image.getSubimage(x * 128, y * 128, 128, 128)));
			}
		}

		history.add(source);
	}

	@Override
	public void update(){
		List<MapRenderer> renderers;
		MapRenderer first;

		for(short id : ids){
			renderers = Bukkit.getMap(id).getRenderers();
			first = renderers.get(0);
			if(renderers.size() > 0 && first instanceof SingleImageRenderer){
				for(Player player : Bukkit.getOnlinePlayers()){
					// Rapid calls to Player.sendMap(MapView) stalls the server trying to buffer RenderData.
					// This, right here, bypasses all of that and immediately ships data off to the client.
					Adapter.convertImageToPackets(id, ((SingleImageRenderer)first).getImage()).send(player);
				}
			}
		}
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {
		byte[] sourceBytes = getImageSource().getBytes(ImgMapPlugin.IO_CHARSET);

		stream.writeInt(ids.length);
		for(short id : ids){
			stream.writeShort(id);
		}

		stream.writeInt(sourceBytes.length);
		stream.write(sourceBytes);

		stream.writeInt(sizeX);
		stream.writeInt(sizeY);
	}

	@Override
	public void read(DataInputStream stream) throws IOException {
		ids = new short[stream.readInt()];
		for(int i=0; i < ids.length; i++){
			ids[i] = stream.readShort();
		}

		byte[] source = new byte[stream.readInt()];
		if(stream.read(source) != source.length){
			ImgMapPlugin.logger().log(Level.WARNING, "Non-matching bytes read to bytes expected!");
		}

		sizeX = stream.readInt();
		sizeY = stream.readInt();

		history.add(new String(source, ImgMapPlugin.IO_CHARSET));
	}

}