package coveragealgorithms;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import constants.Colors_ID;
import constants.CoverageAlgorithms_ID;

@SuppressWarnings("deprecation")
public class GraphCoverageCriteria {
	
	private Graph graph;
	private ArrayList<GraphNode> nodes;
	private SelectionAdapter event;	

	public GraphCoverageCriteria(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		nodes = new ArrayList<GraphNode>();
		setNodes();
		setEdges();
		setLayout();
		createSelectionListener();
	}
	
	private void setNodes() {
		GraphNode cpc = new GraphNode(graph, SWT.SINGLE, "Complete Path\n      Coverage\n------------------------\n            (CPC)");
		nodes.add(cpc); 
		GraphNode ppc = new GraphNode(graph, SWT.SINGLE, "Prime Path\n Coverage\n-------------------\n      (PPC)");
		ppc.setData(CoverageAlgorithms_ID.PRIME_PATH_ID);
		nodes.add(ppc);
		GraphNode adupc = new GraphNode(graph, SWT.SINGLE, "All-du-Paths\n  Coverage\n-------------------\n  (ADUPC)");
		nodes.add(adupc); 
		GraphNode epc = new GraphNode(graph, SWT.SINGLE, "Edge-Pair\nCoverage\n----------------\n     (EPC)");
		epc.setData(CoverageAlgorithms_ID.EDGE_PAIR_ID);
		nodes.add(epc); 
		GraphNode crtc = new GraphNode(graph, SWT.SINGLE, "Complete Round\n   Trip Coverage\n----------------------------\n           (CRTC)");
		nodes.add(crtc); 
		GraphNode auc = new GraphNode(graph, SWT.SINGLE, "  All-Uses\nCoverage\n----------------\n    (AUC)");
		nodes.add(auc); 
		GraphNode ec = new GraphNode(graph, SWT.SINGLE, "     Edge\n Coverage\n----------------\n      (EC)");
		ec.setData(CoverageAlgorithms_ID.EDGE_ID);
		nodes.add(ec); 
		GraphNode srtc = new GraphNode(graph, SWT.SINGLE, "Simple Round\nTrip Coverage\n-----------------------\n        (SRTC)");
		nodes.add(srtc); 
		GraphNode adc = new GraphNode(graph, SWT.SINGLE, "  All-Defs\nCoverage\n----------------\n    (ADC)");
		nodes.add(adc); 
		GraphNode nc = new GraphNode(graph, SWT.SINGLE, "    Node\nCoverage\n----------------\n     (NC)");
		nc.setData(CoverageAlgorithms_ID.NODE_ID);
		nodes.add(nc); 
		for(GraphNode gnode : nodes) {
			gnode.setBackgroundColor(Colors_ID.WHITE); 
			gnode.setForegroundColor(Colors_ID.BLACK); 
			gnode.setBorderColor(Colors_ID.BLACK); 
			gnode.setHighlightColor(Colors_ID.YELLOW); 
			gnode.setBorderHighlightColor(Colors_ID.BLACK); 
		}
	}
	
	private void setEdges() {
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(0), nodes.get(1));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(1), nodes.get(2));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(1), nodes.get(3));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(1), nodes.get(4));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(2), nodes.get(5));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(3), nodes.get(6));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(4), nodes.get(7));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(5), nodes.get(6));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(5), nodes.get(8));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(6), nodes.get(9));
	}
	
	private void setLayout() {
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	private void createSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.
				
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item != null && e.item instanceof GraphNode ) {
					GraphItem[] items = null;
					setSelected(items);
					items = new GraphItem[1];
					items[0] = (GraphItem) e.item;
					setSelected(items);
				}
			}
		};	
		graph.addSelectionListener(event);				
	}

	public GraphItem getSelected() {
		if(graph.getSelection().size() == 0)
			return null;
		return (GraphItem) graph.getSelection().get(0); // return the list with the selected nodes.
	}
	
	private void setSelected(GraphItem[] items) {
		graph.setSelection(items); // the items selected.
	}
}
