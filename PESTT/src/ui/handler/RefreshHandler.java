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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import ui.constants.Description;
import ui.constants.JavadocTagAnnotations;
import ui.constants.Messages;
import ui.constants.Preferences;
import ui.editor.ActiveEditor;
import adt.graph.Path;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.Layer;
import domain.constants.TestType;
import domain.coverage.data.CoverageData;
import domain.coverage.data.ICoverageData;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (Activator.getDefault().getEditorController().getActiveEditor() != null) {
			Activator.getDefault().getEditorController().getActiveEditor()
					.deleteObservers();
			Activator.getDefault().getViewController().deleteObserverToViews();
		}
		Activator.getDefault().getEditorController()
				.setEditor(new ActiveEditor());
		Activator.getDefault().getEditorController().getActiveEditor()
				.addObservers();
		Activator.getDefault().getViewController().addObserverToViews();
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		if (Activator.getDefault().getEditorController().isInMethod()) { // if the text selected is the name of the method.
			IPreferenceStore preferenceStore = Activator.getDefault()
					.getPreferenceStore();
			String dot = preferenceStore.getString(Preferences.DOT_PATH);
			if (dot != null && !dot.equals(Description.EMPTY)) {
				Activator.getDefault().getViewController()
						.loadNecessaryViews(event);
				resetDataStructures(window);
				keepCommandOptions();
			} else
				MessageDialog.openInformation(window.getShell(),
						Messages.PREFERENCES_TITLE,
						Messages.PREFERENCES_DOT_MSG);
		} else
			MessageDialog.openInformation(window.getShell(),
					Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		return null;
	}

	private void resetDataStructures(IWorkbenchWindow window) {
		Activator.getDefault().getEditorController().setListenUpdates(false);
		Activator.getDefault().getCoverageDataController()
				.deleteObserverToCoverageData();
		Activator.getDefault().getDefUsesController().deleteObserverToDefUses();
		Activator.getDefault().getTestPathController().cleanTestPathSet();
		Activator.getDefault().getTestRequirementController()
				.cleanTestRequirementSet();
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
		Activator.getDefault().getCFGController()
				.refreshStructuralCoverageGraph();
		Activator.getDefault().getCFGController().refreshLogicCoverageGraph();
		Activator.getDefault().getCoverageDataController()
				.addObserverToCoverageData();
		Activator.getDefault().getDefUsesController().addObserverToDefUses();
		Map<JavadocTagAnnotations, List<String>> javadocAnnotations = Activator
				.getDefault().getSourceGraphController()
				.getJavadocAnnotations();
		List<String> criteria = javadocAnnotations
				.get(JavadocTagAnnotations.COVERAGE_CRITERIA);
		if (criteria != null && !criteria.isEmpty()) {
			Activator
					.getDefault()
					.getTestRequirementController()
					.selectCoverageCriteria(
							GraphCoverageCriteriaId.valueOf(criteria.get(0)));
			List<String> tour = javadocAnnotations
					.get(JavadocTagAnnotations.TOUR_TYPE);
			if (!tour.isEmpty())
				Activator.getDefault().getTestPathController()
						.selectTourType(tour.get(0));
			setInformationFromJavadoc(window, javadocAnnotations,
					JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH,
					Messages.TEST_REQUIREMENT_TITLE,
					Messages.TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG + "\n",
					"\n" + Messages.TEST_REQUIREMENT_REMOVE_MSG);
			setInformationFromJavadoc(window, javadocAnnotations,
					JavadocTagAnnotations.INFEASIBLE_PATH,
					Messages.TEST_REQUIREMENT_TITLE,
					Messages.TEST_REQUIREMENT_BECAME_INVALID_INPUT_MSG + "\n",
					"\n" + Messages.TEST_REQUIREMENT_REMOVE_MSG);
			setInformationFromJavadoc(window, javadocAnnotations,
					JavadocTagAnnotations.ADDITIONAL_TEST_PATH,
					Messages.TEST_PATH_TITLE,
					Messages.TEST_PATH_BECAME_INVALID_INPUT_MSG + "\n", "\n"
							+ Messages.TEST_PATH_REMOVE_MSG);
			if (isADefUsesCoverageVriteria())
				Activator.getDefault().getDefUsesController().generateDefUses();
			Activator.getDefault().getTestRequirementController()
					.generateTestRequirement();
		}
		Activator.getDefault().getEditorController().setListenUpdates(true);
	}

	private void setInformationFromJavadoc(IWorkbenchWindow window,
			Map<JavadocTagAnnotations, List<String>> javadocAnnotations,
			JavadocTagAnnotations tag, String title, String prefix, String sufix) {
		for (String input : javadocAnnotations.get(tag)) {
			String msg = input;
			input = input.substring(1, input.length() - 1);
			Path<Integer> path = Activator.getDefault()
					.getTestRequirementController()
					.createTestRequirement(input);
			if (path != null)
				switch (tag) {
				case ADDITIONAL_TEST_REQUIREMENT_PATH:
					Activator.getDefault().getTestRequirementController()
							.addTestRequirement(path);
					break;
				case INFEASIBLE_PATH:
					Activator.getDefault().getTestRequirementController()
							.enableInfeasible(path);
					break;
				case ADDITIONAL_TEST_PATH:
					Activator.getDefault().getTestPathController()
							.addTestPath(path, TestType.MANUALLY);
					List<ICoverageData> newData = new LinkedList<ICoverageData>();
					newData.add(new CoverageData(path));
					break;
				default:
					break;
				}
			else
				MessageDialog.openInformation(window.getShell(), title, prefix
						+ msg + sufix); // message displayed when the inserted test requirement is not valid.
		}
	}

	private boolean isADefUsesCoverageVriteria() {
		switch (Activator.getDefault().getTestRequirementController()
				.getSelectedCoverageCriteria()) {
		case ALL_DU_PATHS:
		case ALL_USES:
		case ALL_DEFS:
			return true;
		default:
			return false;
		}
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
				Activator.getDefault().getCFGController().settLinkState(false);
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
