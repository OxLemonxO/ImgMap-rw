package ga.nurupeaches.imgmap.renderer;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class EmptyRenderer extends MapRenderer {

	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) {}

	@Override
	public void initialize(MapView map) {}

}