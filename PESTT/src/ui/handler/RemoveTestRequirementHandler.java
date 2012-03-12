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
import domain.constants.JavadocTagAnnotations;

public class RemoveTestRequirementHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		removeTableLine(window.getShell());
		return null;
	}
	
	private void removeTableLine(Shell shell) throws ExecutionException {
		if(Activator.getDefault().getTestRequirementController().isTestRequirementSelected()) {	
			Path<Integer> selectedPath = Activator.getDefault().getTestRequirementController().getSelectedTestRequirement();
			String message = "Are you sure that you want to delete this test requirement:\n" + selectedPath;
			RemoveDialog dialog = new RemoveDialog(shell, message);
			dialog.open();
			String input = dialog.getInput();
			if(input != null) {
				Activator.getDefault().getEditorController().removeJavadocTagAnnotation(JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH, selectedPath.toString());
				if(Activator.getDefault().getTestRequirementController().isInfeasible(selectedPath))
					Activator.getDefault().getEditorController().removeJavadocTagAnnotation(JavadocTagAnnotations.INFEASIBLE_PATH, selectedPath.toString());
				Activator.getDefault().getTestRequirementController().removeSelectedTestRequirement();
				MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_SUCCESS_REMOVE_MSG); // message displayed when the graph is successfully remove.
			}
		} else
			MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_SELECT_TO_REMOVE_MSG); // message displayed when there is no test requirement selected to be removed.
	}		
}