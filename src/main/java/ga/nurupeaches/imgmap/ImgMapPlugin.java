package ga.nurupeaches.imgmap;

import org.bukkit.plugin.java.JavaPlugin;

public class ImgMapPlugin extends JavaPlugin {

    private static ImgMapPlugin SINGLETON;

    @Override
    public void onEnable(){
        SINGLETON = this;
    }

    @Override
    public void onDisable(){
        SINGLETON = null;
    }

    public static ImgMapPlugin getPlugin(){
        synchronized(ImgMapPlugin.class){
            return SINGLETON;
        }
    }

}