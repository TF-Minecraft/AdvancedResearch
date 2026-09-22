package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RStation {
	public Location loc;
	public Input currentProject;
	public ResearchNote topNote;
	public ResearchNote middleNote;
	public ResearchNote bottomNote;
	public Boolean isCompleted;
	public String result;
	public String owner;
	public List<String> currentElements = new ArrayList<String>();
	public List<String> neededElements = new ArrayList<String>();
	public ResearchNote getTopNote() {
		return topNote;
	}
	public void setTopNote(ResearchNote topNote) {
		this.topNote = topNote;
	}
	public ResearchNote getMiddleNote() {
		return middleNote;
	}
	public void setMiddleNote(ResearchNote middleNote) {
		this.middleNote = middleNote;
	}
	public ResearchNote getBottomNote() {
		return bottomNote;
	}
	public void setBottomNote(ResearchNote lowNote) {
		this.bottomNote = lowNote;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Boolean getIsCompleted() {
		return isCompleted;
	}
	public void setCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	public List<String> getNeededElements() {
		return neededElements;
	}
	public void setNeededElements(List<String> neededElements) {
		this.neededElements = neededElements;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public Input getCurrentProject() {
		return currentProject;
	}
	public void setCurrentProject(Input proj) {
		this.currentProject = proj;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public List<String> getCurrentElements() {
		return currentElements;
	}
	public void setCurrentElements(List<String> currentElements) {
		this.currentElements = currentElements;
	}
}
