package sourcecodeexplorer;

import org.eclipse.jdt.core.ICompilationUnit;

import sourcegraph.Graph;

public interface IProjectExplorer {
	
	public Graph<Integer> getSourceCodeGraph(ICompilationUnit unit, String methodName);

}
