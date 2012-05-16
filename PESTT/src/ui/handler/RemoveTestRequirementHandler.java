package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Messages;
import ui.dialog.RemoveDialog;
import adt.graph.AbstractPath;

public class RemoveTestRequirementHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if(Activator.getDefault().getEditorController().isEverythingMatching())
				removeTestRequirement(window);
			else 
				MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);	
		else
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}
	
	private void removeTestRequirement(IWorkbenchWindow window) throws ExecutionException {
		if(Activator.getDefault().getTestRequirementController().isTestRequirementSelected()) {	
			AbstractPath<Integer> selectedPath = Activator.getDefault().getTestRequirementController().getSelectedTestRequirement();
			String message = Messages.TEST_REQUIREMENT_CONFIRM_REMOVE_MSG + selectedPath;
			RemoveDialog dialog = new RemoveDialog(window.getShell(), message);
			dialog.open();
			String input = dialog.getInput();
			if(input != null) {
				Activator.getDefault().getTestRequirementController().removeSelectedTestRequirement();
				MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_SUCCESS_REMOVE_MSG); // message displayed when the graph is successfully remove.
			}
		} else
			MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_SELECT_TO_REMOVE_MSG); // message displayed when there is no test requirement selected to be removed.
	}		
}