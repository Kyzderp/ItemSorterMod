package io.github.kyzderp.itemsortermod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ChestSorter 
{
	private Container container;
	private LinkedList<Integer> items;
	private LinkedList<Integer> meta;
	private ConfigFile configFile;
	
	public ChestSorter(ConfigFile configFile)
	{
		this.configFile = configFile;
		this.items = new LinkedList<Integer>();
		this.meta = new LinkedList<Integer>();
	}

	public void grab(Container container)
	{
		if (this.items.size() < 1)
			return;
		this.container = container;
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		List<Slot> slots = this.container.inventorySlots;
		for (int i = 0; i < slots.size() - 36; i++)
		{
			if (slots.get(i) != null && slots.get(i).getStack() != null)
			{
				int currID = Item.getIdFromItem(slots.get(i).getStack().getItem());
				int currMeta = slots.get(i).getStack().getItemDamage();
				Item item = slots.get(i).getStack().getItem();
				for (int j = 0; j < this.items.size(); j++)
				{
					int id = this.items.get(j);
					int findMeta = this.meta.get(j);
					if (currID == id)
					{
						if (findMeta == -1 || findMeta == currMeta)
						{
							Minecraft.getMinecraft().playerController.windowClick(container.windowId, i, 0, 1, player);
							break;
						}
					}
				}
			}
		}
	}

	public String setItems(String list, boolean isPreset)
	{
		String[] parsedList = list.split(",");
		String result = "";
		for (String item: parsedList)
		{
			String[] parts = item.split(":");
			if (parts[0].matches("[0-9]+") && Item.itemRegistry.containsID(Integer.parseInt(parts[0])))
				this.items.addFirst(Integer.parseInt(parts[0]));
			else if (Item.itemRegistry.containsKey(parts[0]))
				this.items.addFirst(Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject(parts[0])));
			else
			{
				LiteModItemSorter.logError("No such item ID/name/preset as \"" + item + "\".");
				return "";
			}
			///// metadata /////
			if (parts.length > 1 && parts[1].matches("[0-9]+"))
				this.meta.addFirst(Integer.parseInt(parts[1]));
			else
				this.meta.addFirst(-1);
			result += " " + (new ItemStack(Item.getItemById(this.items.get(0)))).getDisplayName() + ",";
		}
		if (isPreset)
			return result.substring(1, result.length() - 1);
		LiteModItemSorter.logMessage("Now grabbing items:" + result.substring(0, result.length() - 1) + ".", true);
		return "";
	}

	public void dumpInventory(Container container)
	{
		this.container = container;
		List<Slot> slots = this.container.inventorySlots;
		for (int i = slots.size() - 36; i < slots.size(); i++)
		{
			Minecraft.getMinecraft().playerController.windowClick(container.windowId, i, 0, 1, Minecraft.getMinecraft().thePlayer);
		}
	}
	
	public void grabInventory(Container container)
	{
		this.container = container;
		List<Slot> slots = this.container.inventorySlots;
		for (int i = 0; i < slots.size() - 36; i++)
		{
			Minecraft.getMinecraft().playerController.windowClick(container.windowId, i, 0, 1, Minecraft.getMinecraft().thePlayer);
		}
	}
	
	public void handleCommand(String message)
	{
		String[] tokens = message.split(" ");
		if (tokens.length < 2)
		{
			if (this.items.size() < 1)
			{
				LiteModItemSorter.logError("No items specified yet. Usage: /grab <item[,item2]>");
				return;
			}
			String result = "Currently grabbing:";
			for (Integer id: this.items)
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
					"<F1> - Dumps all inventory items into open container",
					"<F3> - Grabs all of open container's items"};
			for (String command: commands)
				LiteModItemSorter.logMessage(command, false);
		}
		else if (tokens.length == 2 && tokens[1].equalsIgnoreCase("reload"))
		{
			this.configFile.loadFile();
			LiteModItemSorter.logMessage("ItemSorter presets reloaded.", true);
		}
		else if (tokens.length == 2 && tokens[1].equalsIgnoreCase("clear"))
		{
			this.items.clear();
			this.meta.clear();
			LiteModItemSorter.logMessage("Cleared list of items to grab", true);
		}
		else if (tokens.length == 2 && tokens[1].matches("(held|meta)"))
		{
			if (Minecraft.getMinecraft().thePlayer.getHeldItem() == null
					|| Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == null)
			{
				LiteModItemSorter.logError("Stahp trying to grab air");
				return;
			}
			ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
			this.items.clear();
			this.meta.clear();
			this.items.addFirst(Item.getIdFromItem(heldItem.getItem()));
			if (tokens[1].equalsIgnoreCase("held"))
			{
				this.meta.addFirst(-1);
				LiteModItemSorter.logMessage("Grabbing " + heldItem.getDisplayName() + " with all metadata.", true);
			}
			else
			{
				this.meta.addFirst(heldItem.getItemDamage());
				LiteModItemSorter.logMessage("Grabbing " + heldItem.getDisplayName() + " with specific metadata.", true);
			}
		}
		else if (tokens.length == 2 && this.configFile.presets.containsKey(tokens[1].toLowerCase()))
		{
			this.items.clear();
			this.meta.clear();
			String result = this.setItems(this.configFile.presets.get(tokens[1]), true);
			LiteModItemSorter.logMessage("Now grabbing preset " + tokens[1] + " (" + result + ")", true);
		}
		else if (tokens.length == 2 && tokens[1].matches("presets?"))
		{
			String result = "Available presets:";
			for (String preset: this.configFile.presets.keySet())
				result += " " + preset + ",";
			LiteModItemSorter.logMessage(result.substring(0, result.length() - 1) + ".", true);
		}
		else if (tokens.length == 2)
		{
			this.items.clear();
			this.meta.clear();
			this.setItems(tokens[1], false);
		}
		else
		{
			LiteModItemSorter.logError("Invalid parameters! Usage: /grab <item[,item2]>");
			String result = "Available presets:";
			for (String preset: this.configFile.presets.keySet())
				result += " " + preset + ",";
			LiteModItemSorter.logMessage(result.substring(0, result.length() - 1) + ".", true);
		}
	}
}
