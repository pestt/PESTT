package ui.handler;

import java.util.Set;

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

public class EditTestPath extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if(Activator.getDefault().getEditorController().isEverythingMatching())
				editTestPath(window, "");
			else 
				MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);	
		else
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}
	
	private void editTestPath(IWorkbenchWindow window, String input) throws ExecutionException {
		if(Activator.getDefault().getTestPathController().isTestPathSelected()) {
			if(input.equals(Description.EMPTY)) {
				Set<Path<Integer>> selected = Activator.getDefault().getTestPathController().getSelectedTestPaths();
				if(selected.size() != 1) {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_TITLE, Messages.TEST_PATH_WARNING_EDITED_MSG);
					return;
				}
				input = selected.iterator().next().toString();
				input = input.substring(1, input.length() - 1);
			}
			String message = Messages.SAVE_CHANGES;
			InputDialog dialog = new InputDialog(window.getShell(), message, input);
			dialog.open();
			input = dialog.getInput();
			if(input != null)
				if(!input.equals(Description.EMPTY)) {
					Path<Integer> newTestPath = Activator.getDefault().getTestPathController().createTestPath(input);
					if(newTestPath != null) {
						Activator.getDefault().getTestPathController().removeCoverageData();
						Activator.getDefault().getTestPathController().removeTestPath();
						Activator.getDefault().getTestPathController().addTestPath(newTestPath);
						MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_TITLE, Messages.TEST_PATH_SUCCESS_EDITED_MSG); // message displayed when the test path is successfully edited.
					} else {
						MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_TITLE, Messages.TEST_PATH_INVALID_INPUT_MSG); // message displayed when the inserted test path is not valid.
						editTestPath(window, input);
					}
				} else {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_TITLE, Messages.TEST_PATH_INPUT_MSG); // message displayed when the inserted test path is empty.
					editTestPath(window, input);
				}
		}
	}
}	