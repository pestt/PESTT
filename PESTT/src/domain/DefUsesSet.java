package domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import domain.events.DefUsesChangedEvent;

public class DefUsesSet extends Observable {
	
	private Map<String, List<String>> nodeDefUses;
	
	public DefUsesSet() {
		nodeDefUses = new LinkedHashMap<String, List<String>>();
	}

	public void put(String node, List<String> defuses) {
		nodeDefUses.put(node, defuses);
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeDefUses));
	}
	
	public void clear() {
		nodeDefUses.clear();
		setChanged();
		notifyObservers(new DefUsesChangedEvent(nodeDefUses));
	}
}