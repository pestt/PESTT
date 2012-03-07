package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.structural.TableViewerFactory;

import domain.constants.TableViewers;

public class ViewStructuralCriteria extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		TableViewerFactory.INSTANCE.createTebleViewer(parent, getSite(), TableViewers.TEST_REQUIREMENTS_VIEWER_ID);
		TableViewerFactory.INSTANCE.createTebleViewer(parent, getSite(), TableViewers.TEST_PATHS_VIEWER_ID);
		TableViewerFactory.INSTANCE.createTebleViewer(parent, getSite(), TableViewers.STATISTICS_VIEWER_ID);
	}

	@Override
	public void setFocus() {
		// does nothing.
	}	
}