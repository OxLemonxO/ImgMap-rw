package ga.nurupeaches.imgmap.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public abstract class Adapter {

	private static final Adapter IMPL;

	static {
		String name = Bukkit.getServer().getClass().getName(); // org.bukkit.craftbukkit.vX_X_XX.CraftServer
		String version = name.split("\\.")[3]; // vX_X_XX

		try{
			Class<?> implClass = Class.forName(Adapter.class.getPackage().getName() + "." + version + ".AdapterImpl");
			IMPL = (Adapter)implClass.newInstance();
		} catch (ClassNotFoundException e){
			throw new IllegalStateException("Failed to retrieve NMS adapter for version " + version);
		} catch (InstantiationException e){
			throw new RuntimeException(e);
		} catch (IllegalAccessException e){
			throw new RuntimeException(e);
		}
	}

	public static MapPacket generatePacket(int id, byte[] data){
		return IMPL._generatePacket(id, data);
	}

	public static MapView generateMap(World world, short id){
		return IMPL._generateMap(world, id);
	}

	protected abstract MapPacket _generatePacket(int id, byte[] data);
	protected abstract MapView _generateMap(World world, short id);

}