package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.MapContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Map;


public class DrawImageCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments){
        if(arguments.length > 0){
            return false;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "You need to be a player to use this command.");
            return true;
        }

        Player player = (Player)sender;
        MapContext context = MapContext.getContext(player.getItemInHand().getDurability());
        context.updateContent(player, new Image() {
            @Override
            public int getWidth(ImageObserver observer) {
                return 0;
            }

            @Override
            public int getHeight(ImageObserver observer) {
                return 0;
            }

            @Override
            public ImageProducer getSource() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public Object getProperty(String name, ImageObserver observer) {
                return null;
            }
        });
    }

}