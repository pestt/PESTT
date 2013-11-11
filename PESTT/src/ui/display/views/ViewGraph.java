package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.source.Graph;

public class ViewGraph extends ViewPart {

	private Graph graph;

	@Override
	public void createPartControl(Composite parent) {
		graph = new Graph(parent);
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