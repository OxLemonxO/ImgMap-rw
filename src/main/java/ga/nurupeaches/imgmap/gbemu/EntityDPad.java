package ga.nurupeaches.imgmap.gbemu;

import ga.nurupeaches.imgmap.ImgMapPlugin;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

public class EntityDPad extends EntityHorse implements Listener {

    private boolean[] keypadMapping;
    private static final Field aY_f;

    static {
        try {
            aY_f = EntityLiving.class.getDeclaredField("aY");
            aY_f.setAccessible(true);
        } catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    public EntityDPad(World world){
        super(world);
        Bukkit.getPluginManager().registerEvents(this, ImgMapPlugin.getPlugin());
    }

    @Override
    public void die(){
        PlayerToggleSprintEvent.getHandlerList().unregister(this);
        super.die();
    }

    public void setKeypadMapping(boolean[] mapping){
        this.keypadMapping = mapping;
    }

    @Override
    public void g(float sideMotions, float forwardMotion) {
        // backward: forwardMotion < 0
        // forward: forwardMotion > 0
        // left: sideMotion > 0
        // right: sideMotion < 0
        if(this.passenger != null){
            sideMotions = ((EntityLiving) this.passenger).aZ * 0.5F;
            forwardMotion = ((EntityLiving) this.passenger).ba;
            System.out.println("sideMot=" + sideMotions);
            System.out.println("fowardMot=" + forwardMotion);
            if(keypadMapping == null) return;
            keypadMapping[0] = forwardMotion > 0F;
            keypadMapping[1] = forwardMotion < 0F;
            keypadMapping[2] = sideMotions > 0F;
            keypadMapping[3] = sideMotions < 0F;
            keypadMapping[4] = get_aY(this.passenger);
        } else {
            super.g(sideMotions, forwardMotion);
        }
    }

    public boolean get_aY(Entity entity) {
        try {
            return (Boolean)aY_f.get(entity);
        } catch (IllegalAccessException e){
            return false;
        }
    }

    public static final void injectEntity(){
        try {
            Field e_f = EntityTypes.class.getDeclaredField("e");
            e_f.setAccessible(true);
            Map<Integer, Class<? extends Entity>> mapping = (Map<Integer, Class<? extends Entity>>)e_f.get(null);
            mapping.remove(100);

            Method register_m = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            register_m.setAccessible(true);
            register_m.invoke(null, EntityDPad.class, "DPad-chan", 100);
        } catch (ReflectiveOperationException e){
            ImgMapPlugin.logger().log(Level.WARNING, "Failed to inject EntityDPad!", e);
        }
    }

    @EventHandler
    public void sprintMapToBButton(PlayerToggleSprintEvent e){
        if(((CraftEntity)e.getPlayer().getVehicle()).getHandle() == this){
           keypadMapping[5] = e.isSprinting();
        }
    }

}