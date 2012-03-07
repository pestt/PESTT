package domain;

import domain.constants.Layer;
import domain.constants.TourType;
import domain.coverage.instrument.CoverageDataController;
import domain.coverage.instrument.CoverageDataSet;


public class PESTT { 

	private SourceGraph sourceGraph;
	private SourceGraphController sourceGraphController;
	private TestRequirementSet testRequirementSet;
	private TestRequirementController testRequirementController;
	private TestPathSet testPathSet;
	private TestPathController testPathController;
	private CoverageDataSet coverageDataSet;
	private CoverageDataController coverageDataController;
	private StatisticsSet statisticsSet;
	private StatisticsController statisticsController;
	private EditorController editorController;
	private CFGController cfgController;
	
	public PESTT() {
		editorController = new EditorController();
		sourceGraph = new SourceGraph();
		sourceGraphController = new SourceGraphController(sourceGraph);
		testRequirementSet = new TestRequirementSet();
		testRequirementController = new TestRequirementController(sourceGraph, testRequirementSet);
		testRequirementController.selectTourType(TourType.TOUR.toString());
		testPathSet = new TestPathSet();
		testPathController = new TestPathController(testPathSet);
		coverageDataSet = new CoverageDataSet();
		coverageDataController = new CoverageDataController(coverageDataSet);
		statisticsSet = new StatisticsSet();
		statisticsController = new StatisticsController(statisticsSet);
		cfgController = new CFGController();
		cfgController.selectLayer(Layer.EMPTY.toString());
	}
	
	public SourceGraphController getSourceGraphController() {
		return sourceGraphController;
	}
	
	public TestRequirementController getTestRequirementController() {
		return testRequirementController;
	}
	
	public TestPathController getTestPathController() {
		return testPathController;
	}
	
	public CoverageDataController getCoverageDataController() {
		return coverageDataController;
	}
	
	public StatisticsController getStatisticsController() {
		return statisticsController;
	}

	public EditorController getEditorController() {
		return editorController;
	}

	public CFGController getCFGController() {
		return cfgController;
	}
}