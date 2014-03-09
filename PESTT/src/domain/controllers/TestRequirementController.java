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
import domain.SourceGraph;
import domain.TestSuiteCatalog;
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
	private AbstractPath selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;
	private TestSuiteCatalog testSuiteCatalog;

	public TestRequirementController(SourceGraph sourceGraph,
			TestSuiteCatalog testSuiteCatalog) {
		this.sourceGraph = sourceGraph;
		this.testSuiteCatalog= testSuiteCatalog;
	}
	
	public void addTestRequirement(Path newTestRequirement) {
		testSuiteCatalog.getMethodUnderTest().addManualTestRequirement(newTestRequirement);
		notifyObserversTestReqChanged();
		testSuiteCatalog.flushCurrentProject();
	}

	private void notifyObserversTestReqChanged() {
		unSelectTestRequirements();
		setChanged();
		notifyObservers(new TestRequirementChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteCatalog.getMethodUnderTest().hasInfinitePath()));
	}

	public void removeSelectedTestRequirement() {
		testSuiteCatalog.getMethodUnderTest().removeTestRequirement(selectedTestRequirement);
		notifyObserversTestReqChanged();
		testSuiteCatalog.flushCurrentProject();
	}

	public void clearTestRequirementSet() {
		testSuiteCatalog.getMethodUnderTest().clearTestRequirements();
		notifyObserversTestReqChanged();
		testSuiteCatalog.flushCurrentProject();
	}

	public int testRequirementsSize() {
		return testSuiteCatalog.getMethodUnderTest().testRequirementsSize();
	}

	public void setInfeasible(AbstractPath abstractPath, boolean status) {
		testSuiteCatalog.getMethodUnderTest().setInfeasible(abstractPath, status);
		setChanged();
		notifyObservers(new InfeasibleChangedEvent(getTestRequirements(),
				getInfeasiblesTestRequirements(),
				getTestRequirementsManuallyAdded(), 
				testSuiteCatalog.getMethodUnderTest().hasInfinitePath()));
		testSuiteCatalog.flushCurrentProject();
	}

	public int infeasiblesSize() {
		return testSuiteCatalog.getMethodUnderTest().infeasiblesSize();
	}

	public boolean isInfeasiblesTestRequirements(
			AbstractPath abstractPath) {
		return testSuiteCatalog.getMethodUnderTest().isInfeasible(abstractPath);
	}

	public Iterable<AbstractPath> getInfeasiblesTestRequirements() {
		return testSuiteCatalog.getMethodUnderTest().getInfeasiblesTestRequirements();
	}

	public Iterable<Path> getTestRequirementsManuallyAdded() {
		return testSuiteCatalog.getMethodUnderTest().getManuallyAddedTestPaths();
	}

	public Iterable<AbstractPath> getTestRequirements() {
		return testSuiteCatalog.getMethodUnderTest().getTestRequirements();
	}

	public Path createTestRequirement(String input) {
		boolean validPath = true;
		List<String> insertedNodes = getInsertedNodes(input);
		List<Node> pathNodes = new LinkedList<Node>();
		try {
			List<Node> fromToNodes = new ArrayList<Node>(2);
			fromToNodes.add(sourceGraph.getSourceGraph().getNode(
					Integer.parseInt(insertedNodes.get(0))));
			int i = 1;
			while (i < insertedNodes.size() && validPath) {
				fromToNodes.add(sourceGraph.getSourceGraph().getNode(
						Integer.parseInt(insertedNodes.get(i))));
				if (fromToNodes.get(0) != null
						&& fromToNodes.get(1) != null
						&& sourceGraph.getSourceGraph().isPath(
								new Path(fromToNodes))) {
					pathNodes.add(fromToNodes.get(0));
					fromToNodes.remove(0);
				} else
					validPath = false;
				i++;
			}
			if (validPath) {
				pathNodes.add(fromToNodes.get(0));
				return new Path(pathNodes);
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

	public AbstractPath getSelectedTestRequirement() {
		return selectedTestRequirement;
	}

	public void selectTestRequirement(AbstractPath selected) {
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
		ICoverageAlgorithms algorithm = CoverageAlgorithmsFactory.INSTANCE
				.getCoverageAlgorithm(sourceGraph, selectedCoverageAlgorithm);
		testSuiteCatalog.getMethodUnderTest().generateTestRequirements(algorithm);
		notifyObserversTestReqChanged();
	}

	public Set<Path> getTestPathCoverage(Path seletedTestPath) {
		TourType selectedTourType = Activator.getDefault()
				.getTestPathController().getSelectedTourType();
		switch (selectedTourType) {
		case TOUR:
			return testSuiteCatalog.getMethodUnderTest().getPathToured(seletedTestPath);
		case SIDETRIP:
			return testSuiteCatalog.getMethodUnderTest().getPathsTouredWithSideTrip(seletedTestPath);
		case DETOUR:
			return testSuiteCatalog.getMethodUnderTest().getPathsTouredWithDeTour(seletedTestPath);
		default:
			return null;//normal
		}
	}
}