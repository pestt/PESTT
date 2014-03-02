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
	
	@XmlElement(name = "testRequirements")
	private TestRequirements testRequirements;
	
	@XmlElement(name = "testPaths")
	private TestPaths testPaths;
	
	public MethodTest() {
	}

	public MethodTest(String methodSignature) {
		this.methodSignature = methodSignature;
		testRequirements = new TestRequirements();
		testPaths = new TestPaths();
	}
	
	public String getMethodSignature() {
		return methodSignature;
	}
	
	public void addManualTestRequirement(Path path) {
		testRequirements.addManualTestRequirement(path);
	}

	public void removeManualTestRequirement(AbstractPath selectedTestRequirement) {
		testRequirements.remove(selectedTestRequirement);
	}

	public void clearTestRequirements() {
		testRequirements.clear();
	}

	public int size() {
		return testRequirements.size();
	}

	public boolean hasInfinitePath() {
		return testRequirements.hasInfinitePath();
	}

	public boolean isInfeasible(AbstractPath requirement) {
		return testRequirements.isInfeasible(requirement);
	}

	public void removeTestRequirement(AbstractPath requirement) {
		testRequirements.remove(requirement);
	}

	public void setInfeasible(AbstractPath infeasible, boolean status) {
		testRequirements.setInfeasible(infeasible, status);
	}

	public int infeasiblesSize() {
		return testRequirements.infeasiblesSize();
	}

	public void generateTestRequirements(ICoverageAlgorithms algorithm) {
		testRequirements.generateTestRequirements(algorithm);
	}

	public Iterable<AbstractPath> getInfeasiblesTestRequirements() {
		return testRequirements.getInfeasiblesTestRequirements();
	}

	public Iterable<Path> getManuallyAddedTestPaths() {
		return testRequirements.getTestRequirementsManuallyAdded();
	}

	public Iterable<Path> getTestPaths() {
		return testPaths.getTestPaths();
	}

	public Iterable<AbstractPath> getTestRequirements() {
		return testRequirements.getTestRequirements();
	}

	public Set<Path> getPathToured(Path seletedTestPath) {
		return testRequirements.getPathToured(seletedTestPath);
	}

	public Set<Path> getPathsTouredWithSideTrip(
			Path seletedTestPath) {
		return testRequirements.getPathsTouredWithSideTrip(seletedTestPath);
	}

	public Set<Path> getPathsTouredWithDeTour(
			Path seletedTestPath) {
		return testRequirements.getPathsTouredWithDeTour(seletedTestPath);
	}

	public void addManualTestPath(Path newTestPath) {
		testPaths.add(newTestPath);
	}

	public void addAutomaticTestPath(Path newTestPath, String executionTip) {
		testPaths.addAutomatic(newTestPath, executionTip);
	}

	public String getExecutionTip(Path path) {
		return testPaths.getExecutionTip(path);
	}

	public void removeTestPaths(Set<Path> selectedTestPaths) {
		testPaths.remove(selectedTestPaths);
	}

	public void clearAutomaticTestPaths() {
		testPaths.clearAutomatic();
	}

	public void clearManuallyAddedTestPaths() {
		testPaths.clearManually();
	}
	
}
