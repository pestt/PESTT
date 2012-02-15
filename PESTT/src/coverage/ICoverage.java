package coverage;

import java.util.ArrayList;
import java.util.List;

import sourcegraph.Path;

public interface ICoverage {
	
	public ArrayList<Object> getExecutedGraphs();

	public ArrayList<Path<Integer>> getCoveredPaths(Object executedGraph);
	
	public int getStatusOfRun(Object executedGraph);
	
	public ArrayList<ICoverageData> getCoverageStatus(int index);
	
	public List<ArrayList<ICoverageData>> getCoverageData();
	
	public ArrayList<String> getCoverageStatistics(int index, Object executedGraph);
}
