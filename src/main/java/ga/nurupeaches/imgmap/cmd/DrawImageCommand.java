package ga.nurupeaches.imgmap.cmd;

import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.MapContext;
import ga.nurupeaches.imgmap.context.MultiMapContext;
import ga.nurupeaches.imgmap.utils.IOHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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
		if(arguments.length >= 4 && arguments[1].equalsIgnoreCase("-mm")){
			commandWarning(sender, "Multi-map mode is being used!");
			int xSize, ySize;

			try{
				xSize = Integer.parseInt(arguments[2]);
				ySize = Integer.parseInt(arguments[3]);
			} catch (NumberFormatException e){
				commandFailure(sender, "The given dimensions for canvas were not valid.");
				return;
			}

			List<Short> toUse = new LinkedList<Short>();
			for(int i=0; i < xSize * ySize; i++){
				toUse.add((short)(i + stack.getDurability()));
			}
			sender.sendMessage("using ids " + toUse.toString());

			if(context == null){
				context = new MultiMapContext(Context._conv(toUse), xSize, ySize);
			}

			if(context instanceof MultiMapContext){
				((MultiMapContext)context).updateSizes(xSize, ySize);
				((MultiMapContext)context).updateIds(Context._conv(toUse));
			}
		} else {
			if(context == null){
				context = new MapContext(stack.getDurability());
			}
		}

		context.updateContent(new Context.Notifiable() {
			@Override
			public void sendMessage(String message) {
				player.sendMessage(message);
			}
		}, arguments[0], image);
		context.update(); // Update the map for everyone now.
		Context.registerContext(context);
		commandSuccess(sender, "Drawing " + arguments[0] + "...");
	}

}