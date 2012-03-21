package ui.handler;

import java.util.ArrayList;
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

import ui.constants.Description;
import ui.constants.Messages;
import ui.dialog.InputDialog;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.coverage.instrument.CoverageData;
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
					newData.add(new CoverageData(newTestPath));
					Activator.getDefault().getCoverageDataController().addCoverageData(newTestPath, newData);			
				} else {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_INPUT_TITLE, Messages.TEST_PATH_INVALID_INPUT_MSG); // message displayed when the inserted graph is not valid.
					addTableLine(shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_INPUT_TITLE, Messages.TEST_PATH_INPUT_MSG); // message displayed when the inserted graph is empty.
				addTableLine(shell);
			}
	}
	
	
	private Path<Integer> createTestPath(String input) {
		boolean validPath = true;
		List<String> insertedNodes = getInsertedNodes(input);
		List<Node<Integer>> pathNodes = new LinkedList<Node<Integer>> ();
		try {
			List<Node<Integer>> fromToNodes = new ArrayList<Node<Integer>>(2);
			fromToNodes.add(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(0))));
			int i = 1; 
			while (i < insertedNodes.size() && validPath) {
				fromToNodes.add(sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i))));
				if (fromToNodes.get(0) != null && fromToNodes.get(1) != null && 
						sourceGraph.isPath(new Path<Integer>(fromToNodes))) {
					pathNodes.add(fromToNodes.get(0));
					fromToNodes.remove(0);
				} else
					validPath = false;
				i++;
			}
			if (validPath) {
				pathNodes.add(fromToNodes.get(0));

				if(!sourceGraph.isInitialNode(pathNodes.get(0)) || !sourceGraph.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
					return null;

				return new Path<Integer>(pathNodes);
			}
		} catch(NumberFormatException ee) {
		}
		return null;
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