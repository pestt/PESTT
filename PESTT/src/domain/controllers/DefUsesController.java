package domain.controllers;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import domain.DefUsesSet;

public class DefUsesController extends Observable {

	private DefUsesSet defUsesSet;
	
	public DefUsesController(DefUsesSet defUsesSet) {
		this.defUsesSet = defUsesSet;
	}
	
	public void addObserverDefUses(Observer o) {
		defUsesSet.addObserver(o);
	}
	
	public void put(String node, List<String> defuses) {
		defUsesSet.put(node, defuses);
	}
	
	public void clear() {
		defUsesSet.clear();
	}
}