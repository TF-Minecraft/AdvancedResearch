package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.ItemManager;

public class ResearchEvents implements Listener{
	public static List<RStation> stations = new ArrayList<>();
	public static HashMap<Player, Location> currentLoc = new HashMap<Player, Location>();
	public InventoryManager inv = new InventoryManager();
	@EventHandler
	public void focusPotionEvent(PlayerInteractEvent e) {
		if(!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
		Player p = e.getPlayer();
		ItemStack i = p.getInventory().getItemInMainHand();
		NBTItem nbt = NBTItem.get(i);
		if(nbt.hasType() == false) return;
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		if(nbt.getType().equalsIgnoreCase(ConfigLoader.minorPotion.split("\\.")[0]) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(ConfigLoader.minorPotion.split("\\.")[1])) {
			if(rp.getMentalPoints() >= 150) {
				p.sendMessage("§cAlready at max Mental Points!");
				return;
			}
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITCH_DRINK, 3f, 1f);
			Integer amount = rp.getMentalPoints()+20;
			if(amount > 150) amount = 150;
			p.sendMessage("§EYou now have §a"+amount+" §6Mental Points.");
			rp.setMentalPoints(amount);
		} else if(nbt.getType().equalsIgnoreCase(ConfigLoader.majorPotion.split("\\.")[0]) && nbt.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(ConfigLoader.majorPotion.split("\\.")[1])){
			if(rp.getMentalPoints() >= 150) {
				p.sendMessage("§cAlready at max Mental Points!");
				return;
			}
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITCH_DRINK, 3f, 1f);
			Integer amount = rp.getMentalPoints()+50;
			if(amount > 150) amount = 150;
			p.sendMessage("§EYou now have §a"+amount+" §6Mental Points.");
			rp.setMentalPoints(amount);
		}
	}
	@EventHandler
	public void startResearchEvent(PlayerInteractEvent e) {
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if(!e.getClickedBlock().getType().equals(Material.LECTERN)) return;
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		for(RStation rs : stations) {
			if(rs.getLoc().equals(e.getClickedBlock().getLocation())) {
				if(!rs.getOwner().equalsIgnoreCase(p.getUniqueId().toString())) {
					p.sendMessage("§cStation in use by someone else");
					return;
				}
				inv.MenuInventory(p, rs);
				currentLoc.put(p, e.getClickedBlock().getLocation());
				return;
			}
		}
		if(item.getType().equals(Material.AIR)) return;
		for(Input i : ConfigLoader.loadedInputs) {
			String type = i.getItem().split("\\.")[0];
			if(type.equalsIgnoreCase("m")) {
				NBTItem nbt = NBTItem.get(item);
				if(nbt.hasType() == false) return;
				String configPath = i.getItem().split("\\.")[1] + i.getItem().split("\\.")[2];
				String itemPath = nbt.getType() + nbt.getString("MMOITEMS_ITEM_ID");
				if(configPath.equalsIgnoreCase(itemPath)) {
					startResearch(p, e.getClickedBlock().getLocation(), i, item.getItemMeta().getDisplayName());
				}
			} else {
				String configPath = i.getItem().split("\\.")[1]; //ia.tfmc:abyssalite
				CustomStack stack = CustomStack.byItemStack(item);
				if(stack != null) {
					if(configPath.equalsIgnoreCase(stack.getNamespacedID())) {
						startResearch(p, e.getClickedBlock().getLocation(), i, item.getItemMeta().getDisplayName());
					}
				}
			}
		}
	}
	@EventHandler
	public void scrapEvent(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if(!e.getView().getTitle().equalsIgnoreCase(ChatColor.GRAY + "Confirm Scrap")) return;
		e.setCancelled(true);
		if(e.getCurrentItem() == null) return;
		if(!currentLoc.containsKey(p)) return;
		RStation rs = new RStation();
		for(RStation rst : stations) {
			if(rst.getLoc().equals(currentLoc.get(p))) rs = rst;
		}
		if(rs.getLoc() == null) return;
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		List<Integer> validSlots = Arrays.asList(3, 5);
		if(!validSlots.contains(e.getSlot())) return;
		if(e.getSlot() == 3) {
			p.sendMessage("§cProject scrapped.");
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 4f, 1f);
			p.closeInventory();
			rp.setCurrentStation(null);
			stations.remove(rs);
			return;
		} else {
			inv.MenuInventory(p, rs);
		}
	}
	@EventHandler
	public void onResearchEvent(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if(!e.getView().getTitle().equalsIgnoreCase(ChatColor.GRAY + "Research Station")) return;
		Inventory i = e.getClickedInventory();
		e.setCancelled(true);
		if(e.getCurrentItem() == null) return;
		if(!currentLoc.containsKey(p)) return;
		List<Integer> validSlots = Arrays.asList(16, 22, 25, 34, 40, 53);
		if(!validSlots.contains(e.getSlot())) return;
		RStation rs = new RStation();
		for(RStation rst : stations) {
			if(rst.getLoc().equals(currentLoc.get(p))) rs = rst;
		}
		if(rs.getLoc() == null) return;
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		p.getWorld().playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
		if(e.getSlot() == 53) {
			inv.confirmInventory(p, rs);
		}
		if(rs.isCompleted) {
			if(e.getSlot() != 22) {
				e.setCancelled(true);
				return;
			}
			giveResult(p, rs);
		}
		if(e.getSlot() == 16) {
			if(rp.getMentalPoints() < 1) {
				p.sendMessage("§cYou are too exhausted to research right now.");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				return;
			}
			doResearch(i, rp, rs, rs.getTopNote());
		} else if(e.getSlot() == 25) {
			if(rp.getMentalPoints() < 1) {
				p.sendMessage("§cYou are too exhausted to research right now.");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				return;
			}
			doResearch(i, rp, rs, rs.getMiddleNote());
		} else if(e.getSlot() == 34) {
			if(rp.getMentalPoints() < 1) {
				p.sendMessage("§cYou are too exhausted to research right now.");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				return;
			}
			doResearch(i, rp, rs, rs.getBottomNote());
		}
	}
	private void giveResult(Player p, RStation rs) {
		if(p.getInventory().firstEmpty() == -1) {
			p.sendMessage(ChatColor.RED + "You need at least 1 empty slot in you inventory!");	
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
			return;
		}
		String type = rs.getResult().split("\\.")[0]; //v.emerald
		if(type.equalsIgnoreCase("m")) {
			ItemManager itemManager = MMOItems.plugin.getItems();
			ItemStack item =  itemManager.getMMOItem(MMOItems.plugin.getTypes().get(rs.getResult().split("\\.")[1].toUpperCase()), rs.getResult().split("\\.")[2].toUpperCase()).newBuilder().build(); //m.material.salt
			p.getInventory().addItem(item);
		} else {
			CustomStack stack = CustomStack.getInstance(rs.getResult().split("\\.")[1]);
			if(stack != null) {
				ItemStack i = stack.getItemStack();
				p.getInventory().addItem(i);
			}
		}
		p.closeInventory();
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		if(rp == null) {
			for(RPlayer rpd : Database.loadedPlayers) {
				if(rpd.getPlayer().getUniqueId().toString().equalsIgnoreCase(rs.getOwner())) {
					rp = rpd;
				}
			}
		}
		rp.setCurrentStation(null);
		for(RStation rst : stations) {
			if(rst.getLoc().equals(rs.getLoc())) stations.remove(rs);
		}
	}
	public void doResearch(Inventory i, RPlayer rp, RStation rs, ResearchNote n) {
		for(String e : n.getElements()) {
			String type = e.split("\\.")[0];
			for(String se : rs.getCurrentElements()) {
				String eType = se.split("\\.")[0];
				if(type.equalsIgnoreCase(eType)) {
					Integer a = Integer.parseInt(se.split("\\.")[1]) + Integer.parseInt(e.split("\\.")[1]);
					rs.getCurrentElements().remove(se);
					if(a < 0) a = 0;
					se = eType+"."+a;
					rs.getCurrentElements().add(se);
					break;
				}
			}
		}
		if(checkAmounts(rs)) {
			rp.getPlayer().sendMessage("§dResearch Completed!");
			rs.setCompleted(true);
			setResult(i, rp.getPlayer(), rs);
		}
		rs.setTopNote(generateNote(rp.getPlayer(), rs));
		rs.setMiddleNote(generateNote(rp.getPlayer(), rs));
		rs.setBottomNote(generateNote(rp.getPlayer(), rs));
		rp.setMentalPoints(rp.getMentalPoints()-1);
		inv.UpdateInventory(i, rp.getPlayer(), rs);
	}
	private void setResult(Inventory i, Player p, RStation rs) {
		Collections.shuffle(rs.getCurrentProject().getResults());
		for(String s : rs.getCurrentProject().getResults()) {
			Double chance = Double.parseDouble(s.split("\\(")[1].replace(")", ""));
			if(Math.random() <= chance) {
				String path = s.split("\\(")[0];
				rs.setResult(path);
				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
				break;
			}
		}
		if(rs.getResult() == null) {
			rs.setResult(rs.getCurrentProject().getResults().get(0).split("\\(")[0]);
		}
		inv.completeResearch(i, p, rs, false);
	}
	public Boolean checkAmounts(RStation rs) {
		for(String n : rs.getNeededElements()) {
			String id = n.split("\\.")[0];
			Integer amount = Integer.parseInt(n.split("\\.")[1]);
			for(String c : rs.getCurrentElements()) {
				String cid = c.split("\\.")[0];
				Integer camount = Integer.parseInt(c.split("\\.")[1]);
				if(id.equalsIgnoreCase(cid)) {
					if(amount != camount) return false;
				}
			}
		}
		return true;
	}
	public Integer generateAmount() {
		if(Math.floor(Math.random()*3)+1 == 1) {
			Integer low = Integer.parseInt(ConfigLoader.highAmount.split("\\-")[0]);
			Integer high = Integer.parseInt(ConfigLoader.highAmount.split("\\-")[1]);
			return (int) ((int) 5*Math.round(Math.floor((Math.random()*high)+low)/5));
		}
		if(Math.floor(Math.random()*3)+1 == 2) {
			Integer low = Integer.parseInt(ConfigLoader.mediumAmount.split("\\-")[0]);
			Integer high = Integer.parseInt(ConfigLoader.mediumAmount.split("\\-")[1]);
			return (int) ((int) 5*Math.round(Math.floor((Math.random()*high)+low)/5));
		}
		Integer low = Integer.parseInt(ConfigLoader.lowAmount.split("\\-")[0]);
		Integer high = Integer.parseInt(ConfigLoader.lowAmount.split("\\-")[1]);
		return (int) ((int) 5*Math.round(Math.floor((Math.random()*high)+low)/5));
	}
	public ResearchNote generateNote(Player p, RStation s) {
		ResearchNote n = new ResearchNote();
		n.setId(UUID.randomUUID());
		int i = 0;

		while (n.getElements().size() < 1 && i < 20) {
			Collections.shuffle(s.getNeededElements());
			for (String elstr : s.getNeededElements()) {
				if (Math.random() < 0.25) { // 25% chance to include this element
					String element = elstr.split("\\.")[0];
					Integer needed = Integer.parseInt(elstr.split("\\.")[1]);

					Integer current = 0;
					for (String currStr : s.getCurrentElements()) {
						if (currStr.startsWith(element)) {
							current = Integer.parseInt(currStr.split("\\.")[1]);
							break;
						}
					}
					Integer intelligence = PlayerData.get(p).getAttributes().getInstance("intelligence").getBase();
					if (current.equals(needed)) {
						double skipChance = Math.min(intelligence * 0.03, 0.9); // up to 90% chance to skip fully completed elements
						if (Math.random() < skipChance) {
							continue; // skip this element
						}
					}

					Integer delta = needed - current;

					

					double smartChance = Math.min(intelligence * 0.02, 0.7);

					Integer amount;

					if (Math.random() < smartChance) {
						int maxHelpful = Math.min(Math.abs(delta), 20);
						int raw = (int)(Math.random() * maxHelpful + 5);
						amount = 5 * Math.round(raw / 5f);
						if (delta < 0) amount = -amount;
					} else {
						amount = (int)(5 * Math.round(Math.floor((Math.random() * 15) + 5) / 5));
						if (Math.random() < 0.5) amount = -amount;
					}

					n.getElements().add(element + "." + amount);
				}
			}
			i++;
		}

		return n;
	}

	
	public void startResearch(Player p, Location loc, Input i, String itemName) {
		p.sendMessage("§eStarted research of "+itemName);
		RPlayer r = ResearchMain.getRPlayerbyPlayer(p);
		if(r.getCurrentStation() != null) {
			p.sendMessage("§cYou already have a project!");
			return;
		}
		p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
		r.setCurrentStation(loc);
		RStation s = new RStation();
		s.setResult(null);
		s.setCompleted(false);
		s.setOwner(p.getUniqueId().toString());
		s.setCurrentProject(i);
		s.setLoc(loc);
		for(String elstr : i.getElements()) {
			s.getCurrentElements().add(elstr+".0");
			s.getNeededElements().add(elstr+"."+generateAmount());
		}
		s.setTopNote(generateNote(p, s));
		s.setMiddleNote(generateNote(p, s));
		s.setBottomNote(generateNote(p, s));
		stations.add(s);
	}
}
