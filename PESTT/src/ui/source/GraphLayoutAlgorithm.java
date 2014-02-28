package ui.source;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.interfaces.EntityLayout;
import org.eclipse.gef4.zest.layouts.interfaces.LayoutContext;
import org.eclipse.swt.widgets.Composite;

public class GraphLayoutAlgorithm implements LayoutAlgorithm {

	private double layoutWidth;
	private double layoutHeight;
	GraphElements graphElements;
	private LayoutContext context;

	public GraphLayoutAlgorithm(final Composite parent,
			GraphElements graphElements) {
		this.graphElements = graphElements;
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	@Override
	public void applyLayout(boolean clean) {
		EntityLayout[] entries = context.getEntities();
		layoutHeight = context.getBounds().height;
		layoutWidth = context.getBounds().width;
		setNodePosition(graphElements, graphElements.getNodesInfo(), entries);
	}

	private void setNodePosition(GraphElements graphelements,
			Map<String, Node> nodeslist, EntityLayout[] graphNodes) {
		double pointX = 0;
		double pointY = 0;
		int current = 0;

		Set<Entry<String, Node>> set = nodeslist.entrySet();
		Iterator<Entry<String, Node>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Node> entry = iterator.next();
			Node node = entry.getValue();
			pointX = convertX(layoutWidth, graphelements.getGraphWidth(),
					node.getXPosition());
			pointY = convertY(layoutHeight, graphelements.getGraphHeight(),
					node.getYPosition());
			EntityLayout nodeEntity = graphNodes[current++];
			nodeEntity.setLocation(pointX, pointY);
		}
	}

	private double convertX(double screen, double graph, double point) {
		return (screen * point / graph);
	}

	private double convertY(double screen, double graph, double point) {
		return screen - (screen * point / graph);
	}
}