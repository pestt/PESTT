package domain;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;
import adt.graph.Path;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;
import domain.coverage.algorithms.CoverageAlgorithmsFactory;
import domain.coverage.algorithms.ICoverageAlgorithms;


public class TestRequirementController extends Observable {

	private SourceGraph sourceGraph;
	private TestRequirementSet testRequirementSet;
	private Path<Integer> selectedTestRequirement;
	private GraphCoverageCriteriaId selectedCoverageAlgorithm;
	private TourType selectedTourType;
	
	public TestRequirementController(SourceGraph sourceGraph, TestRequirementSet testRequirementSet) {
		this.sourceGraph = sourceGraph;
		this.testRequirementSet = testRequirementSet;
	}
	
	public void addObserverTestRequirement(Observer o) {
		testRequirementSet.addObserver(o);
	}

	public void addTestRequirement(Path<Integer> newTestRequirement) {
		testRequirementSet.addTestRequirement(newTestRequirement);
	}

	public void removeSelectedTestRequirement() {
		testRequirementSet.removeTestRequirement(selectedTestRequirement);
		selectTestRequirement(null);
	}
	
	public void cleanTestRequirementSet() {
		testRequirementSet.clean();
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
		notifyObservers(new TestRequirementSelected(selected));
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
	
	public TourType getSelectedTourType() {
		return selectedTourType;
	}

	public void selectTourType(String selected) {
		if(selected.equals(TourType.TOUR))
			this.selectedTourType = TourType.TOUR;
		else if(selected.equals(TourType.SIDETRIP))
			this.selectedTourType = TourType.SIDETRIP;
		else 
			this.selectedTourType = TourType.DETOUR;
	}

	public void generateTestRequirement() {
		ICoverageAlgorithms<Integer> algorithm = CoverageAlgorithmsFactory.INSTANCE.getCoverageAlgorithm(sourceGraph, selectedCoverageAlgorithm);
		testRequirementSet.generateTestRequirements(algorithm);
	}

	public List<Path<Integer>> getTestPathCoverage(Path<Integer> seletedTestPath) {
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

	public List<Path<Integer>> getTotalTestPathCoverage() {
		Iterator<Path<Integer>> iterator = Activator.getDefault().getTestPathController().iterator();
		switch(selectedTourType) {
			case TOUR:
				return testRequirementSet.getTotalPathToured(iterator);
			case SIDETRIP:
				return testRequirementSet.getTotalPathsTouredWithSideTrip(iterator);
			case DETOUR:
				return testRequirementSet.getTotalPathsTouredWithDeTour(iterator);
			default:
				return null;	
		}
	}
	
	public Iterator<Path<Integer>> iterator() {
		return testRequirementSet.iterator();
	}
}