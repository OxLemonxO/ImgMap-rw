package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.AnimatedMapContext;
import ga.nurupeaches.imgmap.context.AnimatedMultiMapContext;
import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.WatchableContext;
import ga.nurupeaches.imgmap.utils.YTRegexHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class DrawVideoCommand extends CommandHandler {

	public DrawVideoCommand(){
		super(1, true, "imgmap.command.drawvideo", "/drawimage <url|youtube-id> [-yt]");
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments){
		final Player player = (Player)sender;
		ItemStack stack = player.getItemInHand();
		if(stack == null || (stack.getType() != Material.MAP)){
			commandFailure(sender, "You must be holding a map to use this command!");
			return;
		}

		String url = arguments[0];
		if(arguments.length > 1 && (arguments[1].equalsIgnoreCase("-yt") || arguments[1].startsWith("-ytmm"))){
			url = YTRegexHelper.getDirectLinks(arguments[0]).get(0);
		}

		Context context = Context.getContext(stack.getDurability());
		if(arguments[1].startsWith("-ytmm")){
			// -mm:x:y
			commandWarning(sender, "Multi-map mode is being used!");
			String[] params = arguments[1].split(":");
			int x, y;
			try{
				x = Integer.parseInt(params[1]);
				y = Integer.parseInt(params[2]);
			} catch (NumberFormatException e) {
				commandFailure(sender, "The given dimensions for canvas were not numbers.");
				return;
			}

			short[] ids = getIdsBetween(stack.getDurability(), (short)(stack.getDurability() + (x * y)));

			if(!(context instanceof AnimatedMultiMapContext)){
				context = new AnimatedMultiMapContext(ids, x, y);
			}

			AnimatedMultiMapContext newContext = (AnimatedMultiMapContext)context;
			if(newContext.getVideo() != null && newContext.getVideo().isStreaming()){
				newContext.stopThreads();
			}
			newContext.updateSizes(x, y);
			newContext.updateIds(ids);
		} else {
			if(!(context instanceof AnimatedMapContext)){
				context = new AnimatedMapContext(stack.getDurability());
			}
		}

		WatchableContext casted = (WatchableContext)context;
		casted.clearViewers();
		casted.updateContent(new Context.Notifiable() {
			@Override
			public void sendMessage(String message) {
				player.sendMessage(message);
			}
		}, url, null);
		Context.registerContext(casted);
		commandSuccess(sender, "Rendering " + url + "...");

		casted.addViewer(player.getUniqueId());
		casted.startThreads();
	}

}