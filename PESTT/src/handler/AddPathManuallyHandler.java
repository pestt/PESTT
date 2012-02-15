package handler;

import java.util.ArrayList;
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
import dialog.InputDialog;

public class AddPathManuallyHandler extends AbstractHandler {

	private ViewRequirementSet view;
	private IWorkbenchWindow window;
	private Graph<Integer> sourceGraph;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		view = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		addTableLine(view.getTableViewer(TableViewers_ID.TEST_REQUIREMENT_SET_VIEWER_ID), window.getShell());
		return null;
	}
	
	private void addTableLine(TableViewer viewer, Shell shell) throws ExecutionException {
		String message = "Please enter a requirement test:\n(e.g. 0, ..., 3)";
		InputDialog dialog = new InputDialog(shell, message);
		dialog.open();
		String input = dialog.getInput();
		if(input != null)
			if(!input.equals(Description_ID.EMPTY)) {
				Path<Integer> fakeTestRequirement = createFakeTestRequirement(input);
				if(fakeTestRequirement != null) {
					ArrayList<Path<Integer>> testRequirements = view.getTestRequirement();
					testRequirements.add(fakeTestRequirement);	
					view.cleanPathStatus();
					viewer.setInput(testRequirements);
				} else {
					MessageDialog.openInformation(window.getShell(), Messages_ID.TEST_REQUIREMENT_INPUT_TITLE, Messages_ID.TEST_REQUIREMENT_INVALID_INPUT_MSG); // message displayed when the inserted test requirement is not valid.
					addTableLine(viewer, shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages_ID.TEST_REQUIREMENT_INPUT_TITLE, Messages_ID.TEST_REQUIREMENT_INPUT_MSG); // message displayed when the inserted test requirement is empty.
				addTableLine(viewer, shell);
			}
	}
	
	@SuppressWarnings("unchecked")
	private Path<Integer> createFakeTestRequirement(String input) {
		boolean flag = true;
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		ArrayList<String> insertedNodes = getInsertedNodes(input);
		Path<Integer> fakeRequirementTest = null;
		for(int i = 0; i < insertedNodes.size(); i++) {
			try {
				Node<Integer> nodeFrom = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i)));
				if(nodeFrom != null && flag)
					if(i + 1 < insertedNodes.size()) {
						Node<Integer> nodeTo = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i + 1)));
						for(Edge<Integer> edge : sourceGraph.getNodeEdges(nodeFrom))
							if(nodeTo == edge.getEndNode()) {
								if(fakeRequirementTest == null) 
									fakeRequirementTest = new Path<Integer>(nodeFrom);
								 else 
									fakeRequirementTest.addNode(nodeFrom);
								flag = true;
								break;
							} else
								flag = false;
					} else
						fakeRequirementTest.addNode(nodeFrom);
				else
					return null;
			} catch(NumberFormatException ee) {
				return null;
			}
		}
		return fakeRequirementTest;
	}
	
	private ArrayList<String> getInsertedNodes(String input) {
		ArrayList<String> aux = new ArrayList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		// separate the inserted nodes.
		while(strtok.hasMoreTokens())
			aux.add(strtok.nextToken());
		return aux;
	}
}