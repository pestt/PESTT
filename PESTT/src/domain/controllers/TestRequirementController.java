package domain.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Node;
import adt.graph.Path;
import domain.SourceGraph;
import domain.TestRequirementSet;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;
import domain.coverage.algorithms.CoverageAlgorithmsFactory;
import domain.coverage.algorithms.ICoverageAlgorithms;
import domain.events.TestRequirementSelectedCriteriaEvent;
import domain.events.TestRequirementSelectedEvent;

public class TestRequirementController extends Observable {

	private SourceGraph sourceGraph;
	private TestRequirementSet testRequirementSet;
	private AbstractPath<Integer> selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;

	public TestRequirementController(SourceGraph sourceGraph,
			TestRequirementSet testRequirementSet) {
		this.sourceGraph = sourceGraph;
		this.testRequirementSet = testRequirementSet;
	}

	public void addObserverTestRequirement(Observer o) {
		testRequirementSet.addObserver(o);
	}

	public void deleteObserverTestRequirement(Observer o) {
		testRequirementSet.deleteObserver(o);
	}

	public void addTestRequirement(Path<Integer> newTestRequirement) {
		testRequirementSet.add(newTestRequirement);
		unSelectTestRequirements();
	}

	public void removeSelectedTestRequirement() {
		if (testRequirementSet.isInfeasible(selectedTestRequirement))
			testRequirementSet.disableInfeasible(selectedTestRequirement);
		testRequirementSet.remove(selectedTestRequirement);
		unSelectTestRequirements();
	}

	public void cleanTestRequirementSet() {
		testRequirementSet.clear();
	}

	public int size() {
		return testRequirementSet.size();
	}

	public void enableInfeasible(AbstractPath<Integer> abstractPath) {
		testRequirementSet.enableInfeasible(abstractPath);
	}

	public void disableInfeasible(AbstractPath<Integer> abstractPath) {
		testRequirementSet.disableInfeasible(abstractPath);
	}

	public int sizeInfeasibles() {
		return testRequirementSet.sizeInfeasibles();
	}

	public boolean isInfeasiblesTestRequirements(
			AbstractPath<Integer> abstractPath) {
		return testRequirementSet.isInfeasible(abstractPath);
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
		}
		return null; //TODO
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
		testRequirementSet.generateTestRequirements(algorithm);
	}

	public Set<Path<Integer>> getTestPathCoverage(Path<Integer> seletedTestPath) {
		TourType selectedTourType = Activator.getDefault()
				.getTestPathController().getSelectedTourType();
		switch (selectedTourType) {
		case TOUR:
			return testRequirementSet.getPathToured(seletedTestPath);
		case SIDETRIP:
			return testRequirementSet
					.getPathsTouredWithSideTrip(seletedTestPath);
		case DETOUR:
			return testRequirementSet.getPathsTouredWithDeTour(seletedTestPath);
		default:
			return null;//TODO
		}
	}
}