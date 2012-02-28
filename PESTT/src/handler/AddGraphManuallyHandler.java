package handler;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;
import view.GraphsCreator;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Messages_ID;
import constants.TableViewers_ID;
import coverage.FakeCoverageData;
import coverage.ICoverageData;
import dialog.InputDialog;

public class AddGraphManuallyHandler extends AbstractHandler {

	private ViewRequirementSet view;
	private IWorkbenchWindow window;
	private Graph<Integer> sourceGraph;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		view = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(view.getTableViewer(TableViewers_ID.EXECUTED_GRAPHS_VIEWER_ID) != null)
			addTableLine(view.getTableViewer(TableViewers_ID.EXECUTED_GRAPHS_VIEWER_ID), window.getShell());
		else {
			String criteria = TestRequirementsHandler.getSelectedCriteria();
			view.showCoverage(criteria);
			addTableLine(view.getTableViewer(TableViewers_ID.EXECUTED_GRAPHS_VIEWER_ID), window.getShell());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void addTableLine(TableViewer viewer, Shell shell) throws ExecutionException {
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		List<Object> executedGraphs = view.getExecutedGraphs();
		viewer.setInput(executedGraphs);
		String message = "Please enter a executed graph:\n(e.g. " + sourceGraph.getInitialNodes().iterator().next() + ", ..., " + sourceGraph.getFinalNodes().iterator().next() + ")";
		InputDialog dialog = new InputDialog(shell, message);
		dialog.open();
		String input = dialog.getInput();
		if(input != null)
			if(!input.equals(Description_ID.EMPTY)) {
				if(executedGraphs.contains(Description_ID.TOTAL))
					executedGraphs.remove(Description_ID.TOTAL);
				Path<Integer> fakeExecutedPath = createFakeExecutedPath(input);
				if(fakeExecutedPath != null) {
					executedGraphs.add(fakeExecutedPath);
					List<List<ICoverageData>> data = view.getCoverageData();
					List<ICoverageData> newData = new LinkedList<ICoverageData>();
					newData.add(new FakeCoverageData(fakeExecutedPath));
					data.add(newData);
					if(executedGraphs.size() > 1)
						executedGraphs.add(Description_ID.TOTAL);					
					view.cleanPathStatus();
					viewer.setInput(executedGraphs);
				} else {
					MessageDialog.openInformation(window.getShell(), Messages_ID.GRAPH_INPUT_TITLE, Messages_ID.GRAPH_INVALID_INPUT_MSG); // message displayed when the inserted graph is not valid.
					addTableLine(viewer, shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages_ID.GRAPH_INPUT_TITLE, Messages_ID.GRAPH_INPUT_MSG); // message displayed when the inserted graph is empty.
				addTableLine(viewer, shell);
			}
	}
	
	@SuppressWarnings("unchecked")
	private Path<Integer> createFakeExecutedPath(String input) {
		boolean flag = true;
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		List<String> insertedNodes = getInsertedNodes(input);
		if(!sourceGraph.isInitialNode(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(0)))) || !sourceGraph.isFinalNode(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(insertedNodes.size() - 1)))))
			return null;
		Path<Integer> fakeExecutedPath = null;
		for(int i = 0; i < insertedNodes.size(); i++) {
			try {
				Node<Integer> nodeFrom = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i)));
				if(nodeFrom != null && flag) 
					if(i + 1 < insertedNodes.size()) {
						Node<Integer> nodeTo = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i + 1)));
						for(Edge<Integer> edge : sourceGraph.getNodeEdges(nodeFrom))
							if(nodeTo == edge.getEndNode()) {
								if(fakeExecutedPath == null) 
									fakeExecutedPath = new Path<Integer>(nodeFrom);
								 else 
									fakeExecutedPath.addNode(nodeFrom);
								flag = true;
								break;
							} else
								flag = false;
					} else 
						if(fakeExecutedPath == null) 
							fakeExecutedPath = new Path<Integer>(nodeFrom);
						else
							fakeExecutedPath.addNode(nodeFrom);
				else
					return null;
			} catch(NumberFormatException ee) {
				return null;
			}
		}
		return fakeExecutedPath;
	}
	
	private List<String> getInsertedNodes(String input) {
		List<String> aux = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		// separate the inserted nodes.
		while(strtok.hasMoreTokens())
			aux.add(strtok.nextToken());
		return aux;
	}
		
}