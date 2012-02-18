package sourcecodeexplorer;

import java.util.List;

import sourcegraph.Graph;

public interface IProjectExplorer {
	
	public Graph<Integer> getSourceCodeGraph(List<String> path);

}
