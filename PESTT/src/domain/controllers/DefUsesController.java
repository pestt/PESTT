package domain.controllers;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;
import domain.DefUsesSet;
import domain.graph.visitors.DefUsesVisitor;

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
	
	public void clearDefUsesSet() {
		defUsesSet.clear();
	}

	public void generateDefUses() {
		DefUsesVisitor<Integer> defusesVisitor = new DefUsesVisitor<Integer>();
		Activator.getDefault().getSourceGraphController().applyVisitor(defusesVisitor);
	}
}