package main.activator;

import ui.StatisticsSet;
import ui.controllers.CFGController;
import ui.controllers.EditorController;
import ui.controllers.StatisticsController;
import ui.controllers.ViewController;
import domain.CoverageDataSet;
import domain.DefUsesSet;
import domain.SourceGraph;
import domain.TestPathSet;
import domain.TestRequirementSet;
import domain.TestSuite;
import domain.constants.Layer;
import domain.constants.TourType;
import domain.controllers.BytemanController;
import domain.controllers.CoverageDataController;
import domain.controllers.DefUsesController;
import domain.controllers.SourceGraphController;
import domain.controllers.TestPathController;
import domain.controllers.TestRequirementController;


public class PESTT { 

	private SourceGraphController sourceGraphController;
	private TestRequirementController testRequirementController;
	private TestPathController testPathController;
	private CoverageDataController coverageDataController;
	private StatisticsController statisticsController;
	private EditorController editorController;
	private CFGController cfgController;
	private DefUsesController defusesController;
	private BytemanController bytemanController;
	private ViewController viewController;
	private TestSuite testSuite;
	
	public PESTT() {
		editorController = new EditorController();
		SourceGraph sourceGraph = new SourceGraph();
		sourceGraphController = new SourceGraphController(sourceGraph);
		testRequirementController = new TestRequirementController(sourceGraph, new TestRequirementSet());
		testPathController = new TestPathController(new TestPathSet());
		testPathController.selectTourType(TourType.TOUR.toString());
		coverageDataController = new CoverageDataController(new CoverageDataSet());
		statisticsController = new StatisticsController(new StatisticsSet());
		cfgController = new CFGController();
		cfgController.selectLayer(Layer.EMPTY.toString());
		defusesController = new DefUsesController(new DefUsesSet());
		bytemanController = new BytemanController();
		viewController = new ViewController();
		testSuite = new TestSuite("default.xml");
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

	public DefUsesController getDefUsesController() {
		return defusesController;
	}

	public BytemanController getBytemanController() {
		return bytemanController;
	}

	public ViewController getViewController() {
		return viewController;
	}

	public TestSuite getTestSuite() {
		return testSuite;
	}
}