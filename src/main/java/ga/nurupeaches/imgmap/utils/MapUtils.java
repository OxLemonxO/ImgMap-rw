package ga.nurupeaches.imgmap.utils;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class MapUtils {

	private MapUtils(){}

	public static void clearRenderers(MapView view){
		// view.getRenderers() returns a new ArrayList containing the old renderers.
		for(MapRenderer renderer : view.getRenderers()){
			view.removeRenderer(renderer);
		}
	}

}