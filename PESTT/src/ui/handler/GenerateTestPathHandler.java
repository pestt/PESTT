package ui.handler;

import java.util.Set;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import adt.graph.Graph;
import adt.graph.Path;
import domain.constants.TestType;
import domain.tests.generation.GenerateTestPaths;

public class GenerateTestPathHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Graph<Integer> graph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		Set<Path<Integer>> paths = new GenerateTestPaths<Integer>(graph).getTestPaths();
		if(paths != null && !paths.isEmpty()) {
			for(Path<Integer> newTestPath : paths)
				Activator.getDefault().getTestPathController().addTestPath(newTestPath, TestType.AUTOMATIC);
		}
		return null;
	}	
}
