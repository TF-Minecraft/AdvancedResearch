package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;



public class ConfigLoader {
	public static List<Input> loadedInputs = new ArrayList<Input>();
	public static List<ResearchElement> loadedElements = new ArrayList<ResearchElement>();
	public static String highAmount;
	public static String mediumAmount;
	public static String lowAmount;
	
	public static String minorPotion;
	public static String majorPotion;
	
	public void loadConfig(FileConfiguration config) {
		highAmount = config.getString("high_values");
		mediumAmount = config.getString("medium_values");
		lowAmount = config.getString("low_values");
		
		minorPotion = config.getString("minor_focus");
		majorPotion = config.getString("major_focus");
		
		Set<String> set = config.getConfigurationSection("inputs").getKeys(false);

		List<String> list = new ArrayList<String>(set);
		
		for(String key : list) {
			loadedInputs.add(getInputFromConfig(config, key));
		}
		Set<String> Eset = config.getConfigurationSection("elements").getKeys(false);

		List<String> Elist = new ArrayList<String>(Eset);
		
		for(String key : Elist) {
			loadedElements.add(getElementFromConfig(config, key));
		}
	}
	
	public Input getInputFromConfig(FileConfiguration config, String key) {
		Input i = new Input();
		i.setId(key);
		i.setItem(config.getConfigurationSection("inputs."+key).getString("item"));
		i.setResults(config.getConfigurationSection("inputs."+key).getStringList("results"));
		i.setStation(config.getConfigurationSection("inputs."+key).getString("station"));
		i.setElements(config.getConfigurationSection("inputs."+key).getStringList("elements"));
		return i;
	}
	public ResearchElement getElementFromConfig(FileConfiguration config, String key) {
		ResearchElement e = new ResearchElement();
		e.setId(key);
		e.setName(config.getConfigurationSection("elements."+key).getString("name"));
		e.setMaterial(Material.valueOf(config.getConfigurationSection("elements."+key).getString("material").toUpperCase()));
		e.setModelData(Integer.parseInt(config.getConfigurationSection("elements."+key).getString("model_data")));
		e.setLore(config.getConfigurationSection("elements."+key).getStringList("lore"));
		return e;
	}
}
