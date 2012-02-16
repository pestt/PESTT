package statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ASTNode;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;
import view.GraphsCreator;
import constants.Colors_ID;
import constants.Graph_ID;
import constants.Layer_ID;
import constants.Statistics_ID;
import coverage.ICoverageData;
import coveragealgorithms.ICoverageAlgorithms;
import editor.Line;

public class TotalBasicStatistics implements IStatistics {
		
	private Graph<Integer> sourceGraph;
	private ArrayList<Object> executedGraphs;
	private ArrayList<ICoverageData> datas;
	private ArrayList<String> coverageStatistics;
	private ArrayList<String> individualStatistics;
	private ArrayList<ArrayList<Path<Integer>>> coveredPaths;
	private ICoverageAlgorithms<Integer> testRequirementPaths;
	
	@SuppressWarnings("unchecked")
	public TotalBasicStatistics(ArrayList<Object> param) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		this.executedGraphs = (ArrayList<Object>) param.get(0);
		this.datas = (ArrayList<ICoverageData>) param.get(1);
		this.coveredPaths = (ArrayList<ArrayList<Path<Integer>>>) param.get(2);
		this.testRequirementPaths = (ICoverageAlgorithms<Integer>) param.get(3);
		this.individualStatistics = (ArrayList<String>) param.get(4);
		coverageStatistics = (ArrayList<String>) individualStatistics.clone();
		setCoverageStatistics();
	}

	public ArrayList<String> getStatistics() {
		return coverageStatistics;
	}
	
	private void setCoverageStatistics() {
		coverageStatistics.add(setNodesCoverageStatistics());
		if(sourceGraph.getNodes().size() > 1)
			coverageStatistics.add(setEdgesCoverageStatistics());
		coverageStatistics.add(setLinesCoverageStatistics());
		coverageStatistics.add(setTestRequirementsCoverageStatistics());
	}
	
	@SuppressWarnings("unchecked")
	private String setNodesCoverageStatistics() {
		String unit = Statistics_ID.NODES;;
		ArrayList<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		for(Object executedGraph : executedGraphs)
			if(executedGraph instanceof Graph<?>) {
				for(Node<Integer> node : ((Graph<Integer>) executedGraph).getNodes())
					if(!nodes.contains(node))
						nodes.add(node);
			} else {
				for(Node<Integer> node : ((Path<Integer>) executedGraph).getPathNodes())
					if(!nodes.contains(node))
						nodes.add(node);
			}
		
		int passed = nodes.size();
		int total = sourceGraph.getNodes().size();;
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	@SuppressWarnings("unchecked")
	private String setEdgesCoverageStatistics() {
		String unit = Statistics_ID.EDGES;
		int total = 0;
		ArrayList<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
		for(Object executedGraph : executedGraphs) 
			if(executedGraph instanceof Graph<?>) {
				for(Node<Integer> node : ((Graph<Integer>) executedGraph).getNodes())
					for(Edge<Integer> edge : ((Graph<Integer>) executedGraph).getNodeEdges(node))
						if(!edges.contains(edge))
							edges.add(edge);
			}else {
				for(int i = 0; i < ((Path<Integer>) executedGraph).getPathNodes().size(); i++)
					if(i + 1 < ((Path<Integer>) executedGraph).getPathNodes().size()) {
						Node<Integer> nodeFrom = ((Path<Integer>) executedGraph).getPathNodes().get(i);
						Node<Integer> nodeTo = ((Path<Integer>) executedGraph).getPathNodes().get(i + 1);
						for(Edge<Integer> edge : sourceGraph.getNodeEdges(nodeFrom))
							if(edge.getEndNode() == nodeTo && !edges.contains(edge))
								edges.add(edge);
					}
			}
		for(Node<Integer> node : sourceGraph.getNodes())
			total += sourceGraph.getNodeEdges(node).size();
				
		int passed = edges.size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	@SuppressWarnings("unchecked")
	private String setLinesCoverageStatistics() {
		String unit = Statistics_ID.LINES;
		int total = 0;
		ArrayList<Integer> lines = new ArrayList<Integer>();
		sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			LinkedHashMap<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					for(ICoverageData data : datas)
						if(data.getLineStatus(line).equals(Colors_ID.GRENN_ID))
							if(!lines.contains(line))
								lines.add(line);
						total++;
				}
		}
		int passed = lines.size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	private String setTestRequirementsCoverageStatistics() {
		String unit = Statistics_ID.TESTREQUIREMENTS;
		int total = testRequirementPaths.getTestRequirements().size();
		ArrayList<Path<Integer>> aux = new ArrayList<Path<Integer>>();
		for(ArrayList<Path<Integer>> paths : coveredPaths)
			for(Path<Integer> path : paths)
				if(!aux.contains(path))
				aux.add(path);
		
		int passed = aux.size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}

	private String getPercentage(int passed, int total) {
		DecimalFormat formater = new DecimalFormat("#,##0.0");
		return formater.format(((double) passed / (double) total) * 100) + "%";
	}
	
	private String createString(String unit, int passed, int total, String percentage) {
		return "Total of " + unit + " coverage for all tests: " + passed + " of " + total + " (" + percentage + ")";
	}
}
