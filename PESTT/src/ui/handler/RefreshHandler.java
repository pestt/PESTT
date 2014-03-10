package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import ui.constants.Description;
import ui.constants.Messages;
import ui.controllers.EditorController;
import ui.editor.ActiveEditor;
import ui.preferences.ConstantLabels;
import domain.constants.Layer;
import domain.controllers.TestSuiteController;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EditorController ec = Activator.getDefault().getEditorController();
		if (ec.getActiveEditor() != null) {
			ec.getActiveEditor().deleteObservers();
			Activator.getDefault().getViewController().deleteObserverToViews();
		}
		ec.setEditor(new ActiveEditor());
		ec.getActiveEditor().addObservers();
		Activator.getDefault().getViewController().addObserverToViews();
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		if (ec.isInMethod()) { // if the text selected is the name of the method.
			TestSuiteController tsc = Activator.getDefault().getTestSuiteController();
			IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			if (!tsc.hasTestSuite(ec.getProjectName())) 
				tsc.addTestSuite(ec.getProjectName(), 
						prefs.getString("defaultFilename"), 
						prefs.getString("structuralCoverageCriterium"),
						prefs.getString("tourType"));
			tsc.setMethodUnderTest(ec.getProjectName(), 
					ec.getPackageName(), ec.getClassName(), ec.getSelectedMethod());
			IPreferenceStore preferenceStore = Activator.getDefault()
					.getPreferenceStore();
			String dot = preferenceStore.getString(ConstantLabels.DOT_PATH);
			if (dot != null && !dot.equals(Description.EMPTY)) {
				Activator.getDefault().getViewController().loadNecessaryViews(event);
				resetDataStructures(window);
				keepCommandOptions();
			} else
				MessageDialog.openInformation(window.getShell(),
						Messages.PREFERENCES_TITLE,
						Messages.PREFERENCES_DOT_MSG);
		} else
			MessageDialog.openInformation(window.getShell(),
					Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG); // message displayed when the graph is not draw.
		return null;
	}

	private void resetDataStructures(IWorkbenchWindow window) {
		Activator.getDefault().getCoverageDataController()
				.deleteObserverToCoverageData();
		Activator.getDefault().getDefUsesController().deleteObserverToDefUses();
		Activator.getDefault().getCoverageDataController()
				.clearCoverageDataSet();
		Activator.getDefault().getStatisticsController().cleanStatisticsSet();
		Activator.getDefault().getDefUsesController().clearDefUsesSet();

		String selectedMethod = Activator.getDefault().getEditorController()
				.getSelectedMethod();
		ICompilationUnit unit = Activator.getDefault().getEditorController()
				.getCompilationUnit();
		Activator.getDefault().getSourceGraphController()
				.create(unit, selectedMethod);
		Activator.getDefault().getCFGController().refreshGraph();
		
		Activator.getDefault().getCoverageDataController()
				.addObserverToCoverageData();
		Activator.getDefault().getDefUsesController().addObserverToDefUses();
	}

	private void keepCommandOptions() {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class); // get the IHandlerService.
		ICommandService cmdService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class); // get the ICommandService.
		State stateLink = cmdService.getCommand(Description.LINK_BUTTON)
				.getState("org.eclipse.ui.commands.toggleState"); // the current state of the link command.
		boolean valueLink = (Boolean) stateLink.getValue(); // the value of the state.
		State stateLayer = cmdService.getCommand(Description.LAYER_BUTTON)
				.getState("org.eclipse.ui.commands.radioState"); // the current state of the layer command.
		String valueLayer = stateLayer.getValue().toString(); // the value of the layer.
		try {
			if (valueLink) {
				Activator.getDefault().getCFGController().setLinkState(false);
				stateLink.setValue(false); // disable selection
				handlerService.executeCommand(Description.LINK_BUTTON, null); // update the value.
			}
			if (!valueLayer.equals(Integer.toString(Layer.EMPTY.getLayer()))
					&& !valueLayer.equals(Description.NONE))
				handlerService.executeCommand(Description.LAYER_BUTTON, null); // update the value.

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
