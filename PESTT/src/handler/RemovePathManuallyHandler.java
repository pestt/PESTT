package handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import sourcegraph.Path;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Messages_ID;
import constants.TableViewers_ID;
import dialog.RemoveDialog;

public class RemovePathManuallyHandler extends AbstractHandler {

	private ViewRequirementSet view;
	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		view = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		removeTableLine(view.getTableViewer(TableViewers_ID.TEST_REQUIREMENT_SET_VIEWER_ID), window.getShell());
		return null;
	}
	
	private void removeTableLine(TableViewer viewer, Shell shell) throws ExecutionException {
		Path<Integer> selectedTestRequirement = view.getSelectedTestRequirement();
		if(selectedTestRequirement != null) {	
			String message = "Are you sure that you want to delete this test requirement:\n" + selectedTestRequirement;
			RemoveDialog dialog = new RemoveDialog(shell, message);
			dialog.open();
			String input = dialog.getInput();
			if(input != null) {
				List<Path<Integer>> testRequirements = view.getTestRequirement();
				testRequirements.remove(selectedTestRequirement);
				view.cleanPathStatus();
				viewer.setInput(testRequirements);
				MessageDialog.openInformation(window.getShell(), Messages_ID.TEST_REQUIREMENT_INPUT_TITLE, Messages_ID.TEST_REQUIREMENT_REMOVE_MSG); // message displayed when the graph is successfully remove.
			}
		} else
			MessageDialog.openInformation(window.getShell(), Messages_ID.TEST_REQUIREMENT_INPUT_TITLE, Messages_ID.TEST_REQUIREMENT_SELECT_TO_REMOVE_MSG); // message displayed when there is no test requirement selected to be removed.
	}		
}