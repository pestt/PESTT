package coverage;

import java.util.ArrayList;
import java.util.List;

import sourcegraph.Graph;
import sourcegraph.Path;
import statistics.StatisticsFactory;
import view.GraphsCreator;
import constants.Graph_ID;
import constants.Statistics_ID;
import coveragealgorithms.ICoverageAlgorithms;
import editor.ActiveEditor;
import graphvisitors.ExecutedGraphVisitor;

public class CoverageInformation implements ICoverage {
	
	private Graph<Integer> sourceGraph;
	private CodeCoverage codeCoverage;
	private List<ArrayList<ICoverageData>> data;
	private ICoverageAlgorithms<Integer> requirementSet;
	private ArrayList<Object> executedGraphs;
	private ArrayList<Path<Integer>> coveredPaths;
	
	@SuppressWarnings("unchecked")
	public CoverageInformation(ICoverageAlgorithms<Integer> requirementSet, ActiveEditor editor) {
		this.sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		this.requirementSet = requirementSet;
		codeCoverage = new CodeCoverage(editor);
		data = codeCoverage.getCodeCoverageStatus();
		executedGraphs = new ArrayList<Object>();
	}
	
	public ArrayList<Object> getExecutedGraphs() {
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
	public ArrayList<Path<Integer>> getCoveredPaths(Object executedGraph) {	
		coveredPaths = new ArrayList<Path<Integer>>();
		if(executedGraph instanceof Graph<?>) {
			for(Path<Integer> path : requirementSet.getTestRequirements()) 
				if(((Graph<Integer>) executedGraph).isPath(path))
					coveredPaths.add(path);
		} else {
			for(Path<Integer> path : requirementSet.getTestRequirements()) 
				if(((Path<Integer>) executedGraph).isSubPath(path))
					coveredPaths.add(path);
		}
		return coveredPaths;
	}
	
	public int getStatusOfRun(Object executedGraph) {			
		return executedGraphs.indexOf(executedGraph);
	}
	
	public ArrayList<ICoverageData> getCoverageStatus(int index) {
		return data.get(index);
	}
	
	public List<ArrayList<ICoverageData>> getCoverageData() {
		return data;
	}
	
	public ArrayList<String> getCoverageStatistics(int index, Object executedGraph) {
		ArrayList<Object> param = new ArrayList<Object>();
		param.add(executedGraph);
		param.add(data.get(index).get(0));
		param.add(coveredPaths);
		param.add(requirementSet);
		ArrayList<String> individual = new StatisticsFactory().getStatisticType(Statistics_ID.INDIVIDUAL_BASIC_ID, param).getStatistics();
		if(executedGraphs.size() == 1)
			return individual;
		else {
			param.clear();
			param.add(executedGraphs);
			param.add(getCoveredData());
			param.add(getcoveredPaths());
			param.add(requirementSet);
			param.add(individual);
			return new StatisticsFactory().getStatisticType(Statistics_ID.TOTAL_BASIC_ID, param).getStatistics();
		}
	}
	
	private ArrayList<ICoverageData> getCoveredData() {
		ArrayList<ICoverageData> aux = new ArrayList<ICoverageData>();
		for(ArrayList<ICoverageData> dataList : data)
			for(ICoverageData iData : dataList)
				aux.add(iData);
		return aux;	
	}
	
	private ArrayList<ArrayList<Path<Integer>>> getcoveredPaths() {
		ArrayList<ArrayList<Path<Integer>>> coveredPaths = new ArrayList<ArrayList<Path<Integer>>>();
		for(Object executedGraph : executedGraphs)
			coveredPaths.add(getCoveredPaths(executedGraph));
		return coveredPaths;
	}
}
