package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Description;
import ui.constants.Messages;
import ui.dialog.InputDialog;
import adt.graph.Path;

public class EditTestRequirement extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if(Activator.getDefault().getEditorController().isEverythingMatching())
				editTestRequirement(window, "");
			else 
				MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);	
		else
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}
	
	private void editTestRequirement(IWorkbenchWindow window, String input) throws ExecutionException {
		if(Activator.getDefault().getTestRequirementController().isTestRequirementSelected())  {
			if(input.equals(Description.EMPTY)) {
				input = Activator.getDefault().getTestRequirementController().getSelectedTestRequirement().toString();
				input = input.substring(1, input.length() - 1);
			}
			String message = Messages.SAVE_CHANGES;
			InputDialog dialog = new InputDialog(window.getShell(), message, input);
			dialog.open();
			input = dialog.getInput();
			if(input != null)
				if(!input.equals(Description.EMPTY)) {
					Path<Integer> newTestRequirement = Activator.getDefault().getTestRequirementController().createTestRequirement(input);
					if(newTestRequirement != null) {
						Activator.getDefault().getTestRequirementController().removeSelectedTestRequirement();
						Activator.getDefault().getTestRequirementController().addTestRequirement(newTestRequirement);
					} else {
						MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_INVALID_INPUT_MSG); // message displayed when the inserted test requirement is not valid.
						editTestRequirement(window, input);
					}
				} else {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_INPUT_MSG); // message displayed when the inserted test requirement is empty.
					editTestRequirement(window, input);
				}
		}
	}
}
