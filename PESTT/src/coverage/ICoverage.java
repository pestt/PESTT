package coverage;

import java.util.List;

import sourcegraph.Path;

public interface ICoverage {
	
	public List<Object> getExecutedGraphs();

	public List<Path<Integer>> getCoveredPaths(Object executedGraph, List<Path<Integer>> testRequirements);
	
	public int getStatusOfRun(Object executedGraph);
	
	public List<ICoverageData> getCoverageStatus(int index);
	
	public List<List<ICoverageData>> getCoverageData();
	
	public List<String> getCoverageStatistics(int index, Object executedGraph, List<Path<Integer>> testRequirements);
}
