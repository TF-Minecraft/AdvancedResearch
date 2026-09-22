package net.tfminecraft.advancedresearch;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;




public class ResearchMain extends JavaPlugin{
	FileConfiguration config = getConfig();
	
	public static ResearchMain plugin;
	ConfigLoader loader = new ConfigLoader();
	ResearchEvents events = new ResearchEvents();
	Database db = new Database();
	
	@Override
	public void onEnable(){
		plugin = this;
		db.setConfig(config);
		loader.loadConfig(config);
		getServer().getPluginManager().registerEvents(events, this);
		getServer().getPluginManager().registerEvents(db, this);
		db.loadStations();
		for(Player p : Bukkit.getOnlinePlayers()) {
			db.loadPlayer(p);
		}
		new BukkitRunnable()
		{
			public void run()
			   {
					for(RPlayer rp : Database.loadedPlayers) {
						if(rp.getMentalPoints() < 150) {
							Integer amount = rp.getMentalPoints()+10;
							if(amount > 150) amount = 150;
							rp.setMentalPoints(amount);
						}
					}	
			   }
		}.runTaskTimer(ResearchMain.plugin, 0L, 72000L);
		
	}
	
	public static RPlayer getRPlayerbyPlayer(Player p) {
		for(RPlayer r : Database.loadedPlayers) {
			if(r.getPlayer().equals(p)) return r;
		}
		System.out.println("§c[AdvancedResearch] ERROR: No Research Player found for the player "+p);
		return null;
	}
	@Override
	public void onDisable() {
		if (Database.loadedPlayers != null) {
			for (RPlayer p : Database.loadedPlayers) {
				db.savePlayer(p);
			}
		} else {
			getLogger().warning("Database.loadedPlayers was null during onDisable, skipping save.");
		}
		db.saveStations();
	}

}
