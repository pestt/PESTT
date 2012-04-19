package domain.controllers;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;
import ui.events.DefUsesChangeViewEvent;
import domain.DefUsesSet;
import domain.constants.DefUsesView;
import domain.events.DefUsesSelectedEvent;
import domain.graph.visitors.DefUsesVisitor;

public class DefUsesController extends Observable {

	private DefUsesSet defUsesSet;
	private Set<List<Object>> selectedDefUse;
	private DefUsesView selectedDefUseView;
	
	public DefUsesController(DefUsesSet defUsesSet) {
		this.defUsesSet = defUsesSet;
	}
	
	public void addObserverDefUses(Observer o) {
		defUsesSet.addObserver(o);
	}
	
	public void deleteObserverDefUses(Observer o) {
		defUsesSet.deleteObserver(o);
	}
	
	public void put(Object node, List<String> defuses) {
		defUsesSet.put(node, defuses);
	}
	
	public void clearDefUsesSet() {
		defUsesSet.clear();
	}

	public void generateDefUses() {
		DefUsesVisitor<Integer> defusesVisitor = new DefUsesVisitor<Integer>();
		Activator.getDefault().getSourceGraphController().applyVisitor(defusesVisitor);
	}
	
	public boolean isDefUseSelected() {
		return selectedDefUse != null;
	}
	
	public Set<List<Object>> getSelectedDefUse() {
		return selectedDefUse;
	}

	public void selectDefUse(Set<List<Object>> selected) {
		this.selectedDefUse = selected;
		setChanged();
		notifyObservers(new DefUsesSelectedEvent(selected));		
	}

	public void selectView(String selected) {
		if(selected.equals(DefUsesView.VARIABLE.toString()))
			this.selectedDefUseView = DefUsesView.VARIABLE;
		else 
			this.selectedDefUseView = DefUsesView.NODE_EDGE;
		setChanged();
		notifyObservers(new DefUsesChangeViewEvent(selectedDefUseView));
		if(!defUsesSet.isEmpty())
			defUsesSet.getElements();
	}

	public DefUsesView getSelectedView() {
		return selectedDefUseView;
	}
	
	public Map<Object, List<String>> getDefUsesByNodeEdge() {
		return defUsesSet.getDefUsesByNodeEdge();
	}
	
	public Map<String, List<List<Object>>> getDefUsesByVariable() {
		return defUsesSet.getDefUsesByVariable();
	}
}