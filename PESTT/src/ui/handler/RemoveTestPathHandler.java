package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Messages;
import ui.dialog.RemoveDialog;
import adt.graph.Path;

public class RemoveTestPathHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		removeTableLine(window.getShell());
		return null;
	}
	
	private void removeTableLine(Shell shell) throws ExecutionException {
		if(Activator.getDefault().getTestPathController().isTestPathSelected()) {
			String pathMessage = "";
			for(Path<Integer> path : Activator.getDefault().getTestPathController().getSelectedTestPaths())
				pathMessage += path.toString() +"\n";
			String message = "Are you sure that you want to delete this graph:\n" + pathMessage;
			RemoveDialog dialog = new RemoveDialog(shell, message);
			dialog.open();
			String input = dialog.getInput();
			if(input != null) {
				Activator.getDefault().getTestPathController().removeCoverageData();
				Activator.getDefault().getTestPathController().removeTestPath();
				MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_INPUT_TITLE, Messages.TEST_PATH_SUCCESS_REMOVE_MSG); // message displayed when the graph is successfully remove.
			}
		} else
			MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_INPUT_TITLE, Messages.TEST_PATH_SELECT_TO_REMOVE_MSG); // message displayed when there is no graph selected to be removed.
	}		
}