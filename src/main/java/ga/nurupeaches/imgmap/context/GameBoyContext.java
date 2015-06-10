package ga.nurupeaches.imgmap.context;

//import com.btinternet.davidwinchurch.GameBoyCPU;
//import ga.nurupeaches.imgmap.ImgMapPlugin;
//import ga.nurupeaches.imgmap.gbemu.EntityDPad;
//import ga.nurupeaches.imgmap.nms.Adapter;
//import ga.nurupeaches.imgmap.nms.MapPacket;
//import org.bukkit.Bukkit;
//import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
//import org.bukkit.entity.Horse;
//import org.bukkit.entity.Player;
//
//import java.awt.image.BufferedImage;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class GameBoyContext extends Context implements SingleMapContext {
//
//    private boolean[] keyMap ={false,false,false,false,false,false,false,false};
//    private GameBoyCPU cpu;
//    private short id;
//    private List<UUID> viewers = new ArrayList<>();
//
//    public GameBoyContext(short id, Player player){
//        cpu = new GameBoyCPU(keyMap, this);
//        cpu.fullReset();
//        cpu.startThread();
//        cpu.loadRom(new File(ImgMapPlugin.getPlugin().getDataFolder(), "red.gb").getAbsolutePath());
//
//        EntityDPad dpad = new EntityDPad(((CraftWorld)player.getLocation().getWorld()).getHandle());
//        dpad.teleportTo(player.getLocation(), false);
//        dpad.getBukkitEntity().setPassenger(player);
//        dpad.setKeypadMapping(keyMap);
//        ((Horse)dpad.getBukkitEntity()).setOwner(player);
//
//        this.viewers.add(player.getUniqueId());
//        this.id = id;
//    }
//
//    public void addViewer(UUID uuid){
//        viewers.add(uuid);
//    }
//
//    public void clearViewers(){
//        viewers.clear();
//    }
//
//    @Override
//    public void updateContent(Notifiable notifiable, String source, BufferedImage image) {
//
//    }
//
//    @Override
//    public void update(Object... params) {
//        if(params.length < 1){
//            return;
//        }
//
//        MapPacket packet = Adapter.convertImageToPackets(id, (BufferedImage) params[0]);
//        for(UUID uuid : viewers){
//            packet.send(Bukkit.getPlayer(uuid));
//        }
//    }
//
//    @Override
//    public short getId() {
//        return id;
//    }
//
//    public void write(DataOutputStream stream) throws IOException {}
//    public void read(DataInputStream stream) throws IOException {}
//
//}