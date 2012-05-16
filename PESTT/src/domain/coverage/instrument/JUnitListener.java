package domain.coverage.instrument;

import java.util.List;

import main.activator.Activator;

import org.junit.runner.notification.RunListener;

import adt.graph.Path;

public class JUnitListener extends RunListener {
	
	public void testRunEnded(long arg0) {
	/*	try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Description.VIEW_STRUCTURAL_COVERAGE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	*/	System.out.println("end!!");
		
		getExecutedtestPaths();
		Activator.getDefault().getBytemanController().deleteScripts();
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
