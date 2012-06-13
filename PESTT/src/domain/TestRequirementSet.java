package domain;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.InfinitePath;
import adt.graph.Path;
import domain.coverage.algorithms.ICoverageAlgorithms;
import domain.events.InfeasibleChangedEvent;
import domain.events.TestRequirementChangedEvent;


public class TestRequirementSet extends Observable {

	private Set<AbstractPath<Integer>> testRequirementSet;
	private Set<Path<Integer>> manuallyTestRequirementSet;
	private Set<AbstractPath<Integer>> infeasibleSet;

	public TestRequirementSet() {
		testRequirementSet = new TreeSet<AbstractPath<Integer>>();
		manuallyTestRequirementSet = new TreeSet<Path<Integer>>();
		infeasibleSet = new TreeSet<AbstractPath<Integer>>();
	}

	public void add(Path<Integer> path) {
		testRequirementSet.add(path);
		manuallyTestRequirementSet.add(path);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}

	public void remove(AbstractPath<Integer> selectedTestRequirement) {
		testRequirementSet.remove(selectedTestRequirement);
		manuallyTestRequirementSet.remove(selectedTestRequirement);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public void clear() {
		testRequirementSet.clear();
		manuallyTestRequirementSet.clear();	
		infeasibleSet.clear();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public int size() {
		return testRequirementSet.size();
	}
	
	public void enableInfeasible(AbstractPath<Integer> infeasible) {
		infeasibleSet.add(infeasible);
		setChanged();
		notifyObservers(new InfeasibleChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public void disableInfeasible(AbstractPath<Integer> infeasible) {
		infeasibleSet.remove(infeasible);
		setChanged();
		notifyObservers(new InfeasibleChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
	}
	
	public boolean isInfeasible(AbstractPath<Integer> selectedTestRequirement) {
		return infeasibleSet.contains(selectedTestRequirement);
	}
	
	public int sizeInfeasibles() {
		return infeasibleSet.size();
	}
	
	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		if(algorithm != null) {
			testRequirementSet = algorithm.getTestRequirements();
			testRequirementSet.addAll(manuallyTestRequirementSet);
			setChanged();
			notifyObservers(new TestRequirementChangedEvent(getTestRequirements(), getInfeasiblesTestRequirements(), getTestRequirementsManuallyAdded(), hasInfinitePath()));
		}
	}
	
	public boolean hasInfinitePath() {
		for(AbstractPath<Integer> path : testRequirementSet) 
			if(path instanceof InfinitePath<?>)
				return true;
		return false;
	}

	public Set<Path<Integer>> getPathToured(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(AbstractPath<Integer> path : testRequirementSet)
			if(path instanceof Path)
				if(seletedTestPath.isSubPath(path))  // Infinite paths will never be subpaths 
					coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithSideTrip(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(AbstractPath<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithSideTrip(path)) // Infinite paths will never be subpaths
				coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithDeTour(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for(AbstractPath<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithDetour(path)) // Infinite paths will never be subpaths
				coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}
	
	public Iterable<AbstractPath<Integer>> getInfeasiblesTestRequirements() {
		return infeasibleSet; 
	}
	
	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {		
		return manuallyTestRequirementSet;
	}
	
	public Iterable<AbstractPath<Integer>> getTestRequirements() {		
		return testRequirementSet;
	}
}