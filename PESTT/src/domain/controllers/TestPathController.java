package domain.controllers;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.constants.Colors;
import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.TestPathSet;
import domain.constants.Layer;
import domain.constants.TourType;
import domain.coverage.instrument.CoverageData;
import domain.coverage.instrument.ICoverageData;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;

public class TestPathController extends Observable {

	private TestPathSet testPathSet;
	private Set<Path<Integer>> selectedTestPaths;
	private TourType selectedTourType;
	
	public TestPathController(TestPathSet testPathSet) {
		this.testPathSet = testPathSet;
	}
	
	public void addObserverTestPath(Observer o) {
		testPathSet.addObserver(o);
	}

	public void add(Path<Integer> newTestPath) {
		testPathSet.add(newTestPath);
		selectTestPath(null);
	}

	public void remove() {
		testPathSet.remove(selectedTestPaths);
		selectTestPath(null);
	}
	
	public void clear() {
		testPathSet.clear();
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
		if(selected.equals(TourType.DETOUR))
			this.selectedTourType = TourType.DETOUR;
		else if(selected.equals(TourType.SIDETRIP))
			this.selectedTourType = TourType.SIDETRIP;
		else 
			this.selectedTourType = TourType.TOUR;
	}
	
	public Iterator<Path<Integer>> iterator() {
		return testPathSet.iterator();
	}
	
	public void getStatistics() {
		Activator.getDefault().getStatisticsController().getStatistics(selectedTestPaths);
	}


	public Set<Path<Integer>> getTestRequirementCoverage() {
		Set<Path<Integer>> total = new TreeSet<Path<Integer>>();
		for(Path<Integer> path : selectedTestPaths) {
			for(Path<Integer> p : Activator.getDefault().getTestRequirementController().getTestPathCoverage(path))
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
		notifyObservers(new TestPathChangedEvent(iterator()));
	}

	
	
}