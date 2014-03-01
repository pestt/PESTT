package domain;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import adt.graph.AbstractPath;
import adt.graph.InfinitePath;
import adt.graph.Path;
import domain.coverage.algorithms.ICoverageAlgorithms;

/**
 * Represents the set of test requirements. It distinguishes tests
 * manually added from those automatically added. It accounts for 
 * infeasible tests.
 * 
 * @author fmartins
 * @version 1.0 (1/3/2013)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TestRequirements {

	/**
	 * The set of all tests (automatic plus manually added)
	 */
	private Set<AbstractPath<Integer>> testRequirementSet;
	
	/**
	 * Manually added tests 
	 */
	@XmlElementWrapper(name = "manualRequirements")
	@XmlElement(name = "manualRequirement")
	private Set<Path<Integer>> manualTestRequirementSet;
	
	/**
	 * Tests marked as infeasible
	 */
	@XmlElementWrapper(name = "infeasiblePaths")
	@XmlElement(name = "infeasiblePath")
	private Set<AbstractPath<Integer>> infeasibleSet;

	/**
	 * Constructs a set of empty requirements
	 */
	public TestRequirements() {
		testRequirementSet = new TreeSet<AbstractPath<Integer>>();
		manualTestRequirementSet = new TreeSet<Path<Integer>>();
		infeasibleSet = new TreeSet<AbstractPath<Integer>>();
	}

	/**
	 * Add a manual test requirement
	 * 
	 * @param path The test requirement to be added
	 */
	public void addManualTestRequirement(Path<Integer> path) {
		testRequirementSet.add(path);
		manualTestRequirementSet.add(path);
	}

	/**
	 * Removes a test requirement
	 * 
	 * @param requirement The test requirement to be removed
	 */
	public void remove(AbstractPath<Integer> requirement) {
		testRequirementSet.remove(requirement);
		manualTestRequirementSet.remove(requirement);
		infeasibleSet.remove(requirement);
	}

	/**
	 * Clears the test requirement set
	 */
	public void clear() {
		testRequirementSet.clear();
		manualTestRequirementSet.clear();
		infeasibleSet.clear();
	}

	/**
	 * @return the number of test requirements
	 */
	public int size() {
		return testRequirementSet.size();
	}

	/**
	 * Turns a test requirement infeasible or not
	 * 
	 * @param infeasible The test requirement to be classified
	 * @param status True to make the test infeasible; false otherwise.
	 */
	public void setInfeasible(AbstractPath<Integer> infeasible, boolean status) {
		if (status)
			infeasibleSet.add(infeasible);
		else
			infeasibleSet.remove(infeasible);
	}

	/**
	 * @param selectedTestRequirement The test requirement to be checked
	 * @return true is the test is infeasible; false, otherwise
	 */
	public boolean isInfeasible(AbstractPath<Integer> selectedTestRequirement) {
		return infeasibleSet.contains(selectedTestRequirement);
	}

	/**
	 * @return The number of test requirements marked as infeasible
	 */
	public int infeasiblesSize() {
		return infeasibleSet.size();
	}

	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		testRequirementSet = algorithm.getTestRequirements();
		testRequirementSet.addAll(manualTestRequirementSet);
	}

	public boolean hasInfinitePath() {
		for (AbstractPath<Integer> path : testRequirementSet)
			if (path instanceof InfinitePath<?>)
				return true;
		return false;
	}

	public Set<Path<Integer>> getPathToured(Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for (AbstractPath<Integer> path : testRequirementSet)
			if (path instanceof Path)
				if (seletedTestPath.isSubPath(path)) // Infinite paths will never be subpaths 
					coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithSideTrip(
			Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for (AbstractPath<Integer> path : testRequirementSet)
			if (seletedTestPath.toursWithSideTrip(path)) // Infinite paths will never be subpaths
				coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}

	public Set<Path<Integer>> getPathsTouredWithDeTour(
			Path<Integer> seletedTestPath) {
		Set<Path<Integer>> coveredPaths = new TreeSet<Path<Integer>>();
		for (AbstractPath<Integer> path : testRequirementSet)
			if (seletedTestPath.toursWithDetour(path)) // Infinite paths will never be subpaths
				coveredPaths.add((Path<Integer>) path);
		return coveredPaths;
	}

	/**
	 * @return The set of test requirements marked as infeasible
	 */
	public Iterable<AbstractPath<Integer>> getInfeasiblesTestRequirements() {
		return infeasibleSet;
	}

	/**
	 * @return The set of test requirements added manually
	 */
	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {
		return manualTestRequirementSet;
	}

	/**
	 * @return The set of test requirements
	 */
	public Iterable<AbstractPath<Integer>> getTestRequirements() {
		return testRequirementSet;
	}
}