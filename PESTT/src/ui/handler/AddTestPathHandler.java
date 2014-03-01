package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Description;
import ui.constants.Messages;
import ui.dialog.InputDialog;
import adt.graph.Graph;
import adt.graph.Path;
import domain.constants.TestType;

public class AddTestPathHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		if (Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching())
				addNewTestPath(window, "");
			else
				MessageDialog.openInformation(window.getShell(),
						Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);
		else
			MessageDialog.openInformation(window.getShell(),
					Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}

	private void addNewTestPath(IWorkbenchWindow window, String input)
			throws ExecutionException {
		Graph sourceGraph = Activator.getDefault()
				.getSourceGraphController().getSourceGraph();
		String message = "Please enter a test path:\n(e.g. "
				+ sourceGraph.getInitialNodes().iterator().next() + ", ..., "
				+ sourceGraph.getFinalNodes().iterator().next() + ")";
		InputDialog dialog = new InputDialog(window.getShell(), message, input);
		dialog.open();
		input = dialog.getInput();
		if (input != null)
			if (!input.equals(Description.EMPTY)) {
				Path newTestPath = Activator.getDefault()
						.getTestPathController().createTestPath(input);
				if (newTestPath != null)
					Activator.getDefault().getTestPathController()
							.addTestPath(newTestPath, TestType.MANUALLY);
				else {
					MessageDialog.openInformation(window.getShell(),
							Messages.TEST_PATH_TITLE,
							Messages.TEST_PATH_INVALID_INPUT_MSG); // message displayed when the inserted test path is not valid.
					addNewTestPath(window, input);
				}
			} else {
				MessageDialog.openInformation(window.getShell(),
						Messages.TEST_PATH_TITLE, Messages.TEST_PATH_INPUT_MSG); // message displayed when the inserted test path is empty.
				addNewTestPath(window, input);
			}
	}
}
