package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class ResearchElement {
	public String id;
	public Material material;
	public Integer modelData;
	public String name;
	public List<String> lore = new ArrayList<String>();
	public Integer getModelData() {
		return modelData;
	}
	public void setModelData(Integer modelData) {
		this.modelData = modelData;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLore() {
		return lore;
	}
	public void setLore(List<String> lore) {
		this.lore = lore;
	}
}
