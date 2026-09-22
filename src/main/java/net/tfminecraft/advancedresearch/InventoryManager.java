package net.tfminecraft.advancedresearch;

import net.tfminecraft.advancedresearch.util.LegacyModelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.lone.itemsadder.api.CustomStack;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.ItemManager;

public class InventoryManager {
	public List<Integer> elementSlots = Arrays.asList(10, 19, 28, 37);
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void MenuInventory(Player player, RStation rs) {
		Inventory i = ResearchMain.plugin.getServer().createInventory(null, 54, ChatColor.GRAY + "Research Station");
		Integer counter = 0;
		Collections.sort(rs.getNeededElements());
		for(String e : rs.getNeededElements()) {
			for(ResearchElement re : ConfigLoader.loadedElements) {
				if(re.getId().equalsIgnoreCase(e.split("\\.")[0])) {
					ItemStack eItem = new ItemStack(re.getMaterial(), 1);
					ItemMeta m = eItem.getItemMeta();
					LegacyModelData.set(m, re.getModelData());
					m.setDisplayName(re.getName());
					List<String> lore = new ArrayList<String>();
					for(String ce : rs.getCurrentElements()) {
						if(ce.split("\\.")[0].equalsIgnoreCase(e.split("\\.")[0])) {
							lore.add("§7Current amount: §e"+ce.split("\\.")[1]);
						}
					}
					lore.add("§7Needed amount: §e"+e.split("\\.")[1]);
					lore.add(" ");
					for(String l : re.getLore()) {
						lore.add(l);
					}
					m.setLore(lore);
					eItem.setItemMeta(m);
					i.setItem(elementSlots.get(counter), eItem);
					counter++;
				}
			}
		}
		ItemStack cancelItem = new ItemStack(Material.BARRIER, 1);
		ItemMeta cm = cancelItem.getItemMeta();
		cm.setDisplayName("§cScrap");
		List<String> cLore = new ArrayList<String>();
		cLore.add("§4WARNING! §cThe item will not be refunded!");
		cm.setLore(cLore);
		cancelItem.setItemMeta(cm);
		i.setItem(53, cancelItem);
		ItemStack mentalPoints = new ItemStack(Material.LIGHT, 1);
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(player);
		ItemMeta mentalM = mentalPoints.getItemMeta();
		mentalM.setDisplayName("§7Mental Points: §6"+rp.getMentalPoints());
		mentalPoints.setItemMeta(mentalM);
		i.setItem(45, mentalPoints);
		i.setItem(13, getItemsAdderFiller("mcicons:icon_down_blue"));
		i.setItem(15, getItemsAdderFiller("mcicons:icon_back_orange"));
		i.setItem(24, getItemsAdderFiller("mcicons:icon_back_orange"));
		i.setItem(33, getItemsAdderFiller("mcicons:icon_back_orange"));
		ResearchEvents events = new ResearchEvents();
		if(rs.getTopNote() == null) {
			ResearchNote n = events.generateNote(player, rs);
			rs.setTopNote(n);
			i.setItem(16, getResearchNote(n));
		} else {
			i.setItem(16, getResearchNote(rs.getTopNote()));
		}
		if(rs.getMiddleNote() == null) {
			ResearchNote n = events.generateNote(player, rs);
			rs.setMiddleNote(n);
			i.setItem(25, getResearchNote(n));
		} else {
			i.setItem(25, getResearchNote(rs.getMiddleNote()));
		}
		if(rs.getBottomNote() == null) {
			ResearchNote n = events.generateNote(player, rs);
			rs.setBottomNote(n);
			i.setItem(34, getResearchNote(n));
		} else {
			i.setItem(34, getResearchNote(rs.getBottomNote()));
		}
		String type = rs.getCurrentProject().getItem().split("\\.")[0]; //v.emerald
		if(type.equalsIgnoreCase("m")) {
			i.setItem(4, getMMOItem(rs.getCurrentProject().getItem()));
		} else {
			i.setItem(4, getItemsAdderItem(rs.getCurrentProject().getItem()));
		}
		Integer slot = 0;
		while(slot < i.getSize()) {
			if(i.getItem(slot) == null) {
				if(slot != 40) {
					if(slot != 22) {
						ItemStack fill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
						ItemMeta fm = fill.getItemMeta();
						fm.setDisplayName("§8 ");
						fill.setItemMeta(fm);
						i.setItem(slot, fill);
					}
				}
			}
			slot++;
		}
		player.openInventory(i);
		if(rs.isCompleted) {
			completeResearch(i, rp.getPlayer(), rs, false);
			return;
		}
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void confirmInventory(Player player, RStation rs) {
		Inventory i = ResearchMain.plugin.getServer().createInventory(null, 9, ChatColor.GRAY + "Confirm Scrap");
		ItemStack yesItem = new ItemStack(Material.GREEN_CONCRETE, 1);
		ItemMeta ym = yesItem.getItemMeta();
		ym.setDisplayName("§2YES");
		yesItem.setItemMeta(ym);
		ItemStack noItem = new ItemStack(Material.RED_CONCRETE, 1);
		ItemMeta nm = noItem.getItemMeta();
		nm.setDisplayName("§cNO");
		noItem.setItemMeta(nm);
		i.setItem(3, yesItem);
		i.setItem(5, noItem);
		Integer slot = 0;
		while(slot < i.getSize()) {
			if(i.getItem(slot) == null) {
				ItemStack fill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
				ItemMeta fm = fill.getItemMeta();
				fm.setDisplayName("§8 ");
				fill.setItemMeta(fm);
				i.setItem(slot, fill);
			}
			slot++;
		}
		player.openInventory(i);
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void UpdateInventory(Inventory i, Player p, RStation rs) {
		Integer counter = 0;
		Collections.sort(rs.getNeededElements());
		for(String e : rs.getNeededElements()) {
			for(ResearchElement re : ConfigLoader.loadedElements) {
				if(re.getId().equalsIgnoreCase(e.split("\\.")[0])) {
					ItemStack eItem = new ItemStack(re.getMaterial(), 1);
					ItemMeta m = eItem.getItemMeta();
					LegacyModelData.set(m, re.getModelData());
					m.setDisplayName(re.getName());
					List<String> lore = new ArrayList<String>();
					for(String ce : rs.getCurrentElements()) {
						if(ce.split("\\.")[0].equalsIgnoreCase(e.split("\\.")[0])) {
							lore.add("§7Current amount: §e"+ce.split("\\.")[1]);
						}
					}
					lore.add("§7Needed amount: §e"+e.split("\\.")[1]);
					lore.add(" ");
					for(String l : re.getLore()) {
						lore.add(l);
					}
					m.setLore(lore);
					eItem.setItemMeta(m);
					i.setItem(elementSlots.get(counter), eItem);
					counter++;
				}
			}
		}
		ItemStack mentalPoints = new ItemStack(Material.LIGHT, 1);
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		ItemMeta mentalM = mentalPoints.getItemMeta();
		mentalM.setDisplayName("§7Mental Points: §6"+rp.getMentalPoints());
		mentalPoints.setItemMeta(mentalM);
		i.setItem(45, mentalPoints);
		i.setItem(16, getResearchNote(rs.getTopNote()));
		i.setItem(25, getResearchNote(rs.getMiddleNote()));
		i.setItem(34, getResearchNote(rs.getBottomNote()));
		p.updateInventory();
	}
	
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public ItemStack getItemsAdderFiller(String path) {
		CustomStack stack = CustomStack.getInstance(path);
		if(stack != null) {
			ItemStack i = stack.getItemStack();
			ItemMeta m = i.getItemMeta();
			m.setDisplayName("§8 ");
			i.setItemMeta(m);
			return i;
		}
		return null;
	}
	public ItemStack getItemsAdderItem(String path) {
		CustomStack stack = CustomStack.getInstance(path);
		if(stack != null) {
			ItemStack i = stack.getItemStack();
			return i;
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public ItemStack getMMOItem(String path) {
		ItemStack item =  MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(path.split("\\.")[1].toUpperCase()), path.split("\\.")[2].toUpperCase()).newBuilder().build(); //m.material.salt
		return item;
	}
	
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public ItemStack getResearchNote(ResearchNote n) {
		ItemStack i = new ItemStack(Material.PAPER, 1);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("§6Research Note");
		List<String> lore = new ArrayList<String>();
		for(String s : n.getElements()) {
			if(Integer.parseInt(s.split("\\.")[1]) < 0) {
				lore.add("§7"+s.split("\\.")[0]+": §c"+s.split("\\.")[1]);
			} else {
				lore.add("§7"+s.split("\\.")[0]+": §2+"+s.split("\\.")[1]);
			}
		}
		m.setLore(lore);
		i.setItemMeta(m);
		return i;
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void completeResearch(Inventory i, Player p, RStation rs, Boolean open) {
		Integer counter = 0;
		Collections.sort(rs.getNeededElements());
		for(String e : rs.getNeededElements()) {
			for(ResearchElement re : ConfigLoader.loadedElements) {
				if(re.getId().equalsIgnoreCase(e.split("\\.")[0])) {
					ItemStack eItem = new ItemStack(re.getMaterial(), 1);
					ItemMeta m = eItem.getItemMeta();
					LegacyModelData.set(m, re.getModelData());
					m.setDisplayName(re.getName());
					List<String> lore = new ArrayList<String>();
					for(String ce : rs.getCurrentElements()) {
						if(ce.split("\\.")[0].equalsIgnoreCase(e.split("\\.")[0])) {
							lore.add("§7Current amount: §e"+ce.split("\\.")[1]);
						}
					}
					lore.add("§7Needed amount: §e"+e.split("\\.")[1]);
					lore.add(" ");
					for(String l : re.getLore()) {
						lore.add(l);
					}
					m.setLore(lore);
					eItem.setItemMeta(m);
					i.setItem(elementSlots.get(counter), eItem);
					counter++;
				}
			}
		}
		ItemStack mentalPoints = new ItemStack(Material.LIGHT, 1);
		RPlayer rp = ResearchMain.getRPlayerbyPlayer(p);
		ItemMeta mentalM = mentalPoints.getItemMeta();
		mentalM.setDisplayName("§7Mental Points: §6"+rp.getMentalPoints());
		mentalPoints.setItemMeta(mentalM);
		i.setItem(45, mentalPoints);
		i.setItem(16, new ItemStack(Material.AIR, 1));
		i.setItem(25, new ItemStack(Material.AIR, 1));
		i.setItem(33, new ItemStack(Material.AIR, 1));
		i.setItem(4, new ItemStack(Material.AIR, 1));
		i.setItem(40, new ItemStack(Material.AIR, 1));
		String type = rs.getResult().split("\\.")[0]; //v.emerald
		if(type.equalsIgnoreCase("m")) {
			i.setItem(22, getMMOItem(rs.getResult()));
		} else {
			i.setItem(22, getItemsAdderItem(rs.getResult()));
		}
		if(open) {
			p.openInventory(i);
		} else {
			p.updateInventory();
		}
	}
}
