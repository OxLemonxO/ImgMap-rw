package ga.nurupeaches.imgmap.renderer;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.lang.ref.SoftReference;

public class SingleImageRenderer extends MapRenderer {

    private boolean hasDrawn = false;
    private Disposable<Image> disposableImage;
	private String source;

    public SingleImageRenderer(String source, Image image){
        disposableImage = new Disposable<Image>(image);
    	this.source = source;
	}

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if(!hasDrawn){
            hasDrawn = true;
            canvas.drawImage(0, 0, disposableImage.get());
        }
    }

	public String sauce(){
		return source;
	}

    private class Disposable<T> {

        private Object object;
        private boolean isDisposable = false;

        public Disposable(T object){
            this.object = object;
        }

        public T get(){
            if(isDisposable && object instanceof SoftReference){
                return (T)((SoftReference)object).get();
            } else {
                T old = (T)object;
                object = new SoftReference<T>(old);
                isDisposable = true;
                return old;
            }
        }

    }

}