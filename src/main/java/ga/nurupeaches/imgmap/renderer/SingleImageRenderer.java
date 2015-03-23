package ga.nurupeaches.imgmap.renderer;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class SingleImageRenderer extends MapRenderer {

    private boolean hasDrawn = false;
    private BufferedImage image;
	private String source;

    public SingleImageRenderer(String source, BufferedImage image){
        this.image = image;
    	this.source = source;
	}

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if(!hasDrawn){
            hasDrawn = true;
            canvas.drawImage(0, 0, image);
        }
    }

	public String sauce(){
		return source;
	}

	public BufferedImage getImage(){
		return image;
	}

}