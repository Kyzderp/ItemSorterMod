package io.github.kyzderp.itemsortermod;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ChestSorter 
{
	private Container container;
	private LinkedList<Integer> items;
	private LinkedList<Integer> meta;

	public ChestSorter()
	{
		this.setItems(new LinkedList<Integer>());
		this.setMeta(new LinkedList<Integer>());
	}

	/**
	 * Grab specified items
	 * @param container
	 */
	public void grab(Container container)
	{
		if (this.getItems().size() < 1)
			return;
		this.container = container;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Slot> slots = this.container.inventorySlots;
		for (int i = 0; i < slots.size() - 36; i++)
		{
			if (slots.get(i) != null && slots.get(i).getStack() != null)
			{
				int currID = Item.getIdFromItem(slots.get(i).getStack().getItem());
				int currMeta = slots.get(i).getStack().getItemDamage();
				for (int j = 0; j < this.getItems().size(); j++)
				{
					int id = this.getItems().get(j);
					int findMeta = this.getMeta().get(j);
					if (currID == id)
					{
						if (findMeta == -1 || findMeta == currMeta)
						{
							Minecraft.getMinecraft().playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, player);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Set the items to grab
	 * @param list
	 * @param isPreset
	 * @return
	 */
	public String setItems(String list, boolean isPreset)
	{
		String[] parsedList = list.split(",");
		String result = "";
		for (String item: parsedList)
		{
			String[] parts = item.split(":");
			if (parts[0].matches("[0-9]+") 
					&& Item.REGISTRY.getObjectById(Integer.parseInt(parts[0])) != null)
				this.items.addFirst(Integer.parseInt(parts[0]));
			else if (Item.getByNameOrId(parts[0]) != null)
				this.items.addFirst(Item.getIdFromItem(Item.getByNameOrId(parts[0])));
			else
			{
				LiteModItemSorter.logError("No such item ID/name/preset as \"" + item + "\".");
				return "";
			}
			///// metadata /////
			if (parts.length > 1 && parts[1].matches("[0-9]+"))
				this.getMeta().addFirst(Integer.parseInt(parts[1]));
			else
				this.getMeta().addFirst(-1);
			result += " " + (new ItemStack(Item.getItemById(this.items.get(0)))).getDisplayName() + ",";
		}
		if (isPreset)
			return result.substring(1, result.length() - 1);
		LiteModItemSorter.logMessage("Now grabbing items:" + result.substring(0, result.length() - 1) + ".", true);
		return "";
	}

	/**
	 * Dump everything into the container
	 * @param container
	 */
	public void dumpInventory(Container container, boolean hotbar, boolean inventory)
	{
		this.container = container;
		List<Slot> slots = this.container.inventorySlots;

		if (inventory)
		{
			for (int i = slots.size() - 36; i < slots.size() - 9; i++)
			{
				Minecraft.getMinecraft().playerController.windowClick(container.windowId, 
						i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().thePlayer);
			}
		}

		if (hotbar)
		{
			for (int i = slots.size() - 9; i < slots.size(); i++)
			{
				Minecraft.getMinecraft().playerController.windowClick(container.windowId, 
						i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().thePlayer);
			}
		}
	}

	/**
	 * Dump only things that are already in the container
	 * @param container
	 */
	public void quickStackToContainer(Container container, boolean meta, boolean hotbar, boolean inventory)
	{
		this.container = container;
		List<Slot> slots = this.container.inventorySlots;
		// Loop through player inventory portion
		int start = slots.size() - 36;
		if (!inventory)
			start += 27;
		int end = slots.size();
		if (!hotbar)
			end -= 9;
		for (int i = start; i < end; i++)
		{
			if (!slots.get(i).getHasStack()) // Empty slot
				continue;
			ItemStack lookFor = slots.get(i).getStack();
			for (int j = slots.size() - 37; j >= 0; j--)
			{
				if (!slots.get(j).getHasStack()) // Empty
					continue;
				if (lookFor.getItem().equals(slots.get(j).getStack().getItem()))
				{ // Found it
					if (meta && lookFor.getMetadata() != slots.get(j).getStack().getMetadata())
						continue; // Additional check for metadata

					Minecraft.getMinecraft().playerController.windowClick(container.windowId, 
							i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().thePlayer);
					break;
				}
			}
		}
	}

	/**
	 * Grab everything in the container
	 * @param container
	 */
	public void grabInventory(Container container)
	{
		this.container = container;
		List<Slot> slots = this.container.inventorySlots;
		for (int i = 0; i < slots.size() - 36; i++)
		{
			Minecraft.getMinecraft().playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().thePlayer);
		}
	}

	/**
	 * @return the items
	 */
	public LinkedList<Integer> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(LinkedList<Integer> items) {
		this.items = items;
	}

	/**
	 * @return the meta
	 */
	public LinkedList<Integer> getMeta() {
		return meta;
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(LinkedList<Integer> meta) {
		this.meta = meta;
	}


}
