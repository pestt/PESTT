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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import ui.constants.Description;
import ui.constants.Messages;
import ui.constants.Preferences;
import ui.display.views.ViewGraph;
import ui.display.views.ViewGraphCoverageCriteria;
import ui.display.views.ViewStructuralCriteria;
import ui.editor.ActiveEditor;
import domain.constants.Layer;

public class RefreshHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Activator.getDefault().getEditorController().setEditor(new ActiveEditor());
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(Activator.getDefault().getEditorController().isInMethod()) { // if the text selected is the name of the method.
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String dot = preferenceStore.getString(Preferences.DOT_PATH);
			if(dot != null && !dot.equals(Description.EMPTY)) {
				ViewGraphCoverageCriteria viewGraphCoverageCriteria = (ViewGraphCoverageCriteria) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description.VIEW_GRAPH_COVERAGE_CRITERIA); // get the view graph.
				ViewGraph viewGraph = (ViewGraph) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description.VIEW_GRAPH); // get the view graph.
				ViewStructuralCriteria viewStructuralCriteria = (ViewStructuralCriteria) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description.VIEW_STRUCTURAL_COVERAGE); // get the view structural criteria.
				if(viewGraphCoverageCriteria == null || viewGraph == null || viewStructuralCriteria == null) {
					try {
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description.VIEW_GRAPH);
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description.VIEW_GRAPH_COVERAGE_CRITERIA);
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description.VIEW_STRUCTURAL_COVERAGE);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
					viewGraphCoverageCriteria = (ViewGraphCoverageCriteria) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description.VIEW_GRAPH_COVERAGE_CRITERIA); // get the view graph.
					viewGraph = (ViewGraph) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description.VIEW_GRAPH); // get the view graph.
				}
				resetDataStructures();
				keepCommandOptions();
			} else
				MessageDialog.openInformation(window.getShell(), Messages.PREFERENCES_TITLE, Messages.PREFERENCES); 
		} else 
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		return null;
	}
	
	private void resetDataStructures() {
		String selectedMethod = Activator.getDefault().getEditorController().getSelectedMethod();
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		Activator.getDefault().getSourceGraphController().create(unit, selectedMethod);
		Activator.getDefault().getTestPathController().clear();
		Activator.getDefault().getCoverageDataController().cleanCoverageData();
		Activator.getDefault().getTestRequirementController().cleanTestRequirementSet();
		Activator.getDefault().getStatisticsController().cleanStatistics();
		
	}

	private void keepCommandOptions() {
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class); // get the IHandlerService.
		ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class); // get the ICommandService.
		State stateLink = cmdService.getCommand(Description.LINK_BUTTON).getState("org.eclipse.ui.commands.toggleState"); // the current state of the link command.
		boolean valueLink = (Boolean) stateLink.getValue(); // the value of the state.
		State stateLayer = cmdService.getCommand(Description.LAYER_BUTTON).getState("org.eclipse.ui.commands.radioState"); // the current state of the layer command.
		String valueLayer = stateLayer.getValue().toString(); // the value of the layer.
		try {
			if(valueLink) {
				stateLink.setValue(false); // disable selection
				handlerService.executeCommand(Description.LINK_BUTTON, null); // update the value.
			}
			if(!valueLayer.equals(Integer.toString(Layer.EMPTY.getLayer())) && !valueLayer.equals(Description.NONE)) {
				handlerService.executeCommand(cmdService.getCommand(Description.LAYER_BUTTON).getId(), null); // update the value.//
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}