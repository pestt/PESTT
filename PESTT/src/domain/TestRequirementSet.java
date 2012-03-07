package domain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.InfinitePath;
import adt.graph.Path;
import domain.coverage.algorithms.ICoverageAlgorithms;


public class TestRequirementSet extends Observable implements Iterable<Path<Integer>> {

	private Set<Path<Integer>> testRequirementSet;

	public TestRequirementSet() {
		testRequirementSet = new TreeSet<Path<Integer>>();
	}

	public void addTestRequirement(Path<Integer> path) {
		testRequirementSet.add(path);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(testRequirementSet, hasInfinitePath()));
	}

	public void removeTestRequirement(Path<Integer> selected) {
		testRequirementSet.remove(selected);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(testRequirementSet, hasInfinitePath()));
	}
	
	public void clean() {
		testRequirementSet.clear();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(testRequirementSet, hasInfinitePath()));
	}
	
	public int size() {
		return testRequirementSet.size();
	}
	
	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		testRequirementSet = algorithm.getTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(testRequirementSet, hasInfinitePath()));
	}
	
	public boolean hasInfinitePath() {
		for(Path<Integer> path : testRequirementSet) 
			if(path instanceof InfinitePath<?>)
				return true;
		return false;
	}

	public List<Path<Integer>> getPathToured(Path<Integer> seletedTestPath) {
		List<Path<Integer>> coveredPaths = new LinkedList<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.isSubPath(path))
					coveredPaths.add(path);
		return coveredPaths;
	}

	public List<Path<Integer>> getPathsTouredWithSideTrip(Path<Integer> seletedTestPath) {
		List<Path<Integer>> coveredPaths = new LinkedList<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithSideTrip(path))
				coveredPaths.add(path);
		return coveredPaths;
	}

	public List<Path<Integer>> getPathsTouredWithDeTour(Path<Integer> seletedTestPath) {
		List<Path<Integer>> coveredPaths = new LinkedList<Path<Integer>>();
		for(Path<Integer> path : testRequirementSet)
			if(seletedTestPath.toursWithDeTour(path))
				coveredPaths.add(path);
		return coveredPaths;
	}

	public List<Path<Integer>> getTotalPathToured(Iterator<Path<Integer>> iterator) {
		List<Path<Integer>> total = new LinkedList<Path<Integer>>();
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			List<Path<Integer>> aux = getPathToured(path);
			for(Path<Integer> p : aux)
				if(!total.contains(p))
					total.add(p);
		}
		return total;
	}

	public List<Path<Integer>> getTotalPathsTouredWithSideTrip(Iterator<Path<Integer>> iterator) {
		List<Path<Integer>> total = new LinkedList<Path<Integer>>();
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			List<Path<Integer>> aux = getPathsTouredWithSideTrip(path);
			for(Path<Integer> p : aux)
				if(!total.contains(p))
					total.add(p);
		}
		return total;
	}

	public List<Path<Integer>> getTotalPathsTouredWithDeTour(Iterator<Path<Integer>> iterator) {
		List<Path<Integer>> total = new LinkedList<Path<Integer>>();
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			List<Path<Integer>> aux = getPathsTouredWithDeTour(path);
			for(Path<Integer> p : aux)
				if(!total.contains(p))
					total.add(p);
		}
		return total;
	}
	
	@Override
	public Iterator<Path<Integer>> iterator() {
		return testRequirementSet.iterator();
	}
}