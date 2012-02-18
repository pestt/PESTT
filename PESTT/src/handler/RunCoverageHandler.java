package handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.zest.core.widgets.GraphItem;

import view.GraphsCreator;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Messages_ID;
import coveragealgorithms.GraphCoverageCriteria;

public class RunCoverageHandler extends AbstractHandler {

	private String option = "";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(GraphsCreator.INSTANCE.isDisplayed()) { // the graph is displayed.
			GraphCoverageCriteria coverageGraph = (GraphCoverageCriteria) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.COVERAGE_GRAPH_NUM);
			GraphItem item = coverageGraph.getSelected();
			if(item != null) {
				option = (String) item.getData();
				ViewRequirementSet viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
				viewRequirementSet.showTestRequirements(option);
			} else 
				MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.SELECT_COVERAGE); // message displayed when the graph is not draw.
		} else {
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.NEED_TO_DRAW); // message displayed when the graph is not draw.
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.DRAW_GRAPH_MSG); // message displayed when the graph is not draw.
		}
		return null;
	}
}
