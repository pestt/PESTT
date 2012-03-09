package domain.controllers;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;
import adt.graph.Path;
import domain.SourceGraph;
import domain.TestRequirementSet;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;
import domain.coverage.algorithms.CoverageAlgorithmsFactory;
import domain.coverage.algorithms.ICoverageAlgorithms;
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
		testRequirementSet.remove(selectedTestRequirement);
		selectTestRequirement(null);
	}
	
	public void cleanTestRequirementSet() {
		testRequirementSet.clear();
	}
	
	public int size() {
		return testRequirementSet.size();
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
	
	public Iterator<Path<Integer>> iterator() {
		return testRequirementSet.iterator();
	}
}