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
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if(Activator.getDefault().getEditorController().isEverythingMatching())
				if(Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected()) {
					switch(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()) {
						case COMPLETE_PATH:
						case PRIME_PATH:
						case EDGE_PAIR:
						case COMPLETE_ROUND_TRIP:
						case EDGE:
						case SIMPLE_ROUND_TRIP: 
						case NODE:
							Activator.getDefault().getEditorController().setListenUpdates(false);
							Activator.getDefault().getTestRequirementController().generateTestRequirement();
							Activator.getDefault().getEditorController().setListenUpdates(true);
						default:
							System.out.println("Test requirements  handler -> Please change view");
					}
				} else 
					MessageDialog.openInformation(window.getShell(), Messages.COVERAGE_TITLE, Messages.COVERAGE_SELECT_MSG); // message displayed when the graph is not draw.
			else 
				MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);	
		else
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}
}
