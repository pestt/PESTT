package domain.coverage.instrument;

import java.util.List;

import main.activator.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import adt.graph.Path;

public class JUnitRun extends JUnitLaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		Activator.getDefault().getBytemanController().createScripts();
		super.launch(selection, mode);
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.FOLDER, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		getExecutedtestPaths();
		Activator.getDefault().getBytemanController().deleteScripts();
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		Activator.getDefault().getBytemanController().createScripts();
		super.launch(editor, mode);
		getExecutedtestPaths();
//		Activator.getDefault().getBytemanController().deleteScripts();
	}
	
	private void getExecutedtestPaths() {
		List<Path<Integer>> paths = Activator.getDefault().getBytemanController().getExecutedPaths();
		for(Path<Integer> newTestPath : paths)
			if(newTestPath != null) {
				Activator.getDefault().getTestPathController().addTestPath(newTestPath);
				System.out.println(newTestPath.toString());
			}
	}

}