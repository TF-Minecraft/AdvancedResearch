package net.tfminecraft.advancedresearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Database implements Listener{
	public static List<RPlayer> loadedPlayers = new ArrayList<RPlayer>();
	private JSONObject json; // org.json.simple
    JSONParser parser = new JSONParser();
    ConfigLoader loader = new ConfigLoader();
    FileConfiguration config;
	public void setConfig(FileConfiguration c) {
		this.config = c;
	}
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		for(RPlayer rpl : loadedPlayers) {
			if(rpl.getPlayer().equals(e.getPlayer())) return;
		}
		loadPlayer(e.getPlayer());
	}
    @EventHandler 
    public void quitEvent(PlayerQuitEvent e){
    	Player p = e.getPlayer();
    	RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
    	if(loadedPlayers.contains(rp)) loadedPlayers.remove(rp);
    	savePlayer(rp);
    }
    public void loadPlayer(Player player) {
    	File file = new File("plugins/AdvancedResearch/PlayerData", player.getPlayer().getUniqueId() + ".json");
    	for(RPlayer rp : loadedPlayers) {
    		if(rp.getPlayer().equals(player)) loadedPlayers.remove(rp);
    	}
    	if(file.exists() == true) {
    		try {
				json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				RPlayer rp = new RPlayer();
				rp.setPlayer(player);
				rp.setMentalPoints((int) Math.round(((Double) json.get("mental points"))));
				if(((String) json.get("current station world")).equalsIgnoreCase("none")) {
					rp.setCurrentStation(null);
				} else {
					rp.setCurrentStation(new Location(Bukkit.getServer().getWorld((String) json.get("current station world")), (Double) json.get("current station x"),(Double) json.get("current station y"),(Double) json.get("current station z")));
				}
				loadedPlayers.add(rp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
    	} else{
    		RPlayer rp = new RPlayer();
    		rp.setPlayer(player);
    		rp.setCurrentStation(null);
    		rp.setMentalPoints(150);
    		loadedPlayers.add(rp);
    	}
    }
    public void deleteDatabase() {
    	File folder = new File("plugins/AdvancedResearch/Data");
    	for (final File file : folder.listFiles()) {
            if (!file.isDirectory()) {
            	file.delete();
            }
    	}
    }
    public void loadStations() {
		File folder = new File("plugins/AdvancedResearch/Data");
    	for (final File file : folder.listFiles()) {
            if (!file.isDirectory()) {
            	try {
    				json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    				RStation s = new RStation();
    				s.setCurrentProject(loader.getInputFromConfig(config, (String) json.get("current project")));
    				s.setLoc(new Location(Bukkit.getServer().getWorld((String) json.get("world")), (Double) json.get("xPos"),(Double) json.get("yPos"),(Double) json.get("zPos")));
    				s.setCompleted(Boolean.parseBoolean((String) json.get("completed")));
    				s.setOwner((String) json.get("owner"));
    				JSONArray cArray = (JSONArray) json.get("current elements");
    				List<String> cElements = new ArrayList<String>();
    				Integer i = 0;
    				while(i < cArray.size()) {
    					cElements.add(cArray.get(i).toString());
    					i++;
    				}
    				i = 0;
    				JSONArray nArray = (JSONArray) json.get("needed elements");
    				List<String> nElements = new ArrayList<String>();
    				while(i < nArray.size()) {
    					nElements.add(nArray.get(i).toString());
    					i++;
    				}
    				s.setCurrentElements(cElements);
    				s.setNeededElements(nElements);
    				ResearchEvents.stations.add(s);
    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}
            }
        }
	}
    @SuppressWarnings("unchecked")
	public void saveStations() {
    	deleteDatabase();
    	for(RStation s : ResearchEvents.stations) {
    		try {
    			UUID uuid = UUID. randomUUID();
    			String uuidAsString = uuid. toString();
    			File file = new File("plugins/AdvancedResearch/Data",uuidAsString+".json");
    			while(file.exists() == true) {
    				UUID newuuid = UUID.randomUUID();
        			uuidAsString = newuuid.toString();
        			file = new File("plugins/AdvancedResearch/Data",uuidAsString+".json");
    			}
    			file.createNewFile();
            	PrintWriter pw = new PrintWriter(file, "UTF-8");
            	pw.print("{");
            	pw.print("}");
            	pw.flush();
            	pw.close();
                HashMap<String, Object> defaults = new HashMap<String, Object>();
            	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            	defaults.put("world", s.getLoc().getWorld().toString().replace("CraftWorld{name=", "").replace("}", ""));
            	defaults.put("xPos", s.getLoc().getX());
            	defaults.put("yPos", s.getLoc().getY());
            	defaults.put("zPos", s.getLoc().getZ());
            	defaults.put("completed", s.getIsCompleted().toString());
            	defaults.put("result", s.getResult());
            	defaults.put("owner", s.getOwner());
            	defaults.put("current project", s.getCurrentProject().getId());
            	Integer i = 0;
            	List<String> cElements = s.getCurrentElements();
            	JSONArray cArray = new JSONArray();
            	while(i < cElements.size()) {
            		cArray.add(cElements.get(i));
            		i++;
            	}
            	i = 0;
            	List<String> nElements = s.getNeededElements();
            	JSONArray nArray = new JSONArray();
            	while(i < nElements.size()) {
            		nArray.add(nElements.get(i));
            		i++;
            	}
            	defaults.put("needed elements", nArray);
            	defaults.put("current elements", cArray);
            	save(file, defaults);
            } catch (Throwable ex) {
				ex.printStackTrace();
            }
    	}
    }

    public void savePlayer(RPlayer player) {
    	File file = new File("plugins/AdvancedResearch/PlayerData", player.getPlayer().getUniqueId() + ".json");
        saveFile(file, player);
    }
	public void saveFile(File file, RPlayer player) {
        try {
        	if(file.exists() == true) {
        		file.delete();
        	}
        	file.createNewFile();
        	PrintWriter pw = new PrintWriter(file, "UTF-8");
        	pw.print("{");
        	pw.print("}");
        	pw.flush();
        	pw.close();
            HashMap<String, Object> defaults = new HashMap<String, Object>();
        	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        	
        	defaults.put("username", player.getPlayer().getName());
        	defaults.put("mental points", player.getMentalPoints());
        	if(player.getCurrentStation() != null) {
        		defaults.put("current station world", player.getCurrentStation().getWorld().toString().replace("CraftWorld{name=", "").replace("}", ""));
        		defaults.put("current station x", player.getCurrentStation().getX());
        		defaults.put("current station y", player.getCurrentStation().getY());
        		defaults.put("current station z", player.getCurrentStation().getZ());
        	} else {
        		defaults.put("current station world", "none");
        		defaults.put("current station x", "0");
        		defaults.put("current station y", "0");
        		defaults.put("current station z", "0");
        	}
        	save(file, defaults);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean save(File file, HashMap<String, Object> defaults) {
      try {
    	  JSONObject toSave = new JSONObject();
      
        for (String s : defaults.keySet()) {
          Object o = defaults.get(s);
          if (o instanceof String) {
            toSave.put(s, getString(s, defaults));
          } else if (o instanceof Double) {
            toSave.put(s, getDouble(s, defaults));
          } else if (o instanceof Integer) {
            toSave.put(s, getInteger(s, defaults));
          } else if (o instanceof JSONObject) {
            toSave.put(s, getObject(s, defaults));
          } else if (o instanceof JSONArray) {
            toSave.put(s, getArray(s, defaults));
          }
        }
      
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(toSave);
      
       Gson g = new GsonBuilder().setPrettyPrinting().create();
       String prettyJsonString = g.toJson(treeMap);
      
        FileWriter fw = new FileWriter(file);
        fw.write(prettyJsonString);
        fw.flush();
        fw.close();
      
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    }
    
      public String getRawData(String key, HashMap<String, Object> defaults) {
        return json.containsKey(key) ? json.get(key).toString()
           : (defaults.containsKey(key) ? defaults.get(key).toString() : key);
      }
    
      // Keep the existing legacy text representation, formatting, and exact-string comparisons.
      @SuppressWarnings("deprecation")
      public String getString(String key, HashMap<String, Object> defaults) {
        return ChatColor.translateAlternateColorCodes('&', getRawData(key, defaults));
      }

      public boolean getBoolean(String key, HashMap<String, Object> defaults) {
        return Boolean.valueOf(getRawData(key, defaults));
      }

      public double getDouble(String key, HashMap<String, Object> defaults) {
        try {
          return Double.parseDouble(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }

      public double getInteger(String key, HashMap<String, Object> defaults) {
        try {
          return Integer.parseInt(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }
     
      public JSONObject getObject(String key, HashMap<String, Object> defaults) {
         return json.containsKey(key) ? (JSONObject) json.get(key)
           : (defaults.containsKey(key) ? (JSONObject) defaults.get(key) : new JSONObject());
      }
     
      public JSONArray getArray(String key, HashMap<String, Object> defaults) {
    	     return json.containsKey(key) ? (JSONArray) json.get(key)
    	       : (defaults.containsKey(key) ? (JSONArray) defaults.get(key) : new JSONArray());
      }
}
