package ui.handler;

import java.util.List;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import adt.graph.Path;

public class RunHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Path<Integer>> paths = Activator.getDefault().getBytemanController().getExecutedPaths();
		for(Path<Integer> newTestPath : paths)
			if(newTestPath != null)
				Activator.getDefault().getTestPathController().addTestPath(newTestPath);
		return null;
	}

}
