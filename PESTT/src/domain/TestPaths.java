package domain;

import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import domain.constants.TestType;

public class TestPaths extends Observable {

	private Set<Path> testPathSet;
	private Set<Path> manuallyTestPathSet;
	private Map<Path, String> executionTips;

	public TestPaths() {
		testPathSet = new TreeSet<Path>();
		manuallyTestPathSet = new TreeSet<Path>();
	}

	public void add(Path newTestPath) {
		testPathSet.add(newTestPath);
		manuallyTestPathSet.add(newTestPath);
		executionTips.put(newTestPath, TestType.MANUALLY);
	}

	public void addAutomatic(Path newTestPath, String executionTip) {
		testPathSet.add(newTestPath);
		manuallyTestPathSet.remove(newTestPath);
		executionTips.put(newTestPath, executionTip);
	}
	
	public void remove(Set<Path> selectedTestPaths) {
		for (Path path : selectedTestPaths) {
			testPathSet.remove(path);
			manuallyTestPathSet.remove(path);
			executionTips.remove(path);
		}
	}

	public void clearAutomatic() {
		Iterator<Path> it = testPathSet.iterator();
		while (it.hasNext()) {
			Path p = it.next();
			if (!manuallyTestPathSet.contains(p)) {
				it.remove();
				executionTips.remove(p);
			}
		}
	}

	public void clearManually() {
		for (Path p: manuallyTestPathSet) 
			executionTips.remove(p);
		testPathSet.removeAll(manuallyTestPathSet);
		manuallyTestPathSet.clear();
	}

	public void clearAll() {
		testPathSet.clear();
		manuallyTestPathSet.clear();
		executionTips.clear();
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

	public String getExecutionTip(Path path) {
		return executionTips.get(path);
	}
}