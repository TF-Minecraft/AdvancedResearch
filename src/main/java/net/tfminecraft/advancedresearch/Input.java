package net.tfminecraft.advancedresearch;

import java.util.ArrayList;
import java.util.List;

public class Input {
	public String id;
	public String item;
	public String station;
	public List<String> elements = new ArrayList<String>();
	public List<String> results = new ArrayList<String>();
	
	public List<String> getElements() {
		return elements;
	}
	public void setElements(List<String> elements) {
		this.elements = elements;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public List<String> getResults() {
		return results;
	}
	public void setResults(List<String> results) {
		this.results = results;
	}
}
