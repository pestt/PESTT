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

import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Messages_ID;
import constants.TableViewers_ID;
import coverage.ICoverageData;
import dialog.RemoveDialog;

public class RemoveGraphManuallyHandler extends AbstractHandler {

	private ViewRequirementSet view;
	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		view = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		removeTableLine(view.getTableViewer(TableViewers_ID.EXECUTED_GRAPHS_VIEWER_ID), window.getShell());
		return null;
	}
	
	private void removeTableLine(TableViewer viewer, Shell shell) throws ExecutionException {
		Object selectedGraph = view.getSelectedGraph();
		if(selectedGraph != null) {
			if(!(selectedGraph instanceof String)) {
				String message = "Are you sure that you want to delete this graph:\n" + selectedGraph;
				RemoveDialog dialog = new RemoveDialog(shell, message);
				dialog.open();
				String input = dialog.getInput();
				if(input != null) {
					List<Object> executedGraphs = view.getExecutedGraphs();
					int index = executedGraphs.indexOf(selectedGraph);
					executedGraphs.remove(selectedGraph);
					List<List<ICoverageData>> data = view.getCoverageData();
					data.remove(index);
					if(executedGraphs.contains(Description_ID.TOTAL) && executedGraphs.size() <= 2)
						executedGraphs.remove(Description_ID.TOTAL);
					view.cleanPathStatus();
					viewer.setInput(executedGraphs);
					MessageDialog.openInformation(window.getShell(), Messages_ID.GRAPH_INPUT_TITLE, Messages_ID.GRAPH_REMOVE_MSG); // message displayed when the graph is successfully remove.
				}
			} else
				MessageDialog.openInformation(window.getShell(), Messages_ID.GRAPH_INPUT_TITLE, Messages_ID.GRAPH_SELECT_TOTTAL_TO_REMOVE_MSG); // message displayed when user is trying to remove the total.
		} else
			MessageDialog.openInformation(window.getShell(), Messages_ID.GRAPH_INPUT_TITLE, Messages_ID.GRAPH_SELECT_TO_REMOVE_MSG); // message displayed when there is no graph selected to be removed.
	}		
}