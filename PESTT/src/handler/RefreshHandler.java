package handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.zest.core.widgets.GraphItem;

import view.GraphsCreator;
import view.ViewGraph;
import view.ViewGraphCoverageCriteria;
import view.ViewRequirementSet;
import activator.Activator;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Layer_ID;
import constants.Messages_ID;
import constants.Preferences;
import coveragealgorithms.GraphCoverageCriteria;
import editor.ActiveEditor;

public class RefreshHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ActiveEditor editor = new ActiveEditor();
		editor.getSelectedText();
		List<String> location = editor.getLocation(); // get the location of the file.
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(editor.isInMethod()) { // if the text selected is the name of the method.
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String dot = preferenceStore.getString(Preferences.DOT_PATH);
			if(dot != null && !dot.equals(Description_ID.EMPTY)) {
				ViewGraphCoverageCriteria viewGraphCoverageCriteria = (ViewGraphCoverageCriteria) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_COVERAGE_CRITERIA); // get the view graph.
				ViewGraph viewGraph = (ViewGraph) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_GRAPH); // get the view graph.
				ViewRequirementSet viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET); // get the view requirement set.
				if(viewGraphCoverageCriteria == null || viewGraph == null || viewRequirementSet == null) {
					try {
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description_ID.VIEW_GRAPH);
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description_ID.VIEW_COVERAGE_CRITERIA);
						HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Description_ID.VIEW_REQUIREMENT_SET);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
					viewGraphCoverageCriteria = (ViewGraphCoverageCriteria) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_COVERAGE_CRITERIA); // get the view graph.
					viewGraph = (ViewGraph) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_GRAPH); // get the view graph.
					viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET); // get the view requirement set.
				}
				viewGraphCoverageCriteria.create();
				viewGraph.create(location); // update view content.
				viewRequirementSet.disposeControl(1);
				viewRequirementSet.setEditor(editor);
				keepCommandOptions();
			} else
				MessageDialog.openInformation(window.getShell(), Messages_ID.PREFERENCES_TITLE, Messages_ID.PREFERENCES); 
		} else 
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		return null;
	}
	
	private void keepCommandOptions() {
		GraphCoverageCriteria coverageGraph = (GraphCoverageCriteria) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.COVERAGE_GRAPH_NUM);
		String option = Description_ID.EMPTY;
		option = TestRequirementsHandler.getSelectedCriteria();
		if(!option.equals(Description_ID.EMPTY)) {
			GraphItem[] items = new GraphItem[1];
			for(GraphItem item : coverageGraph.getCriteriaGraphNodes()) 
				if(option.equals(item.getData())) {
					items[0] = item;
					coverageGraph.setSelected(items);
					break;
				}
		}
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class); // get the IHandlerService.
		ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class); // get the ICommandService.
		State stateLink = cmdService.getCommand(Description_ID.LINK_BUTTON).getState("org.eclipse.ui.commands.toggleState"); // the current state of the link command.
		boolean valueLink = (Boolean) stateLink.getValue(); // the value of the state.
		State stateLayer = cmdService.getCommand(Description_ID.LAYER_BUTTON).getState("org.eclipse.ui.commands.radioState"); // the current state of the layer command.
		String valueLayer = stateLayer.getValue().toString(); // the value of the layer.
		try {
			if(valueLink) {
				stateLink.setValue(false); // disable selection
				handlerService.executeCommand(Description_ID.LINK_BUTTON, null); // update the value.
			}
			if(!valueLayer.equals(Integer.toString(Layer_ID.EMPTY)) && !valueLayer.equals(Description_ID.NONE)) {
				handlerService.executeCommand(cmdService.getCommand(Description_ID.LAYER_BUTTON).getId(), null); // update the value.//
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}