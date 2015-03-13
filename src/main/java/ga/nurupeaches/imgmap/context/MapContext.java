package ga.nurupeaches.imgmap.context;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MapContext {

    private static final Map<Short, MapContext> CONTEXT_LOOKUP = new HashMap<Short, MapContext>();
    private Map<UUID, String> history = new HashMap<UUID, String>();
    private final short id;

    private MapContext(short id){
        this.id = id;
    }

    public void updateContent(Player player, Image image){
        MapView view = Bukkit.getMap(id);


    }

    public static MapContext getContext(short id){
        synchronized (CONTEXT_LOOKUP) {
            MapContext context = CONTEXT_LOOKUP.get(id);

            if (context == null) {
                context = new MapContext(id);
                CONTEXT_LOOKUP.put(id, context);
            }

            return context;
        }
    }



}