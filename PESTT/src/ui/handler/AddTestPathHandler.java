package ui.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.dialog.InputDialog;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Description;
import domain.constants.Messages;
import domain.coverage.instrument.FakeCoverageData;
import domain.coverage.instrument.ICoverageData;

public class AddTestPathHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	private Graph<Integer> sourceGraph;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		addTableLine(window.getShell());
		return null;
	}
	
	private void addTableLine(Shell shell) throws ExecutionException {
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		String message = "Please enter a executed graph:\n(e.g. " + sourceGraph.getInitialNodes().iterator().next() + ", ..., " + sourceGraph.getFinalNodes().iterator().next() + ")";
		InputDialog dialog = new InputDialog(shell, message);
		dialog.open();
		String input = dialog.getInput();
		if(input != null)
			if(!input.equals(Description.EMPTY)) {
				Path<Integer> newTestPath = createTestPath(input);
				if(newTestPath != null) {
					Activator.getDefault().getTestPathController().addTestPath(newTestPath);
					List<ICoverageData> newData = new LinkedList<ICoverageData>();
					newData.add(new FakeCoverageData(newTestPath));
					Activator.getDefault().getCoverageDataController().addCoverageData(newTestPath, newData);			
				} else {
					MessageDialog.openInformation(window.getShell(), Messages.GRAPH_INPUT_TITLE, Messages.GRAPH_INVALID_INPUT_MSG); // message displayed when the inserted graph is not valid.
					addTableLine(shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages.GRAPH_INPUT_TITLE, Messages.GRAPH_INPUT_MSG); // message displayed when the inserted graph is empty.
				addTableLine(shell);
			}
	}
	
	private Path<Integer> createTestPath(String input) {
		boolean flag = true;
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