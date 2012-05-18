package domain;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Edge;
import adt.graph.Node;
import domain.constants.DefUsesView;
import domain.events.DefUsesChangedEvent;
import domain.events.TestRequirementChangedEvent;

public class DefUsesSet extends Observable implements Observer {
	
	private Map<Object, List<List<String>>> nodeedgeDefUses;
	private Map<String, List<List<Object>>> variableDefUses;
	private Map<Object, Set<AbstractPath<Integer>>> nodeedgeTestRequirements;
	private Map<String, Set<AbstractPath<Integer>>> variableTestRequirements;
	
	public DefUsesSet() {
		nodeedgeDefUses = new LinkedHashMap<Object, List<List<String>>>();
		variableDefUses = new LinkedHashMap<String, List<List<Object>>>();
		nodeedgeTestRequirements = new LinkedHashMap<Object,  Set<AbstractPath<Integer>>>();
		variableTestRequirements = new LinkedHashMap<String,  Set<AbstractPath<Integer>>>();
	}
	
	public void addObserver() {
		Activator.getDefault().getTestRequirementController().addObserverTestRequirement(this);
	}
	
	public void deleteObserver() {
		Activator.getDefault().getTestRequirementController().deleteObserverTestRequirement(this);
	}
	
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof TestRequirementChangedEvent) 
			switch(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()) {
				case ALL_DU_PATHS:
				case ALL_DEFS:
				case ALL_USES:
					getTestRequirements(((TestRequirementChangedEvent) data).testRequirementSet);
					break;
			}		
	}

	public void put(Object node, List<List<String>> defuses) {
		nodeedgeDefUses.put(node, defuses);
		getDefUsesByVariables();
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	public void clear() {
		nodeedgeDefUses.clear();
		variableDefUses.clear();
		nodeedgeTestRequirements.clear();
		variableTestRequirements.clear();
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	public boolean isEmpty() {
		return nodeedgeDefUses.isEmpty();
	}
	
	public void notifyChanges() {
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeedgeDefUses, variableDefUses));
	}
	
	private void getDefUsesByVariables() {
		variableDefUses.clear();
		Set<String> vars = getVariable();
		for(String var : vars) 
			variableDefUses.put(var, getNodesEdgesDefUses(var));
	}
	
	private List<List<Object>> getNodesEdgesDefUses(String var) {
		List<List<Object>> nodeedges = new LinkedList<List<Object>>();
		List<Object> defs = new LinkedList<Object>();
		List<Object> uses = new LinkedList<Object>();
		for(Object key : nodeedgeDefUses.keySet()) {
			List<List<String>> vars = nodeedgeDefUses.get(key);
			if(vars.get(0).contains(var))
				defs.add(key);
			if(vars.get(1).contains(var))
				uses.add(key);
		}
		nodeedges.add(defs);
		nodeedges.add(uses);
		return nodeedges;
	}

	private Set<String> getVariable() {
		Set<String> vars = new TreeSet<String>();
		for(Object key : nodeedgeDefUses.keySet()) {
			List<List<String>> varList = nodeedgeDefUses.get(key);
			vars.addAll(parseVariable(varList.get(0)));
			vars.addAll(parseVariable(varList.get(1)));
		}
		return vars;
	}

	private List<String> parseVariable(List<String> input) {
		List<String> vars = new LinkedList<String>();
		for(String str : input)
			vars.add(str);
		return vars;
	}
	
	public Map<Object, List<List<String>>> getDefUsesByNodeEdge() {
		return nodeedgeDefUses;
	}
	
	public Map<String, List<List<Object>>> getDefUsesByVariable() {
		return variableDefUses;
	}

	private void getTestRequirements(Iterable<AbstractPath<Integer>> testRequirements) {
		nodeedgeTestRequirements.clear();
		variableTestRequirements.clear();
		for(Object key : nodeedgeDefUses.keySet()) {
			Set<AbstractPath<Integer>> pathsForNodeEdge = new LinkedHashSet<AbstractPath<Integer>>();
			for(AbstractPath<Integer> path : testRequirements)
				setDefUsesStatus(pathsForNodeEdge, path, key, DefUsesView.NODE_EDGE);
			nodeedgeTestRequirements.put(key, pathsForNodeEdge);
		}
		for(String key : variableDefUses.keySet()) {
			Set<AbstractPath<Integer>> pathsForVariables = new LinkedHashSet<AbstractPath<Integer>>();
			for(AbstractPath<Integer> path : testRequirements)
				setDefUsesStatus(pathsForVariables, path, key, DefUsesView.VARIABLE);
			variableTestRequirements.put(key, pathsForVariables);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setDefUsesStatus(Set<AbstractPath<Integer>> paths, AbstractPath<Integer> path, Object key, DefUsesView view) {
		switch(view) {
			case NODE_EDGE:
				if(key instanceof Edge<?> && path.isEdgeOfPath((Edge<Integer>) key))
					paths.add(path);
				else if(key instanceof Node<?> && path.containsNode((Node<Integer>) key))
					paths.add(path);
				break;
			case VARIABLE:
				List<List<Object>> values = variableDefUses.get(key);
				List<Object> defs = values.get(0);
				List<Object> uses = values.get(1);
				for(Object def : defs) {
					Node<Integer> begin = null;
					if(def instanceof Edge<?>)
						begin = ((Edge<Integer>) def).getBeginNode();
					else if(def instanceof Node<?>)
						begin = ((Node<Integer>) def);
					if(begin == path.from()) 
						for(Object use : uses) {
							Node<Integer> end = null;
							if(use instanceof Edge<?>)
								end = ((Edge<Integer>) use).getEndNode();
							else if(use instanceof Node<?>)
								end = ((Node<Integer>) use);
							if(end == path.to()) 
								paths.add(path);
						}
				}
				break;
		}
	}
	
	public Set<AbstractPath<Integer>> getTestRequirementsToNode(Object obj) {
		return nodeedgeTestRequirements.get(obj);
	}
	
	public Set<AbstractPath<Integer>> getTestRequirementsToVariable(String str) {
		return variableTestRequirements.get(str);
	}
}