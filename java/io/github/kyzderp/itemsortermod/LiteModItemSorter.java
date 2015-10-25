package io.github.kyzderp.itemsortermod;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.OutboundChatListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

/**
 * ChestSorter
 * @author Kyzeragon
 */
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="staffderpsmod.json")
public class LiteModItemSorter implements Tickable, OutboundChatFilter
{
	///// FIELDS /////
	private ChestSorter chestSorter;
	private ConfigFile configFile;

	private int grabCooldown;


	///// METHODS /////
	public LiteModItemSorter() {}

	@Override
	public String getName() { return "Item Sorter"; }

	@Override
	public String getVersion() { return "1.1.0"; }

	@Override
	public void init(File configPath)
	{
		this.configFile = new ConfigFile();
		this.chestSorter = new ChestSorter(this.configFile);
		this.grabCooldown = 5;
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
	{
		if (inGame && minecraft.thePlayer.openContainer != null
				&& !minecraft.thePlayer.openContainer.equals(minecraft.thePlayer.inventoryContainer))
		{
			if (this.grabCooldown < 5)
				this.grabCooldown++;
			if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && this.grabCooldown == 5)
			{
				this.chestSorter.grab(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F1) && this.grabCooldown == 5)
			{
				this.chestSorter.dumpInventory(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F3) && this.grabCooldown == 5)
			{
				this.chestSorter.grabInventory(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
		}
	}

	@Override
	public boolean onSendChatMessage(String message)
	{
		String[] tokens = message.trim().split(" ");
		if (tokens[0].equalsIgnoreCase("/itemsorter") || tokens[0].equalsIgnoreCase("/grab"))
		{
			this.chestSorter.handleCommand(message);
			return false;
		}
		return true;
	}

	/**
	 * Logs the message to the user
	 * @param message The message to log
	 */
	public static void logMessage(String message, boolean addPrefix)
	{
		if (addPrefix)
			message = "§8[§2ItemSorter§8] §a" + message;
		ChatComponentText displayMessage = new ChatComponentText(message);
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.GREEN));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	public static void logError(String message)
	{
		ChatComponentText displayMessage = new ChatComponentText("§8[§4!§8] §c" + message + " §8[§4!§8]");
		displayMessage.setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.RED));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(displayMessage);
	}
}
