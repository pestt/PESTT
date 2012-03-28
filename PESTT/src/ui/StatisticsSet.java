	package ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.constants.Colors;
import ui.constants.StatisticsElements;
import ui.editor.Line;
import ui.events.StatisticsChangedEvent;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Layer;
import domain.coverage.instrument.ICoverageData;

public class StatisticsSet extends Observable implements Iterable<String>{
	
	private List<String> statisticsSet;
	private Graph<Integer> graph;
	
	public StatisticsSet() {
		statisticsSet = new ArrayList<String>();
	}
	
	public void clean() {
		statisticsSet.clear();
		setChanged();
		notifyObservers(new StatisticsChangedEvent(iterator()));
	}
	
	public void getStatsitics(Set<Path<Integer>> selectedTestPaths) {
		graph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		statisticsSet.clear();
		statisticsSet.add(getNodesStatistics(selectedTestPaths));
		statisticsSet.add(getEdgesStatistics(selectedTestPaths));
		statisticsSet.add(getLinesStatistics(selectedTestPaths));
		statisticsSet.add(getTestRequirementsStatistics());
		setChanged();
		notifyObservers(new StatisticsChangedEvent(iterator()));
	}
		
	private String getNodesStatistics(Set<Path<Integer>> selectedTestPaths) {
		String unit = StatisticsElements.NODES;;
		List<Node<Integer>> nodes = new LinkedList<Node<Integer>>();
		for(Path<Integer> path : selectedTestPaths) {
			for(Node<Integer> node : path)
				if(!nodes.contains(node)) 
					nodes.add(node);
		}
		int total = getTotalNodes();
		int passed = nodes.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	private int getTotalNodes() {
		return graph.size();
	}
	
	private String getEdgesStatistics(Set<Path<Integer>> selectedTestPaths) {
		String unit = StatisticsElements.EDGES;;
		List<Edge<Integer>> edges = new LinkedList<Edge<Integer>>();
		List<Edge<Integer>> aux;
		for(Path<Integer> path : selectedTestPaths) {
			aux = getCoveredEdges(path);
			for(Edge<Integer> edge : aux)
				if(!edges.contains(edge))
					edges.add(edge);
		}
		int total = getTotalEdges();
		int passed = edges.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	private List<Edge<Integer>> getCoveredEdges(Path<Integer> path) {
		List<Edge<Integer>> edges = new LinkedList<Edge<Integer>>();
		Iterator<Node<Integer>> it = path.iterator();
		Node<Integer> nodeFrom = it.next();
		while (it.hasNext()) {
			Node<Integer> nodeTo = it.next();
			for(Edge<Integer> edge : graph.getNodeEdges(nodeFrom))
				if(edge.getEndNode() == nodeTo && !edges.contains(edge))
					edges.add(edge);
			nodeFrom = nodeTo;
		}
		return edges;
	}
	
	private int getTotalEdges() {
		int edges = 0;
		for(Node<Integer> node : graph.getNodes())
			edges += graph.getNodeEdges(node).size();
		return edges;
	}
	
	private String getLinesStatistics(Set<Path<Integer>> selectedTestPaths) {
		String unit = StatisticsElements.LINES;;
		List<Integer> lines = new LinkedList<Integer>();
		List<Integer> aux;
		for(Path<Integer> path : selectedTestPaths) {
			aux = getCoveredLines(path);
			for(int line : aux)
				if(!lines.contains(line))
					lines.add(line);
		}
		int total = getTotalLines();
		int passed = lines.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getCoveredLines(Path<Integer> path) {
		ICoverageData data = Activator.getDefault().getCoverageDataController().getCoverageData(path);
		List<Integer> lines = new LinkedList<Integer>();
		graph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : graph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if(data.getLineStatus(line).equals(Colors.GRENN_ID))
					lines.add(line);
				}
		}
		return lines;
	}
	
	@SuppressWarnings("unchecked")
	private int getTotalLines() {
		int lines = 0;
		for(Node<Integer> node : graph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				lines += map.entrySet().size();
		}
		return lines;
	}
	
	private String getTestRequirementsStatistics() {
		String unit = StatisticsElements.TESTREQUIREMENTS;;
		Set<Path<Integer>> testRequirements = getTestPathCoverage();
		int total = getTotalTestRequirements() - getInfeasible();
		int passed = testRequirements.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	private Set<Path<Integer>> getTestPathCoverage() {
		return Activator.getDefault().getTestPathController().getTestRequirementCoverage();	
	}
	
	private int getTotalTestRequirements() {
		return Activator.getDefault().getTestRequirementController().size();
	}
	
	private int getInfeasible() {
		return Activator.getDefault().getTestRequirementController().sizeInfeasibles();
	}
	
	private String getPercentage(int passed, int total) {
		DecimalFormat formater = new DecimalFormat("#,##0.0");
		return formater.format(((double) passed / (double) total) * 100) + "%";
	}
	
	private String totalOutpu(String unit, int passed, int total, String percentage) {
		return "Total of " + unit + " covered for all tests: " + passed + " of " + total + " (" + percentage + ")";
	}

	@Override
	public Iterator<String> iterator() {
		return statisticsSet.iterator();
	}
}