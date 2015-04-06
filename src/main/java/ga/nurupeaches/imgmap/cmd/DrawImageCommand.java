package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.ImageMapContext;
import ga.nurupeaches.imgmap.context.ImageMultiMapContext;
import ga.nurupeaches.imgmap.utils.IOHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class DrawImageCommand extends CommandHandler {

	public DrawImageCommand(){
		super(1, true, "imgmap.command.drawimage", "/drawimage <url>");
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments){
		final Player player = (Player)sender; // Safe to cast since we check if they're non-player earlier.
		ItemStack stack = player.getItemInHand();
		if(stack == null || (stack.getType() != Material.MAP)){
			commandFailure(sender, "You must be holding a map to use this command!");
			return;
		}

		BufferedImage image;
		try {
			image = IOHelper.fetchImage(new URL(arguments[0]));
		} catch (IOException e){
			commandFailure(sender, "Failed to retrieve image!");
			e.printStackTrace();
			return;
		}

		Context context = Context.getContext(stack.getDurability());
		if(arguments.length > 1 && arguments[1].startsWith("-mm")){
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

			if(!(context instanceof ImageMultiMapContext)){
				context = new ImageMultiMapContext(ids, x, y);
			}

			ImageMultiMapContext casted = (ImageMultiMapContext)context;
			casted.updateSizes(x, y);
			casted.updateIds(ids);
		} else {
			if(!(context instanceof ImageMapContext)){
				context = new ImageMapContext(stack.getDurability());
			}
		}

		context.updateContent(new Context.Notifiable() {
			@Override
			public void sendMessage(String message) {
				player.sendMessage(message);
			}
		}, arguments[0], image);
		Context.registerContext(context);
		commandSuccess(sender, "Drawing " + arguments[0] + "...");
		context.update(); // Update the map for everyone now.
	}

}