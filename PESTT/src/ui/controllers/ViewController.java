package ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Description;
import adt.graph.Path;
import domain.events.CFGCreateEvent;
import domain.events.DefUsesSelectedEvent;
import domain.events.TestPathSelectedEvent;
import domain.events.TestRequirementSelectedEvent;

public class ViewController implements Observer {

	private List<String> views;

	public void addObserverToViews() {
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getDefUsesController().addObserver(this);
		Activator.getDefault().getSourceGraphController()
				.addObserverSourceGraph(this);
		views = new ArrayList<String>();
		addAllViews(views);
	}

	public void deleteObserverToViews() {
		Activator.getDefault().getTestRequirementController()
				.deleteObserver(this);
		Activator.getDefault().getTestPathController().deleteObserver(this);
		Activator.getDefault().getDefUsesController().deleteObserver(this);
		Activator.getDefault().getSourceGraphController()
				.deleteObserverSourceGraph(this);
	}

	private void addAllViews(List<String> views) {
		views.add(Description.VIEW_GRAPH);
		views.add(Description.VIEW_GRAPH_COVERAGE_CRITERIA);
		views.add(Description.VIEW_LOGIC_COVERAGE_CRITERIA);
		views.add(Description.VIEW_STRUCTURAL_COVERAGE);
		views.add(Description.VIEW_DATA_FLOW_COVERAGE);
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof CFGCreateEvent)
			bringViewToTop(Description.VIEW_GRAPH);
		else if (data instanceof TestRequirementSelectedEvent) {
			Object selectedTestRequirement = ((TestRequirementSelectedEvent) data).selectedTestRequirement;
			if (selectedTestRequirement != null) {
				bringViewToTop(Description.VIEW_GRAPH);
				bringViewToTop(Description.VIEW_STRUCTURAL_COVERAGE);
			}
		} else if (data instanceof TestPathSelectedEvent) {
			Set<Path> selectedTestPathSelectedEvent = ((TestPathSelectedEvent) data).selectedTestPaths;
			if (selectedTestPathSelectedEvent != null
					&& !selectedTestPathSelectedEvent.isEmpty()) {
				bringViewToTop(Description.VIEW_GRAPH);
				bringViewToTop(Description.VIEW_STRUCTURAL_COVERAGE);
			}
		} else if (data instanceof DefUsesSelectedEvent) {
			Object selectedDefUses = ((DefUsesSelectedEvent) data).selectedDefUse;
			if (selectedDefUses != null) {
				bringViewToTop(Description.VIEW_GRAPH);
				bringViewToTop(Description.VIEW_DATA_FLOW_COVERAGE);
			}
		}
	}

	/**
	 * Brings the desired view to the top.
	 */
	public void bringViewToTop(String view) {
		try {
			if (view != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(view);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void loadNecessaryViews(ExecutionEvent event) {
		for (String viewId : views) {
			if (HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.findView(viewId) == null)
				bringViewToTop(viewId);
		}
	}

	public void bringEditorToTop(IEditorPart part) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.bringToTop(part);
	}

}
