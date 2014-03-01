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
import adt.graph.AbstractPath;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Layer;
import domain.coverage.data.ICoverageData;

public class StatisticsSet extends Observable implements Iterable<String> {

	private List<String> statisticsSet;
	private Graph graph;

	public StatisticsSet() {
		statisticsSet = new ArrayList<String>();
	}

	public void clean() {
		statisticsSet.clear();
		setChanged();
		notifyObservers(new StatisticsChangedEvent(iterator()));
	}

	public void getStatistics(Set<Path> selectedTestPaths) {
		graph = Activator.getDefault().getSourceGraphController()
				.getSourceGraph();
		statisticsSet.clear();
		statisticsSet.add(getNodesStatistics(selectedTestPaths));
		statisticsSet.add(getEdgesStatistics(selectedTestPaths));
		statisticsSet.add(getLinesStatistics(selectedTestPaths));
		statisticsSet.add(getTestRequirementsStatistics());
		statisticsSet.add(getInfeasiblesStatistics());
		statisticsSet.add(getCyclomaticComplexity());
		statisticsSet.add(getMinOfTestsRequired());
		setChanged();
		notifyObservers(new StatisticsChangedEvent(iterator()));
	}

	private String getNodesStatistics(Set<Path> selectedTestPaths) {
		String unit = StatisticsElements.NODES;
		;
		List<Node> nodes = new LinkedList<Node>();
		for (Path path : selectedTestPaths) {
			for (Node node : path)
				if (!nodes.contains(node))
					nodes.add(node);
		}
		int total = getTotalNodes();
		int passed = nodes.size();
		String percentage = getPercentage(passed, total);
		return totalOutput(unit, passed, total, percentage);
	}

	private int getTotalNodes() {
		return graph.size();
	}

	private String getEdgesStatistics(Set<Path> selectedTestPaths) {
		String unit = StatisticsElements.EDGES;
		;
		List<Edge> edges = new LinkedList<Edge>();
		List<Edge> aux;
		for (Path path : selectedTestPaths) {
			aux = getCoveredEdges(path);
			for (Edge edge : aux)
				if (!edges.contains(edge))
					edges.add(edge);
		}
		int total = getTotalEdges();
		int passed = edges.size();
		String percentage = getPercentage(passed, total);
		return totalOutput(unit, passed, total, percentage);
	}

	private List<Edge> getCoveredEdges(Path path) {
		List<Edge> edges = new LinkedList<Edge>();
		Iterator<Node> it = path.iterator();
		Node nodeFrom = it.next();
		while (it.hasNext()) {
			Node nodeTo = it.next();
			for (Edge edge : graph.getNodeEdges(nodeFrom))
				if (edge.getEndNode() == nodeTo && !edges.contains(edge))
					edges.add(edge);
			nodeFrom = nodeTo;
		}
		return edges;
	}

	private int getTotalEdges() {
		int edges = 0;
		for (Node node : graph.getNodes())
			edges += graph.getNodeEdges(node).size();
		return edges;
	}

