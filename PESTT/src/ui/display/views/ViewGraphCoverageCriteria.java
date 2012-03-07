package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.structural.GraphCoverageCriteria;


public class ViewGraphCoverageCriteria extends ViewPart  {
	
	@Override
	public void createPartControl(Composite parent) {
		new GraphCoverageCriteria(parent);
	}
	
	@Override
	public void setFocus() {
	}
}