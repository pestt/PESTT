package domain;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Colors;
import domain.constants.Layer;
import domain.constants.StatisticsElements;
import domain.coverage.instrument.ICoverageData;

public class StatisticsSet extends Observable{
	
	private List<String> statisticsSet;
	private Graph<Integer> graph;
	private Iterator<Path<Integer>> iterator;
	
	public StatisticsSet() {
		statisticsSet = new ArrayList<String>();
	}
	
	public void clean() {
		statisticsSet.clear();
		setChanged();
		notifyObservers(new StatisticsChangedEvent(statisticsSet));
	}

	@SuppressWarnings("unchecked")
	public void getStatsitics() {
		graph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		Path<Integer> selected = (Path<Integer>) Activator.getDefault().getTestPathController().getSelectedTestPath();
		statisticsSet.clear();
		statisticsSet.add(getNodesStatistics(selected));
		statisticsSet.add(getEdgesStatistics(selected));
		statisticsSet.add(getLinesStatistics(selected));
		statisticsSet.add(getTestRequirementStatistics(selected));
		setChanged();
		notifyObservers(new StatisticsChangedEvent(statisticsSet));
	}
	
	public void getTotalStatsitics() {
		graph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		statisticsSet.clear();
		statisticsSet.add(getTotalNodesStatistics());
		statisticsSet.add(getTotalEdgesStatistics());
		statisticsSet.add(getTotalLinesStatistics());
		statisticsSet.add(getTotalTestRequirementsStatistics());
		setChanged();
		notifyObservers(new StatisticsChangedEvent(statisticsSet));
	}
	
	private String getNodesStatistics(Path<Integer> path) {
		String unit = StatisticsElements.NODES;;
		int total = getTotalNodes();
		int passed = getCoveredNodes(path).size();
		String percentage = getPercentage(passed, total);
		return individualOutpu(unit, passed, total, percentage);
	}
	
	private String getTotalNodesStatistics() {
		String unit = StatisticsElements.NODES;;
		iterator = Activator.getDefault().getTestPathController().iterator();
		List<Node<Integer>> nodes = new LinkedList<Node<Integer>>();
		List<Node<Integer>> aux;
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			aux = getCoveredNodes(path);
			for(Node<Integer> node : aux)
				if(!nodes.contains(node))
					nodes.add(node);
		}
		int total = getTotalNodes();
		int passed = nodes.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	
	private List<Node<Integer>> getCoveredNodes(Path<Integer> path) {
		return path.getPathNodes();
	}
	
	private int getTotalNodes() {
		return graph.getNodes().size();
	}
	
	private String getEdgesStatistics(Path<Integer> path) {
		String unit = StatisticsElements.EDGES;;
		int total = getTotalEdges();
		int passed = getCoveredEdges(path).size();
		String percentage = getPercentage(passed, total);
		return individualOutpu(unit, passed, total, percentage);
	}
	
	private String getTotalEdgesStatistics() {
		String unit = StatisticsElements.EDGES;;
		iterator = Activator.getDefault().getTestPathController().iterator();
		List<Edge<Integer>> edges = new LinkedList<Edge<Integer>>();
		List<Edge<Integer>> aux;
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
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
		for(int i = 0; i < path.getPathNodes().size(); i++)
			if(i + 1 < path.getPathNodes().size()) {
				Node<Integer> nodeFrom = path.getPathNodes().get(i);
				Node<Integer> nodeTo = path.getPathNodes().get(i + 1);
				for(Edge<Integer> edge : graph.getNodeEdges(nodeFrom))
					if(edge.getEndNode() == nodeTo && !edges.contains(edge))
						edges.add(edge);
			}
		return edges;
	}
	
	private int getTotalEdges() {
		int edges = 0;
		for(Node<Integer> node : graph.getNodes())
			edges += graph.getNodeEdges(node).size();
		return edges;
	}
	
	private String getLinesStatistics(Path<Integer> path) {
		String unit = StatisticsElements.LINES;;
		int total = getTotalLines();
		int passed = getCoveredLines(path).size();
		String percentage = getPercentage(passed, total);
		return individualOutpu(unit, passed, total, percentage);
	}
	
	private String getTotalLinesStatistics() {
		String unit = StatisticsElements.LINES;;
		iterator = Activator.getDefault().getTestPathController().iterator();
		List<Integer> lines = new LinkedList<Integer>();
		List<Integer> aux;
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
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
	
	private String getTestRequirementStatistics(Path<Integer> path) {
		String unit = StatisticsElements.TESTREQUIREMENTS;;
		int total = getTotalTestRequirements();
		int passed = getCoveredTestRequirements(path).size();
		String percentage = getPercentage(passed, total);
		return individualOutpu(unit, passed, total, percentage);
	}
	
	private String getTotalTestRequirementsStatistics() {
		String unit = StatisticsElements.TESTREQUIREMENTS;;
		iterator = Activator.getDefault().getTestPathController().iterator();
		List<Path<Integer>> testRequirements = new LinkedList<Path<Integer>>();
		List<Path<Integer>> aux;
		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			aux = getCoveredTestRequirements(path);
			for(Path<Integer> testRequirement : aux)
				if(!testRequirements.contains(testRequirement))
					testRequirements.add(testRequirement);
		}
		int total = getTotalTestRequirements();
		int passed = testRequirements.size();
		String percentage = getPercentage(passed, total);
		return totalOutpu(unit, passed, total, percentage);
	}
	
	private List<Path<Integer>> getCoveredTestRequirements(Path<Integer> path) {
		return Activator.getDefault().getTestRequirementController().getTestPathCoverage(path);		
	}
	
	private int getTotalTestRequirements() {
		return Activator.getDefault().getTestRequirementController().size();
	}
	
	private String getPercentage(int passed, int total) {
		DecimalFormat formater = new DecimalFormat("#,##0.0");
		return formater.format(((double) passed / (double) total) * 100) + "%";
	}
	
	private String individualOutpu(String unit, int passed, int total, String percentage) {
		return "Number of " + unit + " covered: " + passed + " of " + total + " (" + percentage + ")";
	}
	
	private String totalOutpu(String unit, int passed, int total, String percentage) {
		return "Total of " + unit + " covered for all tests: " + passed + " of " + total + " (" + percentage + ")";
	}
}