package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Messages;

public class TestRequirementsHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected())
			Activator.getDefault().getTestRequirementController().generateTestRequirement();
		else 
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.SELECT_COVERAGE); // message displayed when the graph is not draw.
		return null;
	}
}