package ga.nurupeaches.imgmap.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public abstract class Adapter {

    private static final Color[] colors;
	private static final Adapter IMPL;

    private static final Map<Integer, Byte> CACHE = new ConcurrentHashMap<>();
    private static final Map<Short, AdapterBuffer> BUFFER_MAP = new ConcurrentHashMap<>();

    public static final boolean INJECTED;

	static {
		String name = Bukkit.getServer().getClass().getName(); // org.bukkit.craftbukkit.vX_X_XX.CraftServer
		String version = name.split("\\.")[3]; // vX_X_XX

		try{
			Class<?> implClass = Class.forName(Adapter.class.getPackage().getName() + "." + version + ".AdapterImpl");
			IMPL = (Adapter)implClass.newInstance();
            INJECTED = IMPL._injectPacket();

            colors = _stealColors();
		} catch (ClassNotFoundException e){
			throw new IllegalStateException("Failed to retrieve NMS adapter for version " + version);
		} catch (InstantiationException | IllegalAccessException | NoSuchFieldException e){
			throw new RuntimeException(e);
		}
	}

	public static MapPacket generatePacket(short id, byte[] data){
        return IMPL._generatePacket(id, data);
	}

    public static MapPacket convertImageToPackets(short id, BufferedImage image){
        AdapterBuffer buffer = BUFFER_MAP.get(id);
        if(buffer == null){
            buffer = new AdapterBuffer();
            BUFFER_MAP.put(id, buffer);
        }

        image.getRGB(0, 0, 128, 128, buffer.rgbBuffer, 0, 128);
        for(int i=0; i < buffer.dataBuffer.length; i++){
            buffer.dataBuffer[i] = getColor(buffer.rgbBuffer[i]);
        }

        return generatePacket(id, buffer.dataBuffer); // setData will no-op if we're not injected.
    }

    public static MapView generateMap(World world, short id){
        return IMPL._generateMap(world, id);
    }

    private static Color[] _stealColors() throws IllegalAccessException, NoSuchFieldException {
        Field field = MapPalette.class.getDeclaredField("colors");
        field.setAccessible(true);
        return (Color[])field.get(null);
    }


    private static double rmean, r, g, b, weightR, weightB;
    private static double getDistance(int rgb, Color color){
        rmean = (double)(((rgb >> 16) & 0xFF) + color.getRed()) / 2.0D;
        r = (double)((rgb >> 16) & 0xFF) - color.getRed();
        g = (double)((rgb >> 8) & 0xFF) - color.getGreen();
        b = (rgb & 0xFF) - color.getBlue();
        weightR = 2.0D + rmean / 256.0D;
        weightB = 2.0D + (255.0D - rmean) / 256.0D;
        return weightR * r * r + 4.0D * g * g + weightB * b * b;
    }

    private static byte getColor(int rgb){
        Byte ret = CACHE.get(rgb);

        if(ret == null) {
            int index = 0;
            double best = -1.0D;
            double dist;

            for (int i = 4; i < colors.length; ++i) {
                dist = getDistance(rgb, colors[i]);
                if (dist < best || best == -1.0D) {
                    best = dist;
                    index = i;
                }
            }

            ret = (byte) (index < 128 ? index : -129 + (index - 127));
            CACHE.put(rgb, ret);
        }

        return ret;
    }

	protected abstract MapPacket _generatePacket(short id, byte[] data);
    protected abstract MapView _generateMap(World world, short id);
    protected abstract boolean _injectPacket();

}