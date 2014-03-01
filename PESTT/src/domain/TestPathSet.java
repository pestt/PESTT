package domain;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import domain.events.TestPathChangedEvent;

public class TestPathSet extends Observable {

	private Set<Path> testPathSet;
	private Set<Path> manuallyTestPathSet;

	public TestPathSet() {
		testPathSet = new TreeSet<Path>();
		manuallyTestPathSet = new TreeSet<Path>();
	}

	public void add(Path newTestPath) {
		manuallyTestPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public void addAutomatic(Path newTestPath) {
		testPathSet.add(newTestPath);
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public void remove(Set<Path> selectedTestPaths) {
		for (Path path : selectedTestPaths) {
			testPathSet.remove(path);
			manuallyTestPathSet.remove(path);
		}
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public void clearAutomatic() {
		testPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public void clearManually() {
		manuallyTestPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public void clearAll() {
		testPathSet.clear();
		manuallyTestPathSet.clear();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getTestPathsManuallyAdded()));
	}

	public Iterable<Path> getTestPathsManuallyAdded() {
		return manuallyTestPathSet;
	}

	public Iterable<Path> getTestPaths() {
		return testPathSet;
	}

	public boolean isManuallyAdded(Path path) {
		return manuallyTestPathSet.contains(path);
	}
}