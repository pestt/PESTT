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
	@XmlElement private TestRequirements testRequirements;
	
	public MethodTest() {
	}

	public MethodTest(String methodSignature) {
		this.methodSignature = methodSignature;
		testRequirements = new TestRequirements();
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

	public Iterable<Path> getTestRequirementsManuallyAdded() {
		return testRequirements.getTestRequirementsManuallyAdded();
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

	
}
