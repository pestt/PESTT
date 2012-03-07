package domain;

import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;

public class TestPathSet extends Observable implements Iterable<Path<Integer>> {

	private Set<Path<Integer>> testPathSet;

	public TestPathSet() {
		testPathSet = new TreeSet<Path<Integer>>();
	}

	public void addTestPath(Path<Integer> newTestPath) {
		testPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(testPathSet));
	}

	public void removeTestPath(Path<Integer> selected) {
		testPathSet.remove(selected);
		setChanged();
		notifyObservers(new TestPathChangedEvent(testPathSet));
	}
	
	public void clean() {
		testPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(testPathSet));
	}
	
	public int size() {
		return testPathSet.size();
	}
	
	@Override
	public Iterator<Path<Integer>> iterator() {
		return testPathSet.iterator();
	}
}