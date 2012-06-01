package domain;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import domain.events.TestPathChangedEvent;

public class TestPathSet extends Observable {

	private Set<Path<Integer>> testPathSet;
	private Set<Path<Integer>> manuallyTestPathSet;

	public TestPathSet() {
		testPathSet = new TreeSet<Path<Integer>>();
		manuallyTestPathSet = new TreeSet<Path<Integer>>();
	}

	public void add(Path<Integer> newTestPath) {
		manuallyTestPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	public void addAutomatic(Path<Integer> newTestPath) {
		testPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}

	public void remove(Set<Path<Integer>> selectedTestPaths) {
		for(Path<Integer> path : selectedTestPaths) {
			testPathSet.remove(path);
			manuallyTestPathSet.remove(path);
		}
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	public void clearAutomatic() {
		testPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	public void clearManually() {
		manuallyTestPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	
	public void clearAll() {
		testPathSet.clear();
		manuallyTestPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	public Iterable<Path<Integer>> getTestPathsManuallyAdded() {		
		return manuallyTestPathSet;
	}
	
	public Iterable<Path<Integer>> getTestPaths() {
		return testPathSet;
	}
	
	public boolean isManuallyAdded(Path<Integer> path) {
		return manuallyTestPathSet.contains(path);
	}
}