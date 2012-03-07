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

public class AddTestRequirementHandler extends AbstractHandler {

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
		String message = "Please enter a requirement test:\n(e.g. 0, ..., 3)";
		InputDialog dialog = new InputDialog(shell, message);
		dialog.open();
		String input = dialog.getInput();
		if(input != null)
			if(!input.equals(Description.EMPTY)) {
				Path<Integer> newTestRequirement = createTestRequirement(input);
				if(newTestRequirement != null) 
					Activator.getDefault().getTestRequirementController().addTestRequirement(newTestRequirement);
				else {
					MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_INVALID_INPUT_MSG); // message displayed when the inserted test requirement is not valid.
					addTableLine(shell);
				}
			} else {
				MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_INPUT_MSG); // message displayed when the inserted test requirement is empty.
				addTableLine(shell);
			}
	}

	private Path<Integer> createTestRequirement(String input) {
		boolean flag = true;
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		List<String> insertedNodes = getInsertedNodes(input);
		Path<Integer> newTestRequirement = null;
		for(int i = 0; i < insertedNodes.size(); i++) {
			try {
				Node<Integer> nodeFrom = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i)));
				if(nodeFrom != null && flag)
					if(i + 1 < insertedNodes.size()) {
						Node<Integer> nodeTo = sourceGraph.getNode(Integer.parseInt(insertedNodes.get(i + 1)));
						for(Edge<Integer> edge : sourceGraph.getNodeEdges(nodeFrom))
							if(nodeTo == edge.getEndNode()) {
								if(newTestRequirement == null) 
									newTestRequirement = new Path<Integer>(nodeFrom);
								 else 
									newTestRequirement.addNode(nodeFrom);
								flag = true;
								break;
							} else
								flag = false;
					} else
						if(newTestRequirement == null) 
							newTestRequirement = new Path<Integer>(nodeFrom);
						else
							newTestRequirement.addNode(nodeFrom);
				else
					return null;
			} catch(NumberFormatException ee) {
				return null;
			}
		}
		return newTestRequirement;
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