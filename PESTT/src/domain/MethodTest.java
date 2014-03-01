package domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.constants.CoverageCriteriaId;
import domain.coverage.algorithms.ICoverageAlgorithms;


@XmlAccessorType(XmlAccessType.FIELD)
public class MethodTest {

	@XmlAttribute private String methodSignature;
	@XmlElement private CoverageCriteriaId coverageCriteria;
	@XmlElement private String tourType;
	@XmlElement private TestRequirements testRequirementSet;
	
	public MethodTest() {
	}

	public MethodTest(String methodSignature) {
		this.methodSignature = methodSignature;
	}
	
	public String getMethodSignature() {
		return methodSignature;
	}
	
	public void addManualTestRequirement(Path<Integer> path) {
		testRequirementSet.addManualTestRequirement(path);
	}

	public void removeManualTestRequirement(AbstractPath<Integer> selectedTestRequirement) {
		testRequirementSet.remove(selectedTestRequirement);
	}

	public void clearTestRequirements() {
		testRequirementSet.clear();
	}

	public int size() {
		return testRequirementSet.size();
	}

	public boolean hasInfinitePath() {
		return testRequirementSet.hasInfinitePath();
	}

	public boolean isInfeasible(AbstractPath<Integer> requirement) {
		return testRequirementSet.isInfeasible(requirement);
	}

	public void removeTestRequirement(AbstractPath<Integer> requirement) {
		testRequirementSet.remove(requirement);
	}

	public void setInfeasible(AbstractPath<Integer> infeasible, boolean status) {
		testRequirementSet.setInfeasible(infeasible, status);
	}

	public int infeasiblesSize() {
		return testRequirementSet.infeasiblesSize();
	}

	public void generateTestRequirements(ICoverageAlgorithms<Integer> algorithm) {
		testRequirementSet.generateTestRequirements(algorithm);
	}

	public Iterable<AbstractPath<Integer>> getInfeasiblesTestRequirements() {
		return testRequirementSet.getInfeasiblesTestRequirements();
	}

	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {
		return testRequirementSet.getTestRequirementsManuallyAdded();
	}

	public Iterable<AbstractPath<Integer>> getTestRequirements() {
		return testRequirementSet.getTestRequirements();
	}

	public Set<Path<Integer>> getPathToured(Path<Integer> seletedTestPath) {
		return testRequirementSet.getPathToured(seletedTestPath);
	}

	public Set<Path<Integer>> getPathsTouredWithSideTrip(
			Path<Integer> seletedTestPath) {
		return testRequirementSet.getPathsTouredWithSideTrip(seletedTestPath);
	}

	public Set<Path<Integer>> getPathsTouredWithDeTour(
			Path<Integer> seletedTestPath) {
		return testRequirementSet.getPathsTouredWithDeTour(seletedTestPath);
	}

	
}
