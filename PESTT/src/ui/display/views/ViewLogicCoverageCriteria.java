package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.logic.LogicCoverageCriteria;

public class ViewLogicCoverageCriteria extends ViewPart  {
	
	private LogicCoverageCriteria graph;
	
	@Override
	public void createPartControl(Composite parent) {
		graph = new LogicCoverageCriteria(parent);
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