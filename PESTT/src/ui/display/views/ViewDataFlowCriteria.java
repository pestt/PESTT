package ui.display.views;

import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.structural.defuses.DefUsesViewerFactory;
import ui.events.DefUsesChangeViewEvent;
import domain.constants.DefUsesView;

public class ViewDataFlowCriteria extends ViewPart implements Observer {
	
	private DefUsesView view;
	private Composite parent;
	private DefUsesViewerFactory factory;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		Activator.getDefault().getDefUsesController().addObserver(this);
		view = Activator.getDefault().getDefUsesController().getSelectedView();
		factory = new DefUsesViewerFactory();
		factory.createTebleViewer(parent, getSite(), view);
	}

	@Override
	public void setFocus() {
		// does nothing.
	}	
	
	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof DefUsesChangeViewEvent) {
			view = ((DefUsesChangeViewEvent) data).selectedDefUseView;
			factory.dispose();
			factory.createTebleViewer(parent, getSite(), view);
		
		}
	}
}
