package domain;

import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import domain.events.TestPathChangedEvent;

public class TestPathSet extends Observable implements Iterable<Path<Integer>> {

	private Set<Path<Integer>> testPathSet;
	private Set<Path<Integer>> manuallyTestPathSet;

	public TestPathSet() {
		testPathSet = new TreeSet<Path<Integer>>();
		manuallyTestPathSet = new TreeSet<Path<Integer>>();
	}

	public void add(Path<Integer> newTestPath) {
		testPathSet.add(newTestPath);
		manuallyTestPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(iterator()));
	}

	public void remove(Set<Path<Integer>> selectedTestPaths) {
		for(Path<Integer> path : selectedTestPaths) {
			testPathSet.remove(path);
			manuallyTestPathSet.remove(path);
		}
		setChanged();
		notifyObservers(new TestPathChangedEvent(iterator()));
	}
	
	public void clear() {
		testPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(iterator()));
	}
	
	@Override
	public Iterator<Path<Integer>> iterator() {
		return testPathSet.iterator();
	}
}