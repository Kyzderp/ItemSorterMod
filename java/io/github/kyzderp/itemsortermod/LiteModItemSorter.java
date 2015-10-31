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
	private final ConfigFile configFile = new ConfigFile();
	private final Commands commands = new Commands(this);
	private final ChestSorter chestSorter = new ChestSorter(this);

	private int grabCooldown;


	///// METHODS /////
	public LiteModItemSorter() {}

	@Override
	public String getName() { return "Item Sorter"; }

	@Override
	public String getVersion() { return "1.2.0"; }

	@Override
	public void init(File configPath)
	{
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
				this.getChestSorter().grab(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F1) && this.grabCooldown == 5)
			{
				this.getChestSorter().dumpInventory(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F3) && this.grabCooldown == 5)
			{
				this.getChestSorter().grabInventory(minecraft.thePlayer.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_Q) && this.grabCooldown == 5)
			{
				this.getChestSorter().quickStackToContainer(minecraft.thePlayer.openContainer, false);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_W) && this.grabCooldown == 5)
			{
				this.getChestSorter().quickStackToContainer(minecraft.thePlayer.openContainer, true);
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
			this.commands.handleCommand(message);
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

	/**
	 * @return the chestSorter
	 */
	public ChestSorter getChestSorter() {
		return chestSorter;
	}

	/**
	 * @return the configFile
	 */
	public ConfigFile getConfigFile() {
		return configFile;
	}
}
