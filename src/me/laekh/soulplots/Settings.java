package me.laekh.soulplots;

import java.io.File;

public class Settings {
	
	File starter;
	String plotPath;
	String chunkPath;
	
	public Settings() {
		plotPath = SoulPlots.getInstance().getDataFolder().getPath() + "/Plots/";
		chunkPath = SoulPlots.getInstance().getDataFolder().getPath() + "/Chunks/";
		File data = new File(SoulPlots.getInstance().getDataFolder().getPath() + "/");
		if(!data.exists())
			data.mkdir();
		File plotdata = new File(getPlotPath());
		if(!plotdata.exists())
			plotdata.mkdir();
		File chunkdata = new File(getChunkPath());
		if(!chunkdata.exists())
			chunkdata.mkdir();
		starter = new File(SoulPlots.getInstance().getDataFolder().getPath() + "/starter.schematic");
	}
	
	public String getPlotPath() {
		return plotPath;
	}
	
	public String getChunkPath() {
		return chunkPath;
	}
	
	public File getStarterHouse() {
		return starter;
	}
	
}
