package domain;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import domain.events.DefUsesChangedEvent;

public class DefUsesSet extends Observable {
	
	private Map<Object, List<String>> nodeedgeDefUses;
	private Map<String, List<List<Object>>> variableDefUses;
	
	public DefUsesSet() {
		nodeedgeDefUses = new LinkedHashMap<Object, List<String>>();
		variableDefUses = new LinkedHashMap<String, List<List<Object>>>();
	}

	public void put(Object node, List<String> defuses) {
		nodeedgeDefUses.put(node, defuses);
		getdefUsesByVariables();
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	public void clear() {
		nodeedgeDefUses.clear();
		variableDefUses.clear();
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	public boolean isEmpty() {
		return nodeedgeDefUses.isEmpty();
	}
	
	public void getElements() {
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	private void getdefUsesByVariables() {
		Set<String> vars = getVariables(nodeedgeDefUses);
		for(String var : vars) {
			List<List<Object>> nodeedges = getNodesEdgesDefUses(nodeedgeDefUses, var);
			variableDefUses.put(var, nodeedges);
		}
	}
	
	private List<List<Object>> getNodesEdgesDefUses(Map<Object, List<String>> defuses, String var) {
		List<List<Object>> nodeedges = new LinkedList<List<Object>>();
		List<Object> defs = new LinkedList<Object>();
		List<Object> uses = new LinkedList<Object>();
		for(Object obj : defuses.keySet()) {
			List<String> vars = defuses.get(obj);
			if(vars.get(0).contains(var))
				defs.add(obj);
			if(vars.get(1).contains(var))
				uses.add(obj);
		}
		nodeedges.add(defs);
		nodeedges.add(uses);
		return nodeedges;
	}

	private Set<String> getVariables(Map<Object, List<String>> defuses) {
		Set<String> vars = new TreeSet<String>();
		for(Object obj : defuses.keySet()) {
			List<String> variables = defuses.get(obj);
			vars.addAll(parseVariables(variables.get(0)));
			vars.addAll(parseVariables(variables.get(1)));
		}
		return vars;
	}

	private List<String> parseVariables(String input) {
		List<String> vars = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		while(strtok.hasMoreTokens())
			vars.add(strtok.nextToken());
		return vars;
	}
}