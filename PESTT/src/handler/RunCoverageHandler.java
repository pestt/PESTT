package handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import view.GraphsCreator;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Messages_ID;

public class RunCoverageHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(GraphsCreator.INSTANCE.isDisplayed()) { // the graph is displayed.
			ViewRequirementSet viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
			viewRequirementSet.showCoverageResults();
		} else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.NEED_TO_DRAW); // message displayed when the graph is not draw.
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.DRAW_GRAPH_MSG); // message displayed when the graph is not draw.
			MessageDialog.openInformation(window.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.SELECT_COVERAGE); // message displayed when the test requirements are not designed.
		}
		return null;
	}
}
