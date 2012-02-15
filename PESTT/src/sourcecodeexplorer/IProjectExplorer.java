package sourcecodeexplorer;

import java.util.ArrayList;
import sourcegraph.Graph;

public interface IProjectExplorer {
	
	public Graph<Integer> getSourceCodeGraph(ArrayList<String> path);

}
