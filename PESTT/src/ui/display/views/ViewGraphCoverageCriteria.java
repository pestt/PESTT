package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.structural.GraphCoverageCriteria;


public class ViewGraphCoverageCriteria extends ViewPart  {
	
	private GraphCoverageCriteria graph;
	
	@Override
	public void createPartControl(Composite parent) {
		graph = new GraphCoverageCriteria(parent);
	}
	
	@Override
	public void setFocus() {
	}
	
	@Override
	public void dispose() {
	if(graph != null)
		graph.dispose();
	super.dispose();
	}
}