	private String getLinesStatistics(Set<Path> selectedTestPaths) {
		String unit = StatisticsElements.LINES;
		;
		List<Integer> lines = new LinkedList<Integer>();
		List<Integer> aux;
		for (Path path : selectedTestPaths) {
			aux = getCoveredLines(path);
			for (int line : aux)
				if (!lines.contains(line))
					lines.add(line);
		}
		int total = getTotalLines();
		int passed = lines.size();
		String percentage = getPercentage(passed, total);
		return totalOutput(unit, passed, total, percentage);
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getCoveredLines(Path path) {
		ICoverageData data = Activator.getDefault().getCoverageDataController()
				.getCoverageData(path);
		List<Integer> lines = new LinkedList<Integer>();
		graph = Activator.getDefault().getSourceGraphController()
				.getSourceGraph();
		graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for (Node node : graph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph
					.getMetadata(node); // get the information in this layer to this node.
			if (map != null)
				for (Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if (data.getLineStatus(line).equals(Colors.GREEN_ID))
						lines.add(line);
				}
		}
		return lines;
	}

	@SuppressWarnings("unchecked")
	private int getTotalLines() {
		List<Integer> lines = new LinkedList<Integer>();
		for (Node node : graph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph
					.getMetadata(node); // get the information in this layer to this node.
			if (map != null)
				for (Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if (!lines.contains(line))
						lines.add(line);
				}
		}
		return lines.size();
	}

	private String getTestRequirementsStatistics() {
		String unit = StatisticsElements.TESTREQUIREMENTS;
		;
		Set<Path> testRequirements = getTestPathCoverage();
		int total = getTotalTestRequirements();
		int passed = testRequirements.size();
		String percentage = getPercentage(passed, total);
		return totalOutput(unit, passed, total, percentage);
	}

	private Set<Path> getTestPathCoverage() {
		return Activator.getDefault().getTestPathController()
				.getTestRequirementCoverage();
	}

	private int getTotalTestRequirements() {
		return Activator.getDefault().getTestRequirementController().testRequirementsSize();
	}

	private String getInfeasiblesStatistics() {
		String unit = StatisticsElements.INFEASIBLES;
		int infeasibles = getInfeasible();
		return totalOutput(unit, infeasibles, 0, "");
	}

	private int getInfeasible() {
		return Activator.getDefault().getTestRequirementController()
				.infeasiblesSize();
	}

	private String getCyclomaticComplexity() {
		String unit = StatisticsElements.CYCLOMATIC;
		int edges = getTotalEdges();
		int nodes = getTotalNodes();
		int finalNodes = getTotalFinalNodes();
		int complexity = edges - nodes + finalNodes;
		return totalOutput(unit, complexity, 0, "");
	}

	private int getTotalFinalNodes() {
		int size = 0;
		Iterator<Node> iterator = graph.getFinalNodes().iterator();
		while (iterator.hasNext()) {
			size++;
			iterator.next();
		}
		return size;
	}

	private String getMinOfTestsRequired() {
		String unit = StatisticsElements.TESTREQUIRED;
		Iterable<AbstractPath> automatic = Activator.getDefault()
				.getTestRequirementController().getTestRequirements();
		Iterable<Path> manually = Activator.getDefault()
				.getTestRequirementController()
				.getTestRequirementsManuallyAdded();
		int begin = 0;
		int end = 0;
		for (AbstractPath path : automatic) {
			if (graph.isInitialNode(path.from()))
				begin++;
			if (graph.isFinalNode(path.to()))
				end++;
		}
		for (Path path : manually) {
			if (graph.isInitialNode(path.from()))
				begin++;
			if (graph.isFinalNode(path.to()))
				end++;
		}
		int required = Math.max(begin, end);
		return totalOutput(unit, required, 0, "");
	}

	private String getPercentage(int passed, int total) {
		DecimalFormat formatter = new DecimalFormat("#,##0.0");
		if (passed == 0 && total == 0)
			return "0%";
		return formatter.format(((double) passed / (double) total) * 100) + "%";
	}

	private String totalOutput(String unit, int passed, int total,
			String percentage) {
		if (unit.equals(StatisticsElements.CYCLOMATIC))
			return unit + ": " + passed;
		if (unit.equals(StatisticsElements.TESTREQUIRED))
			return unit + ": " + passed;
		if (unit.equals(StatisticsElements.INFEASIBLES))
			return "Total of " + unit + " paths: " + passed;
		if (unit.equals(StatisticsElements.TESTREQUIREMENTS))
			if (getInfeasible() != 0)
				return "Total of " + unit + " covered for all tests: " + passed
						+ " of " + total + " (total) - " + getInfeasible()
						+ " (infeasibles) " + " ("
						+ getPercentage(passed, total - getInfeasible()) + ")";
		return "Total of " + unit + " covered for all tests: " + passed
				+ " of " + total + " (" + percentage + ")";
	}

	@Override
	public Iterator<String> iterator() {
		return statisticsSet.iterator();
	}
}