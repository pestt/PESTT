package domain;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Path;
import adt.graph.SequencePath;
import domain.coverage.algorithms.ICoverageAlgorithms;
import domain.events.TestRequirementChangedEvent;


public class TestRequirementSet extends Observable {

	private Set<Path<Integer>> testRequirementSet;
	private Set<Path<Integer>> manuallyTestRequirementSet;
	private Set<Path<Integer>> infeasibleSet;

	public TestRequirementSet() {
		testRequirementSet = new TreeSet<Path<Integer>>();
		manuallyTestRequirementSet = new TreeSet<Path<Integer>>();
		infeasibleSet = new TreeSet<Path<Integer>>();
	}

	public void add(Path<Integer> path) {
		testRequirementSet.add(path);
		manuallyTestRequirementSet.add(path);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}

	public void remove(Path<Integer> selected) {
		testRequirementSet.remove(selected);
		manuallyTestRequirementSet.remove(selected);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public void clear() {
		testRequirementSet.clear();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public int size() {
		return testRequirementSet.size();
	}
	
	public void enableInfeasible(Path<Integer> infeasible) {
		infeasibleSet.add(infeasible);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public void disableInfeasible(Path<Integer> infeasible) {
		infeasibleSet.remove(infeasible);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public void clearInfeasibles() {
		infeasibleSet.clear();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public boolean isInfeasible(Path<Integer> infeasible) {
		return infeasibleSet.contains(infeasible);
	}
	
	public int sizeInfeasibles() {
		return infeasibleSet.size();
	}
	
	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		testRequirementSet = algorithm.getTestRequirements();
		testRequirementSet.addAll(manuallyTestRequirementSet);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
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
	
	public Iterable<Path<Integer>> getInfeasiblesTestRequirements() {
		return infeasibleSet; 
	}
	
	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {		
		return manuallyTestRequirementSet;
	}
	
	public Iterable<Path<Integer>> getTestRequirements() {		
		return testRequirementSet;
	}
}