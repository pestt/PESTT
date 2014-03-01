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
import domain.TestRequirements;
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
	private MethodTest methodUnderTest;
	private AbstractPath<Integer> selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;

	public TestRequirementController(SourceGraph sourceGraph,
			TestRequirements testRequirementSet) {
		this.sourceGraph = sourceGraph;
	}

	public void setMethodUnderTest(MethodTest method) {
		methodUnderTest = method;
	}
	
	public void addTestRequirement(Path<Integer> newTestRequirement) {
		methodUnderTest.addManualTestRequirement(newTestRequirement);
		unSelectTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), methodUnderTest.hasInfinitePath()));
	}

	public void removeSelectedTestRequirement() {
		methodUnderTest.removeTestRequirement(selectedTestRequirement);
		unSelectTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), methodUnderTest.hasInfinitePath()));
	}

	public void clearTestRequirementSet() {
		methodUnderTest.clearTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), methodUnderTest.hasInfinitePath()));
	}

	public int testRequirementsSize() {
		return methodUnderTest.size();
	}

	public void setInfeasible(AbstractPath<Integer> abstractPath, boolean status) {
		methodUnderTest.setInfeasible(abstractPath, status);
		setChanged();
		notifyObservers(new InfeasibleChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), methodUnderTest.hasInfinitePath()));
	}

	public int infeasiblesSize() {
		return methodUnderTest.infeasiblesSize();
	}

	public boolean isInfeasiblesTestRequirements(
			AbstractPath<Integer> abstractPath) {
		return methodUnderTest.isInfeasible(abstractPath);
	}

	public Iterable<AbstractPath<Integer>> getInfeasiblesTestRequirements() {
		return methodUnderTest.getInfeasiblesTestRequirements();
	}

	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {
		return methodUnderTest.getTestRequirementsManuallyAdded();
	}

	public Iterable<AbstractPath<Integer>> getTestRequirements() {
		return methodUnderTest.getTestRequirements();
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
		methodUnderTest.generateTestRequirements(algorithm);
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(
				getTestRequirements(), getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), methodUnderTest.hasInfinitePath()));
	}

	public Set<Path<Integer>> getTestPathCoverage(Path<Integer> seletedTestPath) {
		TourType selectedTourType = Activator.getDefault()
				.getTestPathController().getSelectedTourType();
		switch (selectedTourType) {
		case TOUR:
			return methodUnderTest.getPathToured(seletedTestPath);
		case SIDETRIP:
			return methodUnderTest.getPathsTouredWithSideTrip(seletedTestPath);
		case DETOUR:
			return methodUnderTest.getPathsTouredWithDeTour(seletedTestPath);
		default:
			return null;//normal
		}
	}
}