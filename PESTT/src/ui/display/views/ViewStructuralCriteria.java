package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.constants.TableViewers;
import ui.display.views.structural.TableViewerFactory;

public class ViewStructuralCriteria extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		TableViewerFactory.INSTANCE.createTableViewer(parent, getSite(),
				TableViewers.TEST_REQUIREMENTS_VIEWER_ID);
		TableViewerFactory.INSTANCE.createTableViewer(parent, getSite(),
				TableViewers.TEST_PATHS_VIEWER_ID);
		TableViewerFactory.INSTANCE.createTableViewer(parent, getSite(),
				TableViewers.STATISTICS_VIEWER_ID);
	}

	@Override
	public void setFocus() {
		// does nothing.
	}
}