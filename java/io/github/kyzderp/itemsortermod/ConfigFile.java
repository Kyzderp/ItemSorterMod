package io.github.kyzderp.itemsortermod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class ConfigFile 
{
	private final File path = new File(Minecraft.getMinecraft().mcDataDir, "liteconfig" + File.separator + "config.1.8" + File.separator + "ItemSorterPresets.txt");

	public HashMap<String, String> presets;
	
	public ConfigFile()
	{
		this.presets = new HashMap<String, String>();
		
		if (!path.exists())
		{
			if (!this.writeFile())
				LiteLoaderLogger.warning("Cannot write to ItemSorter file!");
			else
				LiteLoaderLogger.info("Created new ItemSorter presets file.");
		}
		if (!this.loadFile())
			LiteLoaderLogger.warning("Cannot read from ItemSorter file!");
		else
			LiteLoaderLogger.info("ItemSorter presets loaded.");
	}
	
	public boolean writeFile()
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(path);
		} catch (FileNotFoundException e) {
			return false;
		}
		writer.println("ores=152,331,14,15,16,21,22,173,41,42,56,57,73,129,133,264,263,265,266,388,371,351:4");
		writer.close();
		return true;
	}
	public boolean loadFile()
	{
		if (!path.exists())
			return false;
		this.presets.clear();
		Scanner scan;
		try {
			scan = new Scanner(path);
		} catch (FileNotFoundException e) {
			return false;
		}
		while (scan.hasNext())
		{
			String line = scan.nextLine();
			line = line.replaceAll(" ", "");
			String[] parts = line.split("=");
			if (parts.length != 2)
				LiteLoaderLogger.warning("Invalid format: " + line);
			else
			{
				this.presets.put(parts[0], parts[1]);
			}
		}
		scan.close();
		return true;
	}
	
}
