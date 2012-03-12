package domain;

import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import adt.graph.SequencePath;
import domain.coverage.algorithms.ICoverageAlgorithms;
import domain.events.TestRequirementChangedEvent;


public class TestRequirementSet extends Observable implements Iterable<Path<Integer>> {

	private Set<Path<Integer>> testRequirementSet;
	private Set<Path<Integer>> manuallyTestRequirementSet;
	private Set<Path<Integer>> indeasibleSet;

	public TestRequirementSet() {
		testRequirementSet = new TreeSet<Path<Integer>>();
		manuallyTestRequirementSet = new TreeSet<Path<Integer>>();
		indeasibleSet = new TreeSet<Path<Integer>>();
	}

	public void add(Path<Integer> path) {
		testRequirementSet.add(path);
		manuallyTestRequirementSet.add(path);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(iterator(), hasInfinitePath()));
	}

	public void remove(Path<Integer> selected) {
		testRequirementSet.remove(selected);
		manuallyTestRequirementSet.remove(selected);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(iterator(), hasInfinitePath()));
	}
	
	public void clear() {
		testRequirementSet.clear();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(iterator(), hasInfinitePath()));
	}
	
	public int size() {
		return testRequirementSet.size();
	}
	
	public void enableInfeasible(Path<Integer> infeasible) {
		indeasibleSet.add(infeasible);
	}
	
	public void disableInfeasible(Path<Integer> infeasible) {
		indeasibleSet.remove(infeasible);
	}
	
	public boolean isInfeasible(Path<Integer> infeasible) {
		return indeasibleSet.contains(infeasible);
	}
	
	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		testRequirementSet = algorithm.getTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(iterator(), hasInfinitePath()));
	}
	
	public boolean hasInfinitePath() {
		for(Path<Integer> path : testRequirementSet) 
			if(path instanceof SequencePath<?>)
				return true;
		return false;
	}

	public Set<Path<Integer>> getPathToured(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.isSubPath(path))
					coveredPaths.add(path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithSideTrip(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithSideTrip(path))
				coveredPaths.add(path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithDeTour(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithDeTour(path))
				coveredPaths.add(path);
		return coveredPaths;
	}
	
	@Override
	public Iterator<Path<Integer>> iterator() {
		return testRequirementSet.iterator();
	}
}