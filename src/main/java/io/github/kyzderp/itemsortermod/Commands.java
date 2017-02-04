package io.github.kyzderp.itemsortermod;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Commands 
{
	private LiteModItemSorter main;

	public Commands(LiteModItemSorter main)
	{
		this.main = main;		
	}

	/**
	 * TODO: refactor
	 * @param message
	 */
	public void handleCommand(String message)
	{
		String[] tokens = message.split(" ");
		if (tokens.length < 2)
		{
			if (this.main.getChestSorter().getItems().size() < 1)
			{
				LiteModItemSorter.logError("No items specified yet. Usage: /grab <item[,item2]>");
				return;
			}
			String result = "Currently grabbing:";
			for (Integer id: this.main.getChestSorter().getItems())
				result += " " + (new ItemStack(Item.getItemById(id))).getDisplayName() + ",";
			LiteModItemSorter.logMessage(result.substring(0, result.length() - 1) + ".", true);
		}
		else if (tokens.length == 2 && tokens[1].equalsIgnoreCase("help"))
		{
			LiteModItemSorter.logMessage("commands (alias /itemsorter) and keys:", true);
			String[] commands = {"/grab <item[,item2]> - Specify list of items to grab",
					"/grab held - Grab currently held item regardless of metadata",
					"/grab meta - Grab currently held item with specific metadata",
					"/grab presets - Show the list of available presets",
					"/grab <preset> - Specify preset to grab",
					"/grab clear - Clear the list of items to grab",
					"/grab reload - Reloads .minecraft/liteconfig/config.1.7.2/ItemSorterPresets.txt",
					"<TAB> - Grabs specified items when container opened",
					"<BACKWARD> - Dumps all inventory + hotbar items into open container",
					"<LSHIFT> + <BACKWARD> - Dumps all inventory items into open container",
					"<LCTRL> + <BACKWARD> - Dumps all hotbar items into open container",
					"<RIGHT> - Grabs all of open container's items",
					"<FORWARD> - Quickstacks inventory + hotbar, with metadata",
					"<LSHIFT> + <FORWARD> - Quickstacks inventory only, with metadata",
					"<LCTRL> + <FORWARD> - Quickstacks hotbar only, with metadata",
					"<LEFT> - Quickstacks inventory + hotbar, regardless of metadata",
					"<LSHIFT> + <LEFT> - Quickstacks inventory only, regardless of metadata",
					"<LCTRL> + <LEFT> - Quickstacks hotbar only, regardless of metadata"
			};
			for (String command: commands)
				LiteModItemSorter.logMessage(command, false);
		}
		else if (tokens.length == 2 && tokens[1].equalsIgnoreCase("reload"))
		{
			this.main.getConfigFile().loadFile();
			LiteModItemSorter.logMessage("ItemSorter presets reloaded.", true);
		}
		else if (tokens.length == 2 && tokens[1].equalsIgnoreCase("clear"))
		{
			this.main.getChestSorter().getItems().clear();
			this.main.getChestSorter().getMeta().clear();
			LiteModItemSorter.logMessage("Cleared list of items to grab", true);
		}
		else if (tokens.length == 2 && tokens[1].matches("(held|meta)"))
		{
			if (Minecraft.getMinecraft().player.getHeldItemMainhand() == null
					|| Minecraft.getMinecraft().player.getHeldItemMainhand().getUnlocalizedName().equals("tile.air"))
			{
				LiteModItemSorter.logError("Stahp trying to grab air");
				return;
			}
			ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
			this.main.getChestSorter().getItems().clear();
			this.main.getChestSorter().getMeta().clear();
			this.main.getChestSorter().getItems().addFirst(Item.getIdFromItem(heldItem.getItem()));
			if (tokens[1].equalsIgnoreCase("held"))
			{
				this.main.getChestSorter().getMeta().addFirst(-1);
				LiteModItemSorter.logMessage("Grabbing " + heldItem.getDisplayName() + " with all metadata.", true);
			}
			else
			{
				this.main.getChestSorter().getMeta().addFirst(heldItem.getItemDamage());
				LiteModItemSorter.logMessage("Grabbing " + heldItem.getDisplayName() + " with specific metadata.", true);
			}
		}
		else if (tokens.length == 2 && this.main.getConfigFile().presets.containsKey(tokens[1].toLowerCase()))
		{
			this.main.getChestSorter().getItems().clear();
			this.main.getChestSorter().getMeta().clear();
			String result = this.main.getChestSorter().setItems(this.main.getConfigFile().presets.get(tokens[1]), true);
			LiteModItemSorter.logMessage("Now grabbing preset " + tokens[1] + " (" + result + ")", true);
		}
		else if (tokens.length == 2 && tokens[1].matches("presets?"))
		{
			String result = "Available presets:";
			for (String preset: this.main.getConfigFile().presets.keySet())
				result += " " + preset + ",";
			LiteModItemSorter.logMessage(result.substring(0, result.length() - 1) + ".", true);
		}
		else if (tokens.length == 2)
		{
			this.main.getChestSorter().getItems().clear();
			this.main.getChestSorter().getMeta().clear();
			this.main.getChestSorter().setItems(tokens[1], false);
		}
		else
		{
			LiteModItemSorter.logError("Invalid parameters! Usage: /grab <item[,item2]>");
			String result = "Available presets:";
			for (String preset: this.main.getConfigFile().presets.keySet())
				result += " " + preset + ",";
			LiteModItemSorter.logMessage(result.substring(0, result.length() - 1) + ".", true);
		}
	}
}
