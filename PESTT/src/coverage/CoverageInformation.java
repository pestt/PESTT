package coverage;

import java.util.LinkedList;
import java.util.List;

import sourcegraph.Graph;
import sourcegraph.Path;
import statistics.StatisticsFactory;
import view.GraphsCreator;
import constants.Graph_ID;
import constants.Statistics_ID;
import editor.ActiveEditor;
import graphvisitors.ExecutedGraphVisitor;

public class CoverageInformation implements ICoverage {
	
	private Graph<Integer> sourceGraph;
	private CodeCoverage codeCoverage;
	private List<List<ICoverageData>> data;
	private List<Object> executedGraphs;
	private List<Path<Integer>> coveredPaths;
	
	@SuppressWarnings("unchecked")
	public CoverageInformation(ActiveEditor editor) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		codeCoverage = new CodeCoverage(editor);
		data = codeCoverage.getCodeCoverageStatus();
		executedGraphs = new LinkedList<Object>();
	}
	
	public List<Object> getExecutedGraphs() {
		executedGraphs.clear();
		for(int i = 0; i < data.size(); i++) {
			ExecutedGraphVisitor<Integer> executedGraphVisitor = new ExecutedGraphVisitor<Integer>(data.get(i).get(0));
			sourceGraph.accept(executedGraphVisitor);
			Graph<Integer> executedGraph = executedGraphVisitor.getExecutedgraph();
			executedGraphs.add(executedGraph);
		}
		return executedGraphs;
	}
	
	@SuppressWarnings("unchecked")
	public List<Path<Integer>> getCoveredPaths(Object executedGraph, List<Path<Integer>> testRequirements) {	
		coveredPaths = new LinkedList<Path<Integer>>();
		if(executedGraph instanceof Graph<?>) {
			for(Path<Integer> path : testRequirements) 
				if(((Graph<Integer>) executedGraph).isPath(path))
					coveredPaths.add(path);
		} else if(executedGraph instanceof Path<?>) {
			for(Path<Integer> path : testRequirements) 
				if(((Path<Integer>) executedGraph).isSubPath(path))
					coveredPaths.add(path);
		} else {
			List<Path<Integer>> total = new LinkedList<Path<Integer>>();
			for(Object obj : executedGraphs) 
				if(!(obj instanceof String)) {
					List<Path<Integer>> aux = getCoveredPaths(obj, testRequirements);
					for(Path<Integer> covered : aux)
						if(!total.contains(covered))
							total.add(covered);
				}
			coveredPaths = total;
		}
		
		return coveredPaths;
	}
	
	public int getStatusOfRun(Object executedGraph) {			
		return executedGraphs.indexOf(executedGraph);
	}
	
	public List<ICoverageData> getCoverageStatus(int index) {
		return data.get(index);
	}
	
	public List<List<ICoverageData>> getCoverageData() {
		return data;
	}
	
	public List<String> getCoverageStatistics(int index, Object executedGraph, List<Path<Integer>> testRequirements) {
		List<Object> param = new LinkedList<Object>();
		if(executedGraph instanceof String) {
			param.add(executedGraphs);
			if(data.size() > getStatusOfRun(executedGraph))
				data.remove(getStatusOfRun(executedGraph));
			param.add(data);
			param.add(getCoveredData());
			param.add(getCoveredPaths(testRequirements));
			param.add(testRequirements);
			return new StatisticsFactory().getStatisticType(Statistics_ID.TOTAL_BASIC_ID, param).getStatistics();
		} else {
			param.clear();
			param.add(executedGraph);
			param.add(data.get(index).get(0));
			param.add(coveredPaths);
			param.add(testRequirements);
			return new StatisticsFactory().getStatisticType(Statistics_ID.INDIVIDUAL_BASIC_ID, param).getStatistics();
		}
	}
	
	private List<ICoverageData> getCoveredData() {
		List<ICoverageData> aux = new LinkedList<ICoverageData>();
		for(List<ICoverageData> dataList : data)
			for(ICoverageData iData : dataList)
				aux.add(iData);
		return aux;	
	}
	
	private List<List<Path<Integer>>> getCoveredPaths(List<Path<Integer>> testRequirements) {
		List<List<Path<Integer>>> coveredPaths = new LinkedList<List<Path<Integer>>>();
		for(Object executedGraph : executedGraphs)
			coveredPaths.add(getCoveredPaths(executedGraph, testRequirements));
		return coveredPaths;
	}
}
