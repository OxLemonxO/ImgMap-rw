package ga.nurupeaches.imgmap.nms.v1_8_R2;

import ga.nurupeaches.imgmap.nms.Adapter;
import ga.nurupeaches.imgmap.nms.MapPacket;
import net.minecraft.server.v1_8_R2.MapIcon;
import net.minecraft.server.v1_8_R2.PacketPlayOutMap;
import net.minecraft.server.v1_8_R2.World;
import net.minecraft.server.v1_8_R2.WorldMap;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.ArrayList;

public class AdapterImpl extends Adapter {

	@Override
	protected MapPacket _generatePacket(int id, byte[] data) {
		return new MapPacketImpl(id, data);
	}

	@Override
	protected MapView _generateMap(org.bukkit.World world, short id){
		World nmsWorld = ((CraftWorld)world).getHandle();
		String nmsInternalName = "map_" + id;
		WorldMap worldmap = new WorldMap(nmsInternalName);
		worldmap.scale = MapView.Scale.FARTHEST.getValue();
		int i = 128 * (1 << worldmap.scale);
		worldmap.centerX = Math.round((float) nmsWorld.getWorldData().c() / (float) i) * i;
		worldmap.centerZ = Math.round((float) (nmsWorld.getWorldData().e() / i)) * i;
		worldmap.map = (byte) nmsWorld.worldProvider.getDimension();
		worldmap.c();
		nmsWorld.a(nmsInternalName, worldmap);

		nmsWorld.worldMaps.a(nmsInternalName, worldmap);
		return worldmap.mapView;
	}

	public class MapPacketImpl implements MapPacket {

		private PacketPlayOutMap packet;

		// new PacketPlayOutMap(map.getId(), map.getScale().getValue(), icons, data.buffer, 0, 0, 0, 0);
		MapPacketImpl(int id, byte[] data){
			packet = new PacketPlayOutMap(id, MapView.Scale.FARTHEST.getValue(),
					new ArrayList<MapIcon>(), data, 0, 0, 0, 0);
		}

		public void send(Player player){
			if(!(player instanceof CraftPlayer)){
				throw new IllegalArgumentException("Player wasn't a CraftPlayer!");
			}

			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}

	}

//	public static class SelectableCollectionImpl extends PersistentCollection implements SelectableCollection {
//
//		private IDataManager b;
//		protected Map<String, PersistentBase> a;
//		private List<PersistentBase> c;
//		private Map<String, Short> d;
//
//		public SelectableCollectionImpl(){
//			super(null); // *grumblegrumble*
//			// If the passed param is null, b = null, and b() returns after d.clear().
//		}
//
//		// Prepare for stupidity galore!
//		public static SelectableCollectionImpl init(PersistentCollection pc){
//			IDataManager dataManager = (IDataManager)Reflection.fetchField(pc, "b");
//			Map<String, PersistentBase> stringToPB = (Map<String, PersistentBase>)Reflection.fetchField(pc, "a");
//			List<PersistentBase> pbList = (List<PersistentBase>)Reflection.fetchField(pc, "c");
//			// Create a cloned field since d is cleared.
//			Map<String, Short> stringToShort = new HashMap<String, Short>((Map<String, Short>)Reflection.fetchField(pc, "d"));
//
//			SelectableCollectionImpl soi = new SelectableCollectionImpl(); // Microsoft Sam is invading.
//			soi.a = stringToPB;
//			soi.b = dataManager;
//			soi.c = pbList;
//			soi.d = stringToShort;
//
//			return soi;
//		}
//
//		@Override
//		public void saveId(short id) {
//			a_reimpl(null, id);
//		}
//
//		// Reimplementation of a(String)
//		public short a_reimpl(String s, short targetId){
//			Short currId;
//			if(s != null){
//				if((currId = d.get(s)) == null){
//					currId = 0;
//				} else {
//					currId++;
//				}
//
//				d.put(s, currId);
//			} else {
//				currId = targetId;
//			}
//
//			if(b != null){
//				File file = b.getDataFile("idcounts");
//				if(file != null){
//					NBTTagCompound compound = new NBTTagCompound();
//					for(String key : d.keySet()){
//						compound.setShort(key, d.get(key));
//					}
//
//					try{
//						DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
//						NBTCompressedStreamTools.a(compound, (DataOutput)stream);
//						stream.close();
//					} catch (IOException e){
//						ImgMapPlugin.logger().log(Level.SEVERE, "Internal I/O error with NMS", e);
//					}
//				}
//
//				ImgMapPlugin.logger().log(Level.INFO, "wrote data");
//			}
//
//			return currId;
//		}
//
//		@Override
//		public int a(String s) {
//			return a_reimpl(s, Short.MIN_VALUE);
//		}
//	}

}