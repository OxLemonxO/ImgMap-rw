package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.ImgMapPlugin;
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
import java.util.logging.Level;

public class ImageMapContext extends Context {

    private short id;

	public ImageMapContext(short id){
		this.id = id;
	}

    public ImageMapContext(){}

	@Override
	public short getId(){
		return id;
	}

	@Override
    public void updateContent(Notifiable notifiable, String source, BufferedImage image){
		MapView view = Bukkit.getMap(id);
		MapUtils.clearRenderers(view);

		view.addRenderer(new SingleImageRenderer(source, IOHelper.resizeImage(image)));
		history.add(source);
	}

	@Override
	public void update(Object... params){
		MapView view = Bukkit.getMap(id);
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMap(view);
		}
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {
		byte[] sourceBytes = getImageSource().getBytes(ImgMapPlugin.IO_CHARSET);

		stream.writeShort(id);
		stream.writeInt(sourceBytes.length);
		stream.write(sourceBytes);
	}

	@Override
	public void read(DataInputStream stream) throws IOException {
		id = stream.readShort();

		byte[] source = new byte[stream.readInt()];
		if(stream.read(source) != source.length){
			ImgMapPlugin.logger().log(Level.WARNING, "Non-matching bytes read to bytes expected!");
		}

		history.add(new String(source, ImgMapPlugin.IO_CHARSET));
	}


}