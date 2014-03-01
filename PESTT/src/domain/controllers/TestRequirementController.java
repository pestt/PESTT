package domain.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Node;
import adt.graph.Path;
import domain.MethodTest;
import domain.SourceGraph;
import domain.TestSuite;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;
import domain.coverage.algorithms.CoverageAlgorithmsFactory;
import domain.coverage.algorithms.ICoverageAlgorithms;
import domain.events.InfeasibleChangedEvent;
import domain.events.TestRequirementChangedEvent;
import domain.events.TestRequirementSelectedCriteriaEvent;
import domain.events.TestRequirementSelectedEvent;

public class TestRequirementController extends Observable {

	private SourceGraph sourceGraph;
	private AbstractPath<Integer> selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;
	private TestSuiteController testSuiteController;

	public TestRequirementController(SourceGraph sourceGraph,
			TestSuiteController testSuiteController) {
		this.sourceGraph = sourceGraph;
		this.testSuiteController = testSuiteController;
	}
	
	public void addTestRequirement(Path<Integer> newTestRequirement) {
		testSuiteController.getMethodUnderTest().addManualTestRequirement(newTestRequirement);
		unSelectTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteController.getMethodUnderTest().hasInfinitePath()));
	}

	public void removeSelectedTestRequirement() {
		testSuiteController.getMethodUnderTest().removeTestRequirement(selectedTestRequirement);
		unSelectTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteController.getMethodUnderTest().hasInfinitePath()));
	}

	public void clearTestRequirementSet() {
		testSuiteController.getMethodUnderTest().clearTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteController.getMethodUnderTest().hasInfinitePath()));
	}

	public int testRequirementsSize() {
		return testSuiteController.getMethodUnderTest().size();
	}

	public void setInfeasible(AbstractPath<Integer> abstractPath, boolean status) {
		testSuiteController.getMethodUnderTest().setInfeasible(abstractPath, status);
		setChanged();
		notifyObservers(new InfeasibleChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteController.getMethodUnderTest().hasInfinitePath()));
	}

	public int infeasiblesSize() {
		return testSuiteController.getMethodUnderTest().infeasiblesSize();
	}

	public boolean isInfeasiblesTestRequirements(
			AbstractPath<Integer> abstractPath) {
		return testSuiteController.getMethodUnderTest().isInfeasible(abstractPath);
	}

	public Iterable<AbstractPath<Integer>> getInfeasiblesTestRequirements() {
		return testSuiteController.getMethodUnderTest().getInfeasiblesTestRequirements();
	}

	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {
		return testSuiteController.getMethodUnderTest().getTestRequirementsManuallyAdded();
	}

	public Iterable<AbstractPath<Integer>> getTestRequirements() {
		return testSuiteController.getMethodUnderTest().getTestRequirements();
	}

	public Path<Integer> createTestRequirement(String input) {
		boolean validPath = true;
		List<String> insertedNodes = getInsertedNodes(input);
		List<Node<Integer>> pathNodes = new LinkedList<Node<Integer>>();
		try {
			List<Node<Integer>> fromToNodes = new ArrayList<Node<Integer>>(2);
			fromToNodes.add(sourceGraph.getSourceGraph().getNode(
					Integer.parseInt(insertedNodes.get(0))));
			int i = 1;
			while (i < insertedNodes.size() && validPath) {
				fromToNodes.add(sourceGraph.getSourceGraph().getNode(
						Integer.parseInt(insertedNodes.get(i))));
				if (fromToNodes.get(0) != null
						&& fromToNodes.get(1) != null
						&& sourceGraph.getSourceGraph().isPath(
								new Path<Integer>(fromToNodes))) {
					pathNodes.add(fromToNodes.get(0));
					fromToNodes.remove(0);
				} else
					validPath = false;
				i++;
			}
			if (validPath) {
				pathNodes.add(fromToNodes.get(0));
				return new Path<Integer>(pathNodes);
			}
		} catch (NumberFormatException ee) {
			//ignore
		}
		return null;//!= null check
	}

	private List<String> getInsertedNodes(String input) {
		List<String> aux = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		// separate the inserted nodes.
		while (strtok.hasMoreTokens())
			aux.add(strtok.nextToken());
		return aux;
	}

	public boolean isTestRequirementSelected() {
		return selectedTestRequirement != null;
	}

	public AbstractPath<Integer> getSelectedTestRequirement() {
		return selectedTestRequirement;
	}

	public void selectTestRequirement(AbstractPath<Integer> selected) {
		this.selectedTestRequirement = selected;
		setChanged();
		notifyObservers(new TestRequirementSelectedEvent(selected));
	}

	public void unSelectTestRequirements() {
		selectTestRequirement(null);
	}

	public boolean isCoverageCriteriaSelected() {
		return selectedCoverageAlgorithm != null;
	}

	public GraphCoverageCriteriaId getSelectedCoverageCriteria() {
		return selectedCoverageAlgorithm;
	}

	public void selectCoverageCriteria(GraphCoverageCriteriaId selected) {
		this.selectedCoverageAlgorithm = selected;
		setChanged();
		notifyObservers(new TestRequirementSelectedCriteriaEvent(selected));
	}

	public void generateTestRequirement() {
		ICoverageAlgorithms<Integer> algorithm = CoverageAlgorithmsFactory.INSTANCE
				.getCoverageAlgorithm(sourceGraph, selectedCoverageAlgorithm);
		testSuiteController.getMethodUnderTest().generateTestRequirements(algorithm);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(
				getTestRequirements(), getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteController.getMethodUnderTest().hasInfinitePath()));
	}

	public Set<Path<Integer>> getTestPathCoverage(Path<Integer> seletedTestPath) {
		TourType selectedTourType = Activator.getDefault()
				.getTestPathController().getSelectedTourType();
		switch (selectedTourType) {
		case TOUR:
			return testSuiteController.getMethodUnderTest().getPathToured(seletedTestPath);
		case SIDETRIP:
			return testSuiteController.getMethodUnderTest().getPathsTouredWithSideTrip(seletedTestPath);
		case DETOUR:
			return testSuiteController.getMethodUnderTest().getPathsTouredWithDeTour(seletedTestPath);
		default:
			return null;//normal
		}
	}
}