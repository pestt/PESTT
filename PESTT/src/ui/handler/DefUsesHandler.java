package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import domain.constants.GraphCoverageCriteriaId;
import ui.constants.Messages;

public class DefUsesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		if (Activator.getDefault().getSourceGraphController().numberOfNodes() >= 1)
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching()) {
//				Activator.getDefault().getEditorController()
//						.setListenUpdates(false);
				if (GraphCoverageCriteriaId.isADefUsesCoverageCriteria(
						Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria())
					)
					Activator.getDefault().getDefUsesController()
							.generateDefUses();
				else
					MessageDialog.openInformation(window.getShell(),
							Messages.DEF_USES_TITLE,
							Messages.DEF_USES_CRITERIA_SELECT_MSG);
//				Activator.getDefault().getEditorController()
//						.setListenUpdates(true);
			} else
				MessageDialog.openInformation(window.getShell(),
						Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);
		else
			MessageDialog.openInformation(window.getShell(),
					Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG);
		return null;
	}
}