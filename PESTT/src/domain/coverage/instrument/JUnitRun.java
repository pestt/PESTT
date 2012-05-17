package domain.coverage.instrument;

import main.activator.Activator;

import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class JUnitRun extends JUnitLaunchShortcut {
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		Activator.getDefault().getBytemanController().createScripts();
		Activator.getDefault().getBytemanController().addListener();
		super.launch(editor, mode);		
		IEditorPart part = Activator.getDefault().getEditorController().getEditorPart();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(part);
	}
}