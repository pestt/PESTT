package domain;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import adt.graph.Path;

public class TestPathController extends Observable {

	private TestPathSet testPathSet;
	private Object selected;
	
	public TestPathController(TestPathSet testPathSet) {
		this.testPathSet = testPathSet;
	}
	
	public void addObserverTestPath(Observer o) {
		testPathSet.addObserver(o);
	}

	public void addTestPath(Path<Integer> newTestPath) {
		testPathSet.addTestPath(newTestPath);
	}

	@SuppressWarnings("unchecked")
	public void removeSelectedTestPath() {
		testPathSet.removeTestPath((Path<Integer>) selected);
		selectTestPath(null);
	}
	
	public void cleanTestPathSet() {
		testPathSet.clean();
	}
	
	public int getTestPathSetSize() {
		return testPathSet.size();
	}

	public boolean isTestPathSelected() {
		return selected != null;
	}
	
	public Object getSelectedTestPath() {
		return selected;
	}

	public void selectTestPath(Object selected) {
		this.selected = selected;
		setChanged();
		notifyObservers(new TestPathSelected(selected));
	}
	
	public Iterator<Path<Integer>> iterator() {
		return testPathSet.iterator();
	}
}