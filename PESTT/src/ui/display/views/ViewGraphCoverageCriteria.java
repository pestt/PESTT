package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.structural.GraphCoverageCriteria;

/**
 * The Graph Coverage Criteria View. This file is the extension point from Eclipse.
 * The actual code for constructing the view is done by 
 * class ui.display.views.structural.GraphCoverageCriteria.
 * 
 * @author Rui Gameiro
 * @version 1.0 (1/3/2014)
 */
public class ViewGraphCoverageCriteria extends ViewPart {

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
		if (graph != null)
			graph.dispose();
		super.dispose();
	}
}