package statistics;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.InfinitePath;
import sourcegraph.Node;
import sourcegraph.Path;
import view.GraphsCreator;
import constants.Colors_ID;
import constants.Graph_ID;
import constants.Layer_ID;
import constants.Messages_ID;
import constants.Statistics_ID;
import coverage.FakeCoverageData;
import coverage.ICoverageData;
import editor.Line;

public class TotalBasicStatistics implements IStatistics {
		
	private Graph<Integer> sourceGraph;
	private List<Object> executedGraphs;
	private List<List<ICoverageData>> dataList;
	private List<ICoverageData> datas;
	private List<String> coverageStatistics;
	private List<List<Path<Integer>>> coveredPaths;
	private List<Path<Integer>> testRequirementPaths;
	
	@SuppressWarnings("unchecked")
	public TotalBasicStatistics(List<Object> param) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		this.executedGraphs = (List<Object>) param.get(0);
		this.dataList = (List<List<ICoverageData>>) param.get(1);
		this.datas = (List<ICoverageData>) param.get(2);
		this.coveredPaths = (List<List<Path<Integer>>>) param.get(3);
		this.testRequirementPaths = (List<Path<Integer>>) param.get(4);
		coverageStatistics = new LinkedList<String>();
		setCoverageStatistics();
	}

	public List<String> getStatistics() {
		return coverageStatistics;
	}
	
	private void setCoverageStatistics() {
		boolean infinite = false;
		for(Path<Integer> path : testRequirementPaths)
			if(path instanceof InfinitePath<?>) {
				infinite = true;
				break;
			}
		if(!infinite) {
			coverageStatistics.add(setNodesCoverageStatistics());
			if(sourceGraph.getNodes().size() > 1)
				coverageStatistics.add(setEdgesCoverageStatistics());
			coverageStatistics.add(setLinesCoverageStatistics());
			coverageStatistics.add(setTestRequirementsCoverageStatistics());
		} else {
			coverageStatistics.add(Messages_ID.SHOW_STATISTICS_MSG);
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages_ID.SHOW_STATISTICS_TITLE, Messages_ID.SHOW_STATISTICS_MSG);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String setNodesCoverageStatistics() {
		String unit = Statistics_ID.NODES;;
		List<Node<Integer>> nodes = new LinkedList<Node<Integer>>();
		for(Object executedGraph : executedGraphs)
			if(executedGraph instanceof Graph<?>) {
				for(Node<Integer> node : ((Graph<Integer>) executedGraph).getNodes())
					if(!nodes.contains(node))
						nodes.add(node);
			} else if(executedGraph instanceof Path<?>){
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
		List<Edge<Integer>> edges = new LinkedList<Edge<Integer>>();
		for(Object executedGraph : executedGraphs) 
			if(executedGraph instanceof Graph<?>) {
				for(Node<Integer> node : ((Graph<Integer>) executedGraph).getNodes())
					for(Edge<Integer> edge : ((Graph<Integer>) executedGraph).getNodeEdges(node))
						if(!edges.contains(edge))
							edges.add(edge);
			} else if(executedGraph instanceof Path<?>) {
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
		LinkedHashMap<Integer, String> fakeLineStatus = new LinkedHashMap<Integer, String>();
		List<Integer> lines = new LinkedList<Integer>();
		sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null)
				for(Entry<ASTNode, Line> entry : map.entrySet()) {
					int line = entry.getValue().getStartLine();
					for(ICoverageData data : datas)
						if(data.getLineStatus(line).equals(Colors_ID.GRENN_ID))
							if(!lines.contains(line))
								lines.add(line);

						total++;
						if(!fakeLineStatus.containsKey(line))
							fakeLineStatus.put(line, Colors_ID.RED_ID);
				}
		}
		
		for(int line : lines) {
			fakeLineStatus.remove(line);
			fakeLineStatus.put(line, Colors_ID.GRENN_ID);
		}
		
		List<ICoverageData> coverageLine = new LinkedList<ICoverageData>();
		coverageLine.add(new FakeCoverageData(fakeLineStatus));
		dataList.add(coverageLine);
		int passed = lines.size();
		String percentage = getPercentage(passed, total);
		return createString(unit, passed, total, percentage);
	}
	
	private String setTestRequirementsCoverageStatistics() {
		String unit = Statistics_ID.TESTREQUIREMENTS;
		int total = testRequirementPaths.size();
		List<Path<Integer>> aux = new LinkedList<Path<Integer>>();
		for(List<Path<Integer>> paths : coveredPaths)
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
