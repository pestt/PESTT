package domain.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.constants.Colors;
import ui.editor.Line;
import ui.events.TourChangeEvent;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Layer;
import domain.constants.TourType;
import domain.coverage.data.CoverageData;
import domain.coverage.data.ICoverageData;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;

public class TestPathController extends Observable {

	private TestSuiteController testSuiteController;
	private Set<Path> selectedTestPaths;
	private TourType selectedTourType;
	private CoverageDataController coverageDataController;

	public TestPathController(TestSuiteController testSuiteController,
			CoverageDataController coverageDataController) {
		this.testSuiteController = testSuiteController;
		testSuiteController.setTestPathController(this);
		this.coverageDataController = coverageDataController;
	}

	public void addManualTestPath(Path newTestPath, String tooltip) {
		testSuiteController.getMethodUnderTest().addManualTestPath(newTestPath);
		computeStatistics(newTestPath);
		notifyTheObservers();
		testSuiteController.flush();
	}

	public void addAutomaticTestPath(Path newTestPath, String executionTip) {
		testSuiteController.getMethodUnderTest().addAutomaticTestPath(newTestPath, executionTip);
		computeStatistics(newTestPath);
		notifyTheObservers();
		testSuiteController.flush();
	}

	private void computeStatistics(Path newTestPath) {
		List<ICoverageData> newData = new LinkedList<ICoverageData>();
		newData.add(new CoverageData(newTestPath));
		coverageDataController.addCoverageData(newTestPath, newData);
	}

	private void notifyTheObservers() {
		unSelectTestPaths();
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(),
				getManuallyAddedTestPaths()));
	}

	
	public String getExecutionTip(Path path) {
		return testSuiteController.getMethodUnderTest().getExecutionTip(path);
	}

	public void removeTestPath() {
		for (Path path: selectedTestPaths) 
			coverageDataController.removeSelectedCoverageData(path);
		testSuiteController.getMethodUnderTest().removeTestPaths(selectedTestPaths);
		notifyTheObservers();
		testSuiteController.flush();
	}

	public void clearAutomaticTestPaths() {
		testSuiteController.getMethodUnderTest().clearAutomaticTestPaths();
		notifyTheObservers();
		testSuiteController.flush();
	}

	public void clearManuallyTestPaths() {
		testSuiteController.getMethodUnderTest().clearManuallyAddedTestPaths();
		notifyTheObservers();
		testSuiteController.flush();
	}

	public void clearTestPathSet() {
		testSuiteController.getMethodUnderTest().clearAutomaticTestPaths();
		testSuiteController.getMethodUnderTest().clearManuallyAddedTestPaths();
		notifyTheObservers();
		testSuiteController.flush();
	}

	public boolean isTestPathSelected() {
		return selectedTestPaths != null;
	}

	public Set<Path> getSelectedTestPaths() {
		return selectedTestPaths;
	}

	public void selectTestPath(Set<Path> selectedTestPaths) {
		this.selectedTestPaths = selectedTestPaths;
		setChanged();
		notifyObservers(new TestPathSelectedEvent(selectedTestPaths));
	}

	public void unSelectTestPaths() {
		selectTestPath(null);
	}

	public TourType getSelectedTourType() {
		return selectedTourType;
	}

	public void selectTourType(String selected) {
		if (selected.equals(TourType.DETOUR.toString()))
			this.selectedTourType = TourType.DETOUR;
		else if (selected.equals(TourType.SIDETRIP.toString()))
			this.selectedTourType = TourType.SIDETRIP;
		else
			this.selectedTourType = TourType.TOUR;
		testSuiteController.setTourType(selectedTourType);
		setChanged();
		notifyObservers(new TourChangeEvent(selectedTourType));
	}

	public Iterable<Path> getManuallyAddedTestPaths() {
		return testSuiteController.getMethodUnderTest().getManuallyAddedTestPaths();
	}

	public Iterable<Path> getTestPaths() {
		return testSuiteController.getMethodUnderTest().getTestPaths();
	}

	public void getStatistics() {
		Activator.getDefault().getStatisticsController().getStatistics(selectedTestPaths);
	}

	public Set<Path> getTestRequirementCoverage() {
		Set<Path> total = new TreeSet<Path>();
		for (Path path : selectedTestPaths) {
			Set<Path> coveredPaths = Activator.getDefault()
					.getTestRequirementController().getTestPathCoverage(path);
			for (Path p : coveredPaths)
				if (!total.contains(p))
					total.add(p);
		}
		return total;
	}

	@SuppressWarnings("unchecked")
	public ICoverageData getCoverageData() {
		Graph sourceGraph = Activator.getDefault()
				.getSourceGraphController().getSourceGraph();
		LinkedHashMap<Integer, String> coverageData = new LinkedHashMap<Integer, String>();
		List<Integer> lines = new LinkedList<Integer>();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for (Node node : sourceGraph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph
					.getMetadata(node); // get the information in this layer to this node.
			if (map != null)
				for (Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					for (Path path : selectedTestPaths) {
						ICoverageData data = Activator.getDefault()
								.getCoverageDataController()
								.getCoverageData(path);
						if (data.getLineStatus(line).equals(Colors.GREEN_ID))
							if (!lines.contains(line))
								lines.add(line);

						if (!coverageData.containsKey(line))
							coverageData.put(line, Colors.RED_ID);
					}
				}
		}

		for (int line : lines) {
			coverageData.remove(line);
			coverageData.put(line, Colors.GREEN_ID);
		}

		return new CoverageData(coverageData);
	}

	public Path createTestPath(String input) {
		Graph sourceGraph = Activator.getDefault()
				.getSourceGraphController().getSourceGraph();
		boolean validPath = true;
		List<String> insertedNodes = getInsertedNodes(input);
		List<Node> pathNodes = new LinkedList<Node>();
		try {
			List<Node> fromToNodes = new ArrayList<Node>();
			fromToNodes.add(sourceGraph.getNode(Integer.parseInt(insertedNodes
					.get(0))));
			int i = 1;
			while (i < insertedNodes.size() && validPath) {
				fromToNodes.add(sourceGraph.getNode(Integer
						.parseInt(insertedNodes.get(i))));
				if (fromToNodes.get(0) != null && fromToNodes.get(1) != null
						&& sourceGraph.isPath(new Path(fromToNodes))) {
					pathNodes.add(fromToNodes.get(0));
					fromToNodes.remove(0);
				} else
					validPath = false;
				i++;
			}
			if (validPath) {
				pathNodes.add(fromToNodes.get(0));

				if (!sourceGraph.isInitialNode(pathNodes.get(0))
						|| !sourceGraph.isFinalNode(pathNodes.get(pathNodes
								.size() - 1)))
					return null;//!= null check

				return new Path(pathNodes);
			}
		} catch (NumberFormatException e) {
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
}