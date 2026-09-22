package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResearchNote {
	public UUID id;
	public List<String> elements = new ArrayList<String>();
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public List<String> getElements() {
		return elements;
	}
	public void setElements(List<String> elements) {
		this.elements = elements;
	}
}
