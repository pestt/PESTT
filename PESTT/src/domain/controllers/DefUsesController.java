package domain.controllers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;
import ui.events.DefUsesChangeViewEvent;
import adt.graph.AbstractPath;
import domain.DefUsesSet;
import domain.DefUsesVisitor;
import domain.constants.DefUsesView;
import domain.events.DefUsesSelectedEvent;

public class DefUsesController extends Observable {

	private DefUsesSet defUsesSet;
	private Object selectedDefUse;
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

	public void addObserverToDefUses() {
		defUsesSet.addObserver();
	}

	public void deleteObserverToDefUses() {
		defUsesSet.deleteObserver();
	}

	public void put(Object node, List<List<String>> defuses) {
		defUsesSet.put(node, defuses);
	}

	public void clearDefUsesSet() {
		defUsesSet.clear();
	}

	public void generateDefUses() {
		DefUsesVisitor<Integer> defusesVisitor = new DefUsesVisitor<Integer>();
		Activator.getDefault().getSourceGraphController()
				.applyVisitor(defusesVisitor);
	}

	public boolean isDefUseSelected() {
		return selectedDefUse != null;
	}

	public Set<List<Object>> getSelectedDefUse() {
		Set<List<Object>> set = new HashSet<List<Object>>();
		switch (selectedDefUseView) {
		case NODE_EDGE:
			List<Object> list = new LinkedList<Object>();
			list.add(selectedDefUse);
			set.add(list);
			break;
		case VARIABLE:
			List<List<Object>> varValues = defUsesSet.getDefUsesByVariable()
					.get(selectedDefUse);
			set.add(varValues.get(0));
			set.add(varValues.get(1));
			break;
		}
		return set;
	}

	public void selectDefUse(Object selected) {
		this.selectedDefUse = selected;
		setChanged();
		notifyObservers(new DefUsesSelectedEvent(selected));
	}

	public void unSelectDefUses() {
		selectDefUse(null);
	}

	public void selectView(String selected) {
		if (selected.equals(DefUsesView.VARIABLE.toString()))
			this.selectedDefUseView = DefUsesView.VARIABLE;
		else
			this.selectedDefUseView = DefUsesView.NODE_EDGE;
		setChanged();
		notifyObservers(new DefUsesChangeViewEvent(selectedDefUseView));
		if (!defUsesSet.isEmpty())
			defUsesSet.notifyChanges();
	}

	public DefUsesView getSelectedView() {
		return selectedDefUseView;
	}

	public Map<Object, List<List<String>>> getDefUsesByNodeEdge() {
		return defUsesSet.getDefUsesByNodeEdge();
	}

	public Map<String, List<List<Object>>> getDefUsesByVariable() {
		return defUsesSet.getDefUsesByVariable();
	}

	public Set<AbstractPath<Integer>> getTestRequirementsOfSelected() {
		switch (selectedDefUseView) {
		case NODE_EDGE:
			return defUsesSet.getTestRequirementsToNode(selectedDefUse);
		case VARIABLE:
			return defUsesSet
					.getTestRequirementsToVariable((String) selectedDefUse);
		}
		return null;
	}

	public Set<AbstractPath<Integer>> getTestRequirementsToNode(Object obj) {
		return defUsesSet.getTestRequirementsToNode(obj);
	}

	public Set<AbstractPath<Integer>> getTestRequirementsToVariable(String str) {
		return defUsesSet.getTestRequirementsToVariable(str);
	}

	public boolean isEmpty() {
		return defUsesSet.isEmpty();
	}
}