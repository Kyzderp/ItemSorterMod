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
			String[] commands = {"/grab <item[,item2]> \u00A77- \u00A7fSpecify list of items to grab",
					"/grab held \u00A77- \u00A7fGrab currently held item regardless of metadata",
					"/grab meta \u00A77- \u00A7fGrab currently held item with specific metadata",
					"/grab presets \u00A77- \u00A7fShow the list of available presets",
					"/grab <preset> \u00A77- \u00A7fSpecify preset to grab",
					"/grab clear \u00A77- \u00A7fClear the list of items to grab",
					"/grab reload \u00A77- \u00A7fReloads .minecraft/liteconfig/common/ItemSorterPresets.txt",
					"<TAB> \u00A77- \u00A7fGrabs specified items when container opened",
					"<BACKWARD> \u00A77- \u00A7fDumps all inventory + hotbar items into open container",
					"<LSHIFT> + <BACKWARD> \u00A77- \u00A7fDumps all inventory items into open container",
					"<LCTRL> + <BACKWARD> \u00A77- \u00A7fDumps all hotbar items into open container",
					"<RIGHT> \u00A77- \u00A7fGrabs all of open container's items",
					"<FORWARD> \u00A77- \u00A7fQuickstacks inventory + hotbar, with metadata",
					"<LSHIFT> + <FORWARD> \u00A77- \u00A7fQuickstacks inventory only, with metadata",
					"<LCTRL> + <FORWARD> \u00A77- \u00A7fQuickstacks hotbar only, with metadata",
					"<LEFT> \u00A77- \u00A7fQuickstacks inventory + hotbar, regardless of metadata",
					"<LSHIFT> + <LEFT> \u00A77- \u00A7fQuickstacks inventory only, regardless of metadata",
					"<LCTRL> + <LEFT> \u00A77- \u00A7fQuickstacks hotbar only, regardless of metadata"
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
			if (Minecraft.getMinecraft().thePlayer.getHeldItemMainhand() == null
					|| Minecraft.getMinecraft().thePlayer.getHeldItemMainhand().getItem() == null)
			{
				LiteModItemSorter.logError("Stahp trying to grab air");
				return;
			}
			ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
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
