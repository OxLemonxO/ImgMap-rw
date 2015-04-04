package ga.nurupeaches.imgmap.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public abstract class Adapter {

    private static final Color[] colors;
	private static final Adapter IMPL;

	static {

		String name = Bukkit.getServer().getClass().getName(); // org.bukkit.craftbukkit.vX_X_XX.CraftServer
		String version = name.split("\\.")[3]; // vX_X_XX

		try{
			Class<?> implClass = Class.forName(Adapter.class.getPackage().getName() + "." + version + ".AdapterImpl");
			IMPL = (Adapter)implClass.newInstance();

            colors = _stealColors();
		} catch (ClassNotFoundException e){
			throw new IllegalStateException("Failed to retrieve NMS adapter for version " + version);
		} catch (InstantiationException e){
			throw new RuntimeException(e);
		} catch (IllegalAccessException e){
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }
	}

	public static MapPacket generatePacket(short id, byte[] data){
		return IMPL._generatePacket(id, data);
	}

    public static MapPacket convertImageToPackets(short id, BufferedImage image){
        byte[] data = new byte[128 * 128];
        int[] imageRGB = new int[128 * 128];
        image.getRGB(0, 0, 128, 128, imageRGB, 0, 128);

        byte color;
        for(int i=0; i < data.length; i++){
            color = getColor(imageRGB[i]);
            if(color >= 0 || color <= -113) { // ???? Transparent colors.
                data[i] = color;
            }
        }

        return generatePacket(id, data);
    }

	public static MapView generateMap(World world, short id){
		return IMPL._generateMap(world, id);
	}

    private static Color[] _stealColors() throws IllegalAccessException, NoSuchFieldException {
        Field field = MapPalette.class.getDeclaredField("colors");
        field.setAccessible(true);
        return (Color[])field.get(null);
    }

    // Formula from:
    // http://www.compuphase.com/cmetric.htm
    // Same as Bukkit's, but this one is a more "direct" translation.
    private static double getDistance(int rgb, Color color){
        double rmean = (double)(((rgb >> 16) & 0xFF) + color.getRed()) / 2.0D;
        double r = (double)(((rgb >> 16) & 0xFF) - color.getRed());
        double g = (double)(((rgb >> 8) & 0xFF) - color.getGreen());
        int b = (rgb & 0xFF) - color.getBlue();
        double weightR = 2.0D + rmean / 256.0D;
        double weightG = 4.0D;
        double weightB = 2.0D + (255.0D - rmean) / 256.0D;
        return weightR * r * r + weightG * g * g + weightB * (double)b * (double)b;
    }

    private static byte getColor(int rgb){
        int index = 0;
        double best = -1.0D;
        double dist;

        for(int i = 4; i < colors.length; ++i) {
            dist = getDistance(rgb, colors[i]);
            if(dist < best || best == -1.0D) {
                best = dist;
                index = i;
            }
        }

        return (byte)(index < 128?index:-129 + (index - 127));
    }

	protected abstract MapPacket _generatePacket(short id, byte[] data);
	protected abstract MapView _generateMap(World world, short id);

}