package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.dialog.RemoveDialog;
import domain.constants.Messages;

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
			if(!(Activator.getDefault().getTestPathController().getSelectedTestPath() instanceof String)) {
				String message = "Are you sure that you want to delete this graph:\n" + 
						Activator.getDefault().getTestPathController().getSelectedTestPath();
				RemoveDialog dialog = new RemoveDialog(shell, message);
				dialog.open();
				String input = dialog.getInput();
				if(input != null) {
					Activator.getDefault().getCoverageDataController().removeSelectedCoverageData();
					Activator.getDefault().getTestPathController().removeSelectedTestPath();
					MessageDialog.openInformation(window.getShell(), Messages.GRAPH_INPUT_TITLE, Messages.GRAPH_REMOVE_MSG); // message displayed when the graph is successfully remove.
				}
			} else
				MessageDialog.openInformation(window.getShell(), Messages.GRAPH_INPUT_TITLE, Messages.GRAPH_SELECT_TOTTAL_TO_REMOVE_MSG); // message displayed when user is trying to remove the total
		} else
			MessageDialog.openInformation(window.getShell(), Messages.GRAPH_INPUT_TITLE, Messages.GRAPH_SELECT_TO_REMOVE_MSG); // message displayed when there is no graph selected to be removed.
	}		
}