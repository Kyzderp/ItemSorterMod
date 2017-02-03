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
	private final File path = new File(Minecraft.getMinecraft().mcDataDir, "liteconfig" 
			+ File.separator + "common" + File.separator + "ItemSorterPresets.txt");

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
		writer.println("trash=261,268,269,270,367,287,262,271,290,295,375,394,358:0");
		writer.println("junk=78,69,70,72,77,96,107,65,143,147,148,281,53,67,109,126,128,134,135,136,139,163,164,102,160,171,44:5,44:3");
		writer.println("nostackjunk=355,324,333");
		writer.println("tooljunk=398,273,274,275,291,284,285,286,294,346,293,272");
		writer.println("ores=152,331,14,15,16,21,22,173,41,42,56,57,73,129,133,264,263,265,266,388,371,351:4");
		writer.println("food=260,282,297,319,320,322,349,350,354,357,360,363,364,365,366,391,392,393,400,296,361,362,86,103");
		writer.println("plants=6,18,31,32,37,38,39,40,81,106,111,161,175");
		writer.println("discs=2256,2257,2258,2259,2260,2261,2262,2263,2264,2265,2266,2267");
		writer.println("armor=298,299,300,301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317");
		writer.println("tools=256,257,258,277,278,279,292,267,276,283");
		writer.println("wood=5,17,162");
		writer.println("woodjunk=280,323,50,85");
		writer.println("bonez=352,351:15");
		writer.println("brewing=396,370,376,377,378,382,369,348,353,372");
		writer.println("redstonestuff=23,25,29,33,76,123,131,158,356,404,345,347");
		writer.println("ironstuff=259,359,154,145,325,380,330,101,27,66,328,327");
		writer.println("containers=84,130,116,379,61,58,54,146");
		writer.println("nether=87,88,89,112,113,44:6,114,405,49,121");
		writer.println("sglass=20,95");
		writer.println("clays=82,172,159,337,336,45,108,44:4");
		writer.println("mobloot=341,368,381,288,334,289,351:0");
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
