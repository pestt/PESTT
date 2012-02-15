package statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ASTNode;

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

public class IndividualBasicStatistics implements IStatistics {
		
	private Graph<Integer> sourceGraph;
	private Object executedGraph;
	private ICoverageData data;
	private ArrayList<String> coverageStatistics;
	private ArrayList<Path<Integer>> coveredPaths;
	private ICoverageAlgorithms<Integer> testRequirementPaths;
	
	@SuppressWarnings("unchecked")
	public IndividualBasicStatistics(ArrayList<Object> param) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		this.executedGraph = (Object) param.get(0);
		this.data = (ICoverageData ) param.get(1);
		this.coveredPaths = (ArrayList<Path<Integer>>) param.get(2);
		this.testRequirementPaths = (ICoverageAlgorithms<Integer>) param.get(3);
		coverageStatistics = new ArrayList<String>();
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
		int passed;
		if(executedGraph instanceof Graph<?>)
			passed = ((Graph<Integer>) executedGraph).getNodes().size();
		else {
			ArrayList<Node<Integer>> aux = new ArrayList<Node<Integer>>();
			for(Node<Integer> node : ((Path<Integer>) executedGraph).getPathNodes())
				if(!aux.contains(node))
					aux.add(node);
			passed = aux.size();
		}
		int total = sourceGraph.getNodes().size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	@SuppressWarnings("unchecked")
	private String setEdgesCoverageStatistics() {
		String unit = Statistics_ID.EDGES;
		int passed = 0;
		int total = 0;
		if(executedGraph instanceof Graph<?>)
			for(Node<Integer> node : ((Graph<Integer>) executedGraph).getNodes())
				passed += ((Graph<Integer>) executedGraph).getNodeEdges(node).size();
		else {
			for(int i = 0; i < ((Path<Integer>) executedGraph).getPathNodes().size(); i++)
				if(i + 1 < ((Path<Integer>) executedGraph).getPathNodes().size())
					passed++;
		}		
		for(Node<Integer> node : sourceGraph.getNodes())
			total += sourceGraph.getNodeEdges(node).size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	@SuppressWarnings("unchecked")
	private String setLinesCoverageStatistics() {
		String unit = Statistics_ID.LINES;
		int passed = 0;
		int total = 0;
		sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			LinkedHashMap<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					if(data.getLineStatus(line).equals(Colors_ID.GRENN_ID))
						passed++;
					total++;
				}
		}
			
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	private String setTestRequirementsCoverageStatistics() {
		String unit = Statistics_ID.TESTREQUIREMENTS;
		int passed = coveredPaths.size();
		int total = testRequirementPaths.getTestRequirements().size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	private String getPercentage(int passed, int total) {
		DecimalFormat formater = new DecimalFormat("#,##0.0");
		return formater.format(((double) passed / (double) total) * 100) + "%";
	}
	
	private String createString(String unit, int passed, int total, String percentage) {
		return "Number of " + unit + " coverage: " + passed + " of " + total + " (" + percentage + ")";
	}
}
