package ui.display.views.structural.defuses;

import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.core.commands.State;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import ui.constants.Description;
import ui.events.DefUsesChangeViewEvent;
import domain.constants.DefUsesView;

public class DefUsesViewerFactory implements Observer {

	private Composite parent;
	private IWorkbenchPartSite site;
	private DefUsesView view;
	private IDefUsesViewer viewer;

	public DefUsesViewerFactory() {
		Activator.getDefault().getDefUsesController().addObserver(this);
		ICommandService cmdService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		State state = cmdService.getCommand(Description.DEF_USES_VIEWER_BUTTON)
				.getState("org.eclipse.ui.commands.radioState");
		String value = state.getValue().toString();
		Activator.getDefault().getDefUsesController().selectView(value);
		view = Activator.getDefault().getDefUsesController().getSelectedView();
	}

	public TableViewer createTableViewer(Composite parent,
			IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		switch (view) {
		case NODE_EDGE:
			viewer = new DefUsesViewerByNodeEdge(parent, site);
			viewer.addObservers();
			return viewer.create();
		case VARIABLE:
			viewer = new DefUsesViewerByVariable(parent, site);
			viewer.addObservers();
			return viewer.create();
		default:
			return null;//normal
		}
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof DefUsesChangeViewEvent)
			if (parent != null) {
				view = ((DefUsesChangeViewEvent) data).selectedDefUseView;
				if (viewer != null) {
					viewer.deleteObservers();
					viewer.dispose();
				}
				createTableViewer(parent, site);
				parent.layout();
			}
	}
}
