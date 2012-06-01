package domain.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
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
import domain.TestPathSet;
import domain.constants.Layer;
import domain.constants.TourType;
import domain.coverage.data.CoverageData;
import domain.coverage.data.ICoverageData;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;

public class TestPathController extends Observable {

	private static final String MANUALLY = "Manually added";
	private TestPathSet testPathSet;
	private Set<Path<Integer>> selectedTestPaths;
	private TourType selectedTourType;
	private Map<Path<Integer>, String> tooltips;
	
	public TestPathController(TestPathSet testPathSet) {
		this.testPathSet = testPathSet;
		tooltips = new HashMap<Path<Integer>, String>();
	}
	
	public void addObserverTestPath(Observer o) {
		testPathSet.addObserver(o);
	}

	public void deleteObserverTestPath(Observer o) {
		testPathSet.deleteObserver(o);
	}

	public void addTestPath(Path<Integer> newTestPath) {
		insertTooltip(newTestPath, MANUALLY);
		testPathSet.add(newTestPath);
		List<ICoverageData> newData = new LinkedList<ICoverageData>();
		newData.add(new CoverageData(newTestPath));
		Activator.getDefault().getCoverageDataController().addCoverageData(newTestPath, newData);
		selectTestPath(null);
	}
	
	public void addAutomaticTestPath(Path<Integer> newTestPath, String tooltip) {
		insertTooltip(newTestPath,tooltip);
		testPathSet.addAutomatic(newTestPath);
		List<ICoverageData> newData = new LinkedList<ICoverageData>();
		newData.add(new CoverageData(newTestPath));
		Activator.getDefault().getCoverageDataController().addCoverageData(newTestPath, newData);
		selectTestPath(null);
	}
	
	private void insertTooltip(Path<Integer> path, String tooltip) {
		Path<Integer> remove = null;
		for(Path<Integer> current : tooltips.keySet())
			if(current.toString().equals(path.toString())) {
				if(!tooltip.equals(MANUALLY))
					if(testPathSet.isManuallyAdded(current)) {
						Set<Path<Integer>> set = new TreeSet<Path<Integer>>();
						set.add(current);
						testPathSet.remove(set);
						remove = current;
						break;
					}	
				path = current;
				break;
			}
		if(remove != null)
			tooltips.remove(remove);
		tooltips.put(path, tooltip);
	}
	
	public String getTooltip(Path<Integer> path) {
		return tooltips.get(path);
	}

	public void removeTestPath() {
		for(Path<Integer> path : selectedTestPaths) {
			Activator.getDefault().getCoverageDataController().removeSelectedCoverageData(path);
			tooltips.remove(path);
		}
		testPathSet.remove(selectedTestPaths);
		selectTestPath(null);
	}
	
	public void clearAutomaticTestPaths() {
		testPathSet.clearAutomatic();
	}
	
	public void clearManuallyTestPaths() {
		testPathSet.clearManually();
	}

	public void cleanTestPathSet() {
		testPathSet.clearAll();
		tooltips.clear();
	}

	public boolean isTestPathSelected() {
		return selectedTestPaths != null;
	}
	
	public Set<Path<Integer>> getSelectedTestPaths() {
		return selectedTestPaths;
	}

	public void selectTestPath(Set<Path<Integer>> selectedTestPaths) {
		this.selectedTestPaths = selectedTestPaths;
		setChanged();
		notifyObservers(new TestPathSelectedEvent(selectedTestPaths));
	}
	
	public TourType getSelectedTourType() {
		return selectedTourType;
	}

	public void selectTourType(String selected) {
		if(selected.equals(TourType.DETOUR.toString()))
			this.selectedTourType = TourType.DETOUR;
		else if(selected.equals(TourType.SIDETRIP.toString()))
			this.selectedTourType = TourType.SIDETRIP;
		else 
			this.selectedTourType = TourType.TOUR;
		setChanged();
		notifyObservers(new TourChangeEvent(selectedTourType));
	}
	
	public Iterable<Path<Integer>> getTestPathsManuallyAdded() {
		return testPathSet.getTestPathsManuallyAdded();
	}
	
	public Iterable<Path<Integer>> getTestPaths() {
		return testPathSet.getTestPaths();
	}
	
	public void getStatistics() {
		Activator.getDefault().getStatisticsController().getStatistics(selectedTestPaths);
	}

	public Set<Path<Integer>> getTestRequirementCoverage() {
		Set<Path<Integer>> total = new TreeSet<Path<Integer>>();
		for(Path<Integer> path : selectedTestPaths) {
			Set<Path<Integer>> coveredPaths = Activator.getDefault().getTestRequirementController().getTestPathCoverage(path);
			for(Path<Integer> p : coveredPaths)
				if(!total.contains(p))
					total.add(p);
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public ICoverageData getCoverageData() {
		Graph<Integer> sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		LinkedHashMap<Integer, String> coverageData = new LinkedHashMap<Integer, String>();
		List<Integer> lines = new LinkedList<Integer>();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					for(Path<Integer> path : selectedTestPaths) {
						ICoverageData data = Activator.getDefault().getCoverageDataController().getCoverageData(path);
						if(data.getLineStatus(line).equals(Colors.GRENN_ID))
							if(!lines.contains(line))
								lines.add(line);

						if(!coverageData.containsKey(line))
							coverageData.put(line, Colors.RED_ID);
					}
				}
		}

		for(int line : lines) {
			coverageData.remove(line);
			coverageData.put(line, Colors.GRENN_ID);
		}

		return new CoverageData(coverageData);
	}
	
	public void removeCoverageData() {
		for(Path<Integer> path : selectedTestPaths)
			Activator.getDefault().getCoverageDataController().removeSelectedCoverageData(path);	
	}

	public void unSelect() {
		setChanged();
		notifyObservers(new TestPathChangedEvent(getTestPaths(), getTestPathsManuallyAdded()));
	}
	
	public Path<Integer> createTestPath(String input) {
		Graph<Integer> sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		boolean validPath = true;
		List<String> insertedNodes = getInsertedNodes(input);
		List<Node<Integer>> pathNodes = new LinkedList<Node<Integer>> ();
		try {
			List<Node<Integer>> fromToNodes = new ArrayList<Node<Integer>>();
			fromToNodes.add(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(0))));
			int i = 1; 
			while(i < insertedNodes.size() && validPath) {
				fromToNodes.add(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i))));
				if(fromToNodes.get(0) != null && fromToNodes.get(1) != null && 
						sourceGraph.isPath(new Path<Integer>(fromToNodes))) {
					pathNodes.add(fromToNodes.get(0));
					fromToNodes.remove(0);
				} else
					validPath = false;
				i++;
			}
			if (validPath) {
				pathNodes.add(fromToNodes.get(0));

				if(!sourceGraph.isInitialNode(pathNodes.get(0)) || !sourceGraph.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
					return null;

				return new Path<Integer>(pathNodes);
			}
		} catch(NumberFormatException e) {
		}
		return null;
	}
	
	private List<String> getInsertedNodes(String input) {
		List<String> aux = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		// separate the inserted nodes.
		while(strtok.hasMoreTokens())
			aux.add(strtok.nextToken());
		return aux;
	}
}