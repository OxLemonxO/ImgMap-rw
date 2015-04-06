package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.WatchableContext;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JoinVideoCommand extends CommandHandler {

	public JoinVideoCommand(){
		super(0, true, "imgmap.command.joinvideo", "/joinvideo");
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments){
		Player player = (Player)sender;
		ItemStack stack = player.getItemInHand();
		if(stack == null || (stack.getType() != Material.MAP)){
			commandFailure(sender, "You must be holding a map to use this command!");
			return;
		}

		Context context = Context.getContext(stack.getDurability());
		if(!(context instanceof WatchableContext)){
			commandFailure(sender, "That isn't a video map.");
			return;
		}

		((WatchableContext)context).addViewer(player.getUniqueId());
		commandSuccess(sender, "Joined video ID#" + stack.getDurability() + "!");
	}

}