package io.github.kyzderp.itemsortermod;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

/**
 * ChestSorter
 * @author Kyzeragon
 */
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="itemsortermod.json")
public class LiteModItemSorter implements Tickable, OutboundChatFilter
{
	///// FIELDS /////
	private final ConfigFile configFile = new ConfigFile();
	private final Commands commands = new Commands(this);
	private final ChestSorter chestSorter = new ChestSorter();

	private int grabCooldown;


	///// METHODS /////
	public LiteModItemSorter() {}

	@Override
	public String getName() { return "Item Sorter"; }

	@Override
	public String getVersion() { return "1.6.0"; }

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
		if (inGame && minecraft.player.openContainer != null
				&& !minecraft.player.openContainer.equals(minecraft.player.inventoryContainer))
		{
			if (this.grabCooldown < 5)
				this.grabCooldown++;
			if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && this.grabCooldown == 5)
			{
				this.getChestSorter().grab(minecraft.player.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F1) && this.grabCooldown == 5)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) // inventory only
					this.getChestSorter().dumpInventory(minecraft.player.openContainer, false, true);
				else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) // hotbar only
					this.getChestSorter().dumpInventory(minecraft.player.openContainer, true, false);
				else // all
					this.getChestSorter().dumpInventory(minecraft.player.openContainer, true, true);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_F3) && this.grabCooldown == 5)
			{
				this.getChestSorter().grabInventory(minecraft.player.openContainer);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindForward.getKeyCode()) 
					&& this.grabCooldown == 5)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) // inventory only
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							true, false, true);
				else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) // hotbar only
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							true, true, false);
				else // all
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							true, true, true);
				this.grabCooldown = 0;
			}
			else if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindLeft.getKeyCode()) 
					&& this.grabCooldown == 5)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) // inventory only
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							false, false, true);
				else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) // hotbar only
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							false, true, false);
				else // all
					this.getChestSorter().quickStackToContainer(minecraft.player.openContainer, 
							false, true, true);
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
			message = "\u00A78[\u00A72ItemSorter\u00A78] \u00A7a" + message;
		TextComponentString displayMessage = new TextComponentString(message);
		displayMessage.setStyle((new Style()).setColor(TextFormatting.GREEN));
		Minecraft.getMinecraft().player.sendMessage(displayMessage);
	}

	/**
	 * Logs the error message to the user
	 * @param message The error message to log
	 */
	public static void logError(String message)
	{
		TextComponentString displayMessage = new TextComponentString("\u00A78[\u00A74!\u00A78] \u00A7c" 
				+ message + " \u00A78[\u00A74!\u00A78]");
		displayMessage.setStyle((new Style()).setColor(TextFormatting.RED));
		Minecraft.getMinecraft().player.sendMessage(displayMessage);
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
