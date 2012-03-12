package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import domain.constants.JavadocTagAnnotations;

import ui.constants.Description;
import ui.constants.Messages;
import ui.dialog.InputDialog;
import adt.graph.Path;

public class AddTestRequirementHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		addNewTestReuirementPath(window.getShell());
		return null;
	}
	
	private void addNewTestReuirementPath(Shell shell) throws ExecutionException {
		String message = "Please enter a test requirement:\n(e.g. 0, ..., 3)";
		InputDialog dialog = new InputDialog(shell, message);
		dialog.open();
		String input = dialog.getInput();
		if(input != null)
			if(!input.equals(Description.EMPTY)) {
				Path<Integer> newTestRequirement = Activator.getDefault().getTestRequirementController().createTestRequirement(input);
				if(newTestRequirement != null) {
					Activator.getDefault().getEditorController().addJavadocTagAnnotation(JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH, newTestRequirement.toString());
					Activator.getDefault().getTestRequirementController().addTestRequirement(newTestRequirement);
				} else {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_INVALID_INPUT_MSG); // message displayed when the inserted test requirement is not valid.
					addNewTestReuirementPath(shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_INPUT_MSG); // message displayed when the inserted test requirement is empty.
				addNewTestReuirementPath(shell);
			}
	}
}