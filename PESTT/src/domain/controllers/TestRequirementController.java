package domain.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;

import main.activator.Activator;
import adt.graph.Edge;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SimplePath;
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
	private Path<Integer> selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;
	
	public TestRequirementController(SourceGraph sourceGraph, TestRequirementSet testRequirementSet) {
		this.sourceGraph = sourceGraph;
		this.testRequirementSet = testRequirementSet;
	}
	
	public void addObserverTestRequirement(Observer o) {
		testRequirementSet.addObserver(o);
	}

	public void addTestRequirement(Path<Integer> newTestRequirement) {
		testRequirementSet.add(newTestRequirement);
		selectTestRequirement(null);
	}

	public void removeSelectedTestRequirement() {
		if(testRequirementSet.isInfeasible(selectedTestRequirement))
			testRequirementSet.disableInfeasible(selectedTestRequirement);
		testRequirementSet.remove(selectedTestRequirement);
		selectTestRequirement(null);
	}
	
	public void cleanTestRequirementSet() {
		testRequirementSet.clear();
	}
	
	public int size() {
		return testRequirementSet.size();
	}
	
	public void enableInfeasible(Path<Integer> infeasible) {
		testRequirementSet.enableInfeasible(infeasible);
	}
	
	public void disableInfeasible(Path<Integer> infeasible) {
		testRequirementSet.disableInfeasible(infeasible);
	}
	
	public int sizeInfeasibles() {
		return testRequirementSet.sizeInfeasibles();
	}
	
	public Iterable<Path<Integer>> getInfeasiblesTestRequirements() {
		return testRequirementSet.getInfeasiblesTestRequirements();
	}
	
	public Iterable<Path<Integer>> getTestRequirementsManuallyAdded() {
		return testRequirementSet.getTestRequirementsManuallyAdded();
	}
	
	public Iterable<Path<Integer>> getTestRequirements() {
		return testRequirementSet.getTestRequirements();
	}
	
	public Path<Integer> createTestRequirement(String input) {
		boolean flag = true;
		List<String> insertedNodes = getInsertedNodes(input);
		Path<Integer> newTestRequirement = null;
		for(int i = 0; i < insertedNodes.size(); i++) {
			try {
				Node<Integer> nodeFrom = sourceGraph.getSourceGraph().getNode(Integer.parseInt(insertedNodes.get(i)));
				if(nodeFrom != null && flag)
					if(i + 1 < insertedNodes.size()) {
						Node<Integer> nodeTo = sourceGraph.getSourceGraph().getNode(Integer.parseInt(insertedNodes.get(i + 1)));
						for(Edge<Integer> edge : sourceGraph.getSourceGraph().getNodeEdges(nodeFrom))
							if(nodeTo == edge.getEndNode()) {
								if(newTestRequirement == null) 
									newTestRequirement = new SimplePath<Integer>(nodeFrom);
								 else 
									newTestRequirement.addNode(nodeFrom);
								flag = true;
								break;
							} else
								flag = false;
					} else
						if(newTestRequirement == null) 
							newTestRequirement = new SimplePath<Integer>(nodeFrom);
						else
							newTestRequirement.addNode(nodeFrom);
				else
					return null;
			} catch(NumberFormatException ee) {
				return null;
			}
		}
		return newTestRequirement;
	}
	
	private List<String> getInsertedNodes(String input) {
		List<String> aux = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		// separate the inserted nodes.
		while(strtok.hasMoreTokens())
			aux.add(strtok.nextToken());
		return aux;
	}
	
	public boolean isTestRequirementSelected() {
		return selectedTestRequirement != null;
	}
	
	public Path<Integer> getSelectedTestRequirement() {
		return selectedTestRequirement;
	}

	public void selectTestRequirement(Path<Integer> selected) {
		this.selectedTestRequirement = selected;
		setChanged();
		notifyObservers(new TestRequirementSelectedEvent(selected));
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
		ICoverageAlgorithms<Integer> algorithm = CoverageAlgorithmsFactory.INSTANCE.getCoverageAlgorithm(sourceGraph, selectedCoverageAlgorithm);
		testRequirementSet.generateTestRequirements(algorithm);
	}

	public Set<Path<Integer>> getTestPathCoverage(Path<Integer> seletedTestPath) {
		TourType selectedTourType = Activator.getDefault().getTestPathController().getSelectedTourType();
		switch(selectedTourType) {
			case TOUR:
				return testRequirementSet.getPathToured(seletedTestPath);
			case SIDETRIP:
				return testRequirementSet.getPathsTouredWithSideTrip(seletedTestPath);
			case DETOUR:
				return testRequirementSet.getPathsTouredWithDeTour(seletedTestPath);
			default:
				return null;	
		}
	}
}