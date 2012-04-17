package ui.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import ui.constants.JavadocTagAnnotations;
import ui.constants.Messages;
import ui.constants.Preferences;
import ui.display.views.ViewGraph;
import ui.display.views.ViewGraphCoverageCriteria;
import ui.display.views.ViewStructuralCriteria;
import ui.editor.ActiveEditor;
import adt.graph.Path;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.Layer;
import domain.coverage.data.CoverageData;
import domain.coverage.data.ICoverageData;

public class RefreshHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(Activator.getDefault().getEditorController().getActiveEditor() != null)
			Activator.getDefault().getEditorController().getActiveEditor().deleteObservers();
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
				resetDataStructures(window);
				keepCommandOptions();
			} else
				MessageDialog.openInformation(window.getShell(), Messages.PREFERENCES_TITLE, Messages.PREFERENCES_MSG); 
		} else 
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		return null;
	}
	
	private void resetDataStructures(IWorkbenchWindow window) {
		Activator.getDefault().getEditorController().setListenUpdates(false);
		Activator.getDefault().getCoverageDataController().deleteObserverToCoverageData();
		Activator.getDefault().getTestPathController().cleanTestPathManuallyAdded();
		Activator.getDefault().getTestPathController().cleanTestPathSet();
		Activator.getDefault().getTestRequirementController().clearInfeasibles();
		Activator.getDefault().getTestRequirementController().clearTestRequirementsManuallyAdded();
		Activator.getDefault().getTestRequirementController().cleanTestRequirementSet();
		Activator.getDefault().getCoverageDataController().clearCoverageDataSet();
		Activator.getDefault().getStatisticsController().cleanStatisticsSet();
		Activator.getDefault().getDefUsesController().clearDefUsesSet();
		String selectedMethod = Activator.getDefault().getEditorController().getSelectedMethod();
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		Activator.getDefault().getSourceGraphController().create(unit, selectedMethod);
		Activator.getDefault().getCoverageDataController().addObserverToCoverageData();
		Map<JavadocTagAnnotations, List<String>> javadocAnnotations = Activator.getDefault().getSourceGraphController().getJavadocAnnotations();
		List<String> criteria = javadocAnnotations.get(JavadocTagAnnotations.COVERAGE_CRITERIA);
		if(criteria != null && !criteria.isEmpty()) {
			Activator.getDefault().getTestRequirementController().selectCoverageCriteria(GraphCoverageCriteriaId.valueOf(criteria.get(0)));
			List<String> tour = javadocAnnotations.get(JavadocTagAnnotations.TOUR_TYPE);
			if(!tour.isEmpty())
				Activator.getDefault().getTestPathController().selectTourType(tour.get(0));
			for(String input : javadocAnnotations.get(JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH)) {
				String msg = input;
				input = input.substring(1, input.length() - 1);
				Path<Integer> newTestRequirement = Activator.getDefault().getTestRequirementController().createTestRequirement(input);
				if(newTestRequirement != null) 
					Activator.getDefault().getTestRequirementController().addTestRequirement(newTestRequirement);
				else
					MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG + "\n" + msg + "\n" + Messages.TEST_REQUIREMENT_REMOVE_MSG); // message displayed when the inserted test requirement is not valid.
			}
			for(String input : javadocAnnotations.get(JavadocTagAnnotations.INFEASIBLE_PATH)) {
				String msg = input;
				input = input.substring(1, input.length() - 1);
				Path<Integer> newTestRequirement = Activator.getDefault().getTestRequirementController().createTestRequirement(input);
				if(newTestRequirement != null) 
					Activator.getDefault().getTestRequirementController().enableInfeasible(newTestRequirement);
				else
					MessageDialog.openInformation(window.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG + "\n" + msg + "\n" + Messages.TEST_REQUIREMENT_REMOVE_MSG); // message displayed when the inserted test requirement is not valid.
			}
			for(String input : javadocAnnotations.get(JavadocTagAnnotations.ADDITIONAL_TEST_PATH)) {
				String msg = input;
				input = input.substring(1, input.length() - 1);
				Path<Integer> newTestPath = Activator.getDefault().getTestRequirementController().createTestRequirement(input);
				if(newTestPath != null) {
					Activator.getDefault().getTestPathController().addTestPath(newTestPath);
					List<ICoverageData> newData = new LinkedList<ICoverageData>();
					newData.add(new CoverageData(newTestPath));
					Activator.getDefault().getCoverageDataController().addCoverageData(newTestPath, newData);
				} else
					MessageDialog.openInformation(window.getShell(), Messages.TEST_PATH_TITLE, Messages.TEST_PATH_BECAME_INVALID_INPUT_MSG + "\n" + msg + "\n" + Messages.TEST_PATH_REMOVE_MSG); // message displayed when the inserted test requirement is not valid.
			}
			
			Activator.getDefault().getTestRequirementController().generateTestRequirement();
		}
		Activator.getDefault().getEditorController().setListenUpdates(true);
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
				Activator.getDefault().getCFGController().settLinkState(false);
				stateLink.setValue(false); // disable selection
				handlerService.executeCommand(Description.LINK_BUTTON, null); // update the value.
			}
			if(!valueLayer.equals(Integer.toString(Layer.EMPTY.getLayer())) && !valueLayer.equals(Description.NONE))
				handlerService.executeCommand(Description.LAYER_BUTTON, null); // update the value.
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}