package coveragealgorithms;

import handler.RunCoverageHandler;
import handler.TestRequirementsHandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Label;
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
	private List<GraphNode> nodes;
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
		cpc.setData(CoverageAlgorithms_ID.COMPLETE_PATH_ID);
		cpc.setTooltip(new Label("Complete Path Coverage (CPC):\nTest requirements contains all paths in Graph."));
		nodes.add(cpc); 
		GraphNode ppc = new GraphNode(graph, SWT.SINGLE, "Prime Path\n Coverage\n-------------------\n      (PPC)");
		ppc.setData(CoverageAlgorithms_ID.PRIME_PATH_ID);
		ppc.setTooltip(new Label("Prime Path Coverage (PPC):\nTest requirements contains each prime path in Graph."));
		nodes.add(ppc);
		GraphNode adupc = new GraphNode(graph, SWT.SINGLE, "All-du-Paths\n  Coverage\n-------------------\n  (ADUPC)");
//		adupc.setData(CoverageAlgorithms_ID.ALL_DU_PATHS_ID);
		adupc.setTooltip(new Label("All-du-Paths Coverage (ADUPC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains every path d in S."));
		nodes.add(adupc); 
		GraphNode epc = new GraphNode(graph, SWT.SINGLE, "Edge-Pair\nCoverage\n----------------\n     (EPC)");
		epc.setData(CoverageAlgorithms_ID.EDGE_PAIR_ID);
		epc.setTooltip(new Label("Edge-Pair Coverage (EPC):\nTest requirements contains each reachable path of length up to 2, inclusive, in Graph."));
		nodes.add(epc); 
		GraphNode crtc = new GraphNode(graph, SWT.SINGLE, "Complete Round\n   Trip Coverage\n----------------------------\n           (CRTC)");
		crtc.setData(CoverageAlgorithms_ID.COMPLETE_ROUND_TRIP_ID);
		crtc.setTooltip(new Label("Complete Round Trip Coverage (CRTC):\nTest requirements contains all round-trip paths for each reachable node in Graph."));
		nodes.add(crtc); 
		GraphNode auc = new GraphNode(graph, SWT.SINGLE, "  All-Uses\nCoverage\n----------------\n    (AUC)");
//		auc.setData(CoverageAlgorithms_ID.ALL_USES_ID);
		auc.setTooltip(new Label("All-Uses Coverage (AUC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains at least one path d in S."));
		nodes.add(auc); 
		GraphNode ec = new GraphNode(graph, SWT.SINGLE, "     Edge\n Coverage\n----------------\n      (EC)");
		ec.setData(CoverageAlgorithms_ID.EDGE_ID);
		ec.setTooltip(new Label("Edge Coverage (EC):\nTest requirements contains each reachable path of length up to 1, inclusive, in Graph."));
		nodes.add(ec); 
		GraphNode srtc = new GraphNode(graph, SWT.SINGLE, "Simple Round\nTrip Coverage\n-----------------------\n        (SRTC)");
		srtc.setData(CoverageAlgorithms_ID.SIMPLE_ROUND_TRIP_ID);
		srtc.setTooltip(new Label("Simple Round Trip Coverage (SRTC):\nTest requirements contains at least one round-trip path\n for each reachable node in Graph that begins and ends a round-trip path."));
		nodes.add(srtc); 
		GraphNode adc = new GraphNode(graph, SWT.SINGLE, "  All-Defs\nCoverage\n----------------\n    (ADC)");
//		adc.setData(CoverageAlgorithms_ID.ALL_DEFS_ID);
		adc.setTooltip(new Label("All-Defs Coverage (ADC):\nFor each def-path set S = du(n, v),\nTest requirements contains at least one path d in S."));
		nodes.add(adc); 
		GraphNode nc = new GraphNode(graph, SWT.SINGLE, "    Node\nCoverage\n----------------\n     (NC)");
		nc.setData(CoverageAlgorithms_ID.NODE_ID);
		nc.setTooltip(new Label("Node Coverage (NC):\nTest requirements contains each reachable node in Grpah."));
		nodes.add(nc); 
		for(GraphNode gnode : nodes) {
			gnode.setBackgroundColor(Colors_ID.WHITE); 
			gnode.setForegroundColor(Colors_ID.BLACK); 
			gnode.setBorderColor(Colors_ID.BLACK); 
			gnode.setHighlightColor(Colors_ID.YELLOW); 
			gnode.setBorderHighlightColor(Colors_ID.BLACK); 
		}
		adupc.setBackgroundColor(Colors_ID.RED);
		adupc.setForegroundColor(Colors_ID.WHITE); 
		auc.setBackgroundColor(Colors_ID.RED);
		auc.setForegroundColor(Colors_ID.WHITE); 
		adc.setBackgroundColor(Colors_ID.RED);
		adc.setForegroundColor(Colors_ID.WHITE); 
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
	
	public List<GraphNode> getCriteriaGraphNodes() {
		return nodes;
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
					String option = (String) getSelected().getData();
					TestRequirementsHandler.setSelectionCriteria(option);
					RunCoverageHandler.setSelectionCriteria(option);
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
	
	public void setSelected(GraphItem[] items) {
		graph.setSelection(items); // the items selected.
	}
}