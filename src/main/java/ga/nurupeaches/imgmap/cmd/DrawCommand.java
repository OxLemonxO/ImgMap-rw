package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.*;
import ga.nurupeaches.imgmap.utils.IOHelper;
import ga.nurupeaches.imgmap.utils.YTRegexHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;

/**
 * New implementation to replace DrawImageCommand and DrawVideoCommand.
 */
public class DrawCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command cannot be ran by non-players.");
			return true;
		}

		if(!command.testPermission(sender)) return false;

		ItemStack item = ((Player)sender).getItemInHand();

		// Verify we're holding a map
		if(item.getType() != Material.MAP){
			sender.sendMessage(ChatColor.RED + "You need to have a map in your hand to use this command!");
			return true;
		}

		short targetId = item.getDurability();
		Context context = Context.getContext(targetId);
		if(command.getName().equalsIgnoreCase("joinvideo")){
			if(context instanceof WatchableContext){
				((WatchableContext)context).addViewer(((Player)sender).getUniqueId());
			} else {
				sender.sendMessage(ChatColor.RED + "That isn't a video map.");
			}
			return true;
		} else {
			if(args.length < 1){
				sender.sendMessage(ChatColor.RED + "You have to provide a direct image link or direct video link or YouTube ID.");
			}

			short[] ids = null;
			int x = -1, y = -1;
			String targetURL = args[0];
			int mmIndex = getIndexOf(args, "-mm");
			if(mmIndex != -1){
				sender.sendMessage(ChatColor.YELLOW + "Multi-map mode is being used!");
				String[] params = args[mmIndex].split(":");
				try {
					x = Integer.parseInt(params[1]);
					y = Integer.parseInt(params[2]);
				} catch (NumberFormatException e){
					sender.sendMessage(ChatColor.RED + "Incorrect usage of \"-mm:x:y\"; x and y are both non-decimal numbers.");
					return true;
				} catch (ArrayIndexOutOfBoundsException e){
					sender.sendMessage(ChatColor.RED + "Incorrect usage of \"-mm:x:y\"; you need to provide both the amount of maps in the x axis and y axis.");
					return true;
				}

				ids = getIdsBetween(targetId, (short)(targetId + (x * y)));
			}

			Context.SenderNotifiable notifiable = new Context.SenderNotifiable(sender);
			if(command.getName().equalsIgnoreCase("drawimage")){
				BufferedImage image = IOHelper.fetchImage(targetURL);
				if(image == null){
					sender.sendMessage(ChatColor.RED + "Failed to retrieve image!");
					return true;
				}

				if(ids != null){
					if(!(context instanceof ImageMultiMapContext)){
						context = new ImageMultiMapContext(ids, x, y);
					}
				} else {
					if(!(context instanceof ImageMapContext)){
						context = new ImageMapContext(targetId);
					}
				}

				context.updateContent(notifiable, targetURL, image);
				context.update();
				Context.registerContext(context);
				return true;
			} else if(command.getName().equalsIgnoreCase("drawvideo")){
				if(getIndexOf(args, "-yt") != -1){
					targetURL = YTRegexHelper.getDirectLinks(targetURL).get(0); // At this point, targetURL is more an id.
				}

				if(ids != null){
					if(!(context instanceof AnimatedMultiMapContext)){
						context = new AnimatedMultiMapContext(ids, x, y);
					}
				} else {
					if(!(context instanceof AnimatedMapContext)){
						context = new AnimatedMapContext(targetId);
					}
				}

				WatchableContext casted = (WatchableContext)context;
				casted.clearViewers();
				casted.updateContent(notifiable, targetURL, null);
				casted.addViewer(((Player)sender).getUniqueId());
				casted.startThreads();
                Context.registerContext(casted);
				return true;
			} else if(command.getName().equalsIgnoreCase("lolgameboy")){
//                GameBoyContext gbc = new GameBoyContext(targetId, ((Player)sender));
//                Context.registerContext(gbc);
            }

			return false;
		}
	}

	private int getIndexOf(String[] arr, String str){
		for(int i=0; i < arr.length; i++)
			if(arr[i].startsWith(str)) return i;

		return -1;
	}

	private short[] getIdsBetween(short start, short end){
		short[] ids = new short[end-start];
		for(short i=start; i < end; i++){
			ids[i-start] = i;
		}
		return ids;
	}

}