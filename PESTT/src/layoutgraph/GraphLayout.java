package layoutgraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import constants.Description_ID;

public class GraphLayout {
	
	private int layoutWidth = 0;
	private int layoutHeight = 0;
	private Composite parent;
	GraphElements graphElements;
	
	
	public GraphLayout(final Composite parent, GraphElements graphElements) {
		try {
			this.parent = parent;
			this.graphElements = graphElements;
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Description_ID.VIEW_GRAPH);
			layoutWidth = parent.getSize().x;
			layoutHeight = parent.getSize().y;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	public LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout = new LayoutAlgorithm() {
			private LayoutContext context;

			public void setLayoutContext(LayoutContext context) {
				this.context = context;
			}

			public void applyLayout(boolean clean) {
				EntityLayout[] entries = context.getEntities();
				setNodePosition(parent, graphElements, graphElements.getNodesInfo(), entries);
			}
		};

		return layout;
	}
	
	private void setNodePosition(Composite parent, GraphElements graphelements, HashMap<String, Node> nodeslist, EntityLayout[] graphnodes) {
		double pointX = 0;
		double pointY = 0;
		int current = 0;
		
		Set<Entry<String, Node>> set = nodeslist.entrySet();
		Iterator<Entry<String, Node>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Node> entry = iterator.next();
			Node node = entry.getValue();
			pointX = convertX(layoutWidth, graphelements.getGraphWidth(), node.getXPosition());
			pointY = convertY(layoutHeight, graphelements.getGraphHeight(), node.getYPosition());
			EntityLayout nodeentity = graphnodes[current++];
			nodeentity.setLocation(pointX, pointY);
			resize(parent, nodeentity, pointX, pointY);
		}
	}

	private double convertX(int screen, double graph, double point) {
		return (screen * point / graph);
	}

	private double convertY(int screen, double graph, double point) {
		return screen - (screen * point / graph);
	}
	
	private void resize(Composite parent, EntityLayout node, double pointX, double pointY) {
		// Margin right
		if ((pointX > layoutWidth) || ((pointX + node.getSize().width) > layoutWidth))
			parent.setSize((int) (node.getLocation().x + node.getSize().width / 2), parent.getSize().y);

		// Margin bottom
		if (pointY > layoutHeight) 
			parent.setSize(parent.getSize().x, (int) (node.getLocation().y + node.getSize().height / 2));
	} 

}