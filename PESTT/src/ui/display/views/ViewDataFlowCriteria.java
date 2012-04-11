package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.constants.TableViewers;
import ui.display.views.structural.TableViewerFactory;

public class ViewDataFlowCriteria extends ViewPart {
	
	@Override
	public void createPartControl(Composite parent) {
		TableViewerFactory.INSTANCE.createTebleViewer(parent, getSite(), TableViewers.DEF_USES_VIEWER);
	}

	@Override
	public void setFocus() {
		// does nothing.
	}	
}
