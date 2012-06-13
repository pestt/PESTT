package ui.display.views.structural;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

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

import ui.constants.Colors;
import ui.events.RefreshStructuralGraphEvent;
import domain.constants.GraphCoverageCriteriaId;
import domain.events.TestRequirementSelectedCriteriaEvent;

@SuppressWarnings("deprecation")
public class GraphCoverageCriteria implements Observer {
	
	private Graph graph;
	private Map<GraphCoverageCriteriaId, GraphNode> nodes;
	private SelectionAdapter event;	

	public GraphCoverageCriteria(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getCFGController().addObserver(this);
		create();
	}

	private void create() {
		graph.clear();
		removeSelectionListener();
		setNodes();
		setEdges();
		setLayout();
		addSelectionListener();
		if(Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected()) 
			setSelected(nodes.get(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()));
	}

	public void dispose() {
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
		Activator.getDefault().getCFGController().deleteObserver(this);
	}

	
	private void setNodes() {
		nodes = new HashMap<GraphCoverageCriteriaId, GraphNode>();
		GraphNode cpc = new GraphNode(graph, SWT.SINGLE, "Complete Path\n      Coverage\n" + insertTrace(14) + "\n            (CPC)");
		cpc.setData(GraphCoverageCriteriaId.COMPLETE_PATH);
		cpc.setTooltip(new Label("Complete Path Coverage (CPC):\nTest requirements contains all paths in Graph."));
		nodes.put(GraphCoverageCriteriaId.COMPLETE_PATH, cpc); 
			
		GraphNode ppc = new GraphNode(graph, SWT.SINGLE, "Prime Path\n Coverage\n" + insertTrace(11) + "\n      (PPC)");
		ppc.setData(GraphCoverageCriteriaId.PRIME_PATH);
		ppc.setTooltip(new Label("Prime Path Coverage (PPC):\nTest requirements contains each prime path in Graph."));
		nodes.put(GraphCoverageCriteriaId.PRIME_PATH, ppc); 

		GraphNode adupc = new GraphNode(graph, SWT.SINGLE, "All-du-Paths\n  Coverage\n " + insertTrace(11) + "\n    (ADUPC)");
		adupc.setData(GraphCoverageCriteriaId.ALL_DU_PATHS);
		adupc.setTooltip(new Label("All-du-Paths Coverage (ADUPC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains every path d in S."));		
		nodes.put(GraphCoverageCriteriaId.ALL_DU_PATHS, adupc); 
		
		GraphNode epc = new GraphNode(graph, SWT.SINGLE, "Edge-Pair\nCoverage\n" + insertTrace(9) + "\n     (EPC)");
		epc.setData(GraphCoverageCriteriaId.EDGE_PAIR);
		epc.setTooltip(new Label("Edge-Pair Coverage (EPC):\nTest requirements contains each reachable path of length up to 2, inclusive, in Graph."));
		nodes.put(GraphCoverageCriteriaId.EDGE_PAIR, epc); 

		GraphNode crtc = new GraphNode(graph, SWT.SINGLE, "Complete Round\n   Trip Coverage\n" + insertTrace(16) + "\n           (CRTC)");
		crtc.setData(GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP);
		crtc.setTooltip(new Label("Complete Round Trip Coverage (CRTC):\nTest requirements contains all round-trip paths for each reachable node in Graph."));
		nodes.put(GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP, crtc); 

		GraphNode auc = new GraphNode(graph, SWT.SINGLE, "  All-Uses\nCoverage\n" + insertTrace(9) + "\n    (AUC)");
		auc.setData(GraphCoverageCriteriaId.ALL_USES);
		auc.setTooltip(new Label("All-Uses Coverage (AUC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains at least one path d in S."));
		nodes.put(GraphCoverageCriteriaId.ALL_USES, auc); 

		GraphNode ec = new GraphNode(graph, SWT.SINGLE, "     Edge\n Coverage\n" + insertTrace(10) + "\n      (EC)");
		ec.setData(GraphCoverageCriteriaId.EDGE);
		ec.setTooltip(new Label("Edge Coverage (EC):\nTest requirements contains each reachable path of length up to 1, inclusive, in Graph."));
		nodes.put(GraphCoverageCriteriaId.EDGE, ec); 

		GraphNode srtc = new GraphNode(graph, SWT.SINGLE, "Simple Round\nTrip Coverage\n" + insertTrace(13) + "\n        (SRTC)");
		srtc.setData(GraphCoverageCriteriaId.SIMPLE_ROUND_TRIP);
		srtc.setTooltip(new Label("Simple Round Trip Coverage (SRTC):\nTest requirements contains at least one round-trip path\n for each reachable node in Graph that begins and ends a round-trip path."));
		nodes.put(GraphCoverageCriteriaId.SIMPLE_ROUND_TRIP, srtc); 

		GraphNode adc = new GraphNode(graph, SWT.SINGLE, "  All-Defs\nCoverage\n" + insertTrace(9) + "\n    (ADC)");
		adc.setData(GraphCoverageCriteriaId.ALL_DEFS);
		adc.setTooltip(new Label("All-Defs Coverage (ADC):\nFor each def-path set S = du(n, v),\nTest requirements contains at least one path d in S."));
		nodes.put(GraphCoverageCriteriaId.ALL_DEFS, adc); 

		GraphNode nc = new GraphNode(graph, SWT.SINGLE, "    Node\nCoverage\n" + insertTrace(9) + "\n     (NC)");
		nc.setData(GraphCoverageCriteriaId.NODE);
		nc.setTooltip(new Label("Node Coverage (NC):\nTest requirements contains each reachable node in Grpah."));
		nodes.put(GraphCoverageCriteriaId.NODE, nc); 
		
		for(GraphNode gnode : nodes.values()) {
			gnode.setBackgroundColor(Colors.WHITE); 
			gnode.setForegroundColor(Colors.BLACK); 
			gnode.setBorderColor(Colors.BLACK); 
			gnode.setHighlightColor(Colors.YELLOW); 
			gnode.setBorderHighlightColor(Colors.BLACK); 
		}
	}
	
	private void setEdges() {
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.COMPLETE_PATH), nodes.get(GraphCoverageCriteriaId.PRIME_PATH));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.PRIME_PATH), nodes.get(GraphCoverageCriteriaId.ALL_DU_PATHS));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.PRIME_PATH), nodes.get(GraphCoverageCriteriaId.EDGE_PAIR));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.PRIME_PATH), nodes.get(GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.ALL_DU_PATHS), nodes.get(GraphCoverageCriteriaId.ALL_USES));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.EDGE_PAIR), nodes.get(GraphCoverageCriteriaId.EDGE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP), nodes.get(GraphCoverageCriteriaId.SIMPLE_ROUND_TRIP));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.ALL_USES), nodes.get(GraphCoverageCriteriaId.EDGE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.ALL_USES), nodes.get(GraphCoverageCriteriaId.ALL_DEFS));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.EDGE), nodes.get(GraphCoverageCriteriaId.NODE));
	}

	private String insertTrace(int num) {
		String line = "";
		for(int i = 0; i < num; i++)
			line += (char)0x2013 + "";
		return line;
	}

	private void setLayout() {
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	private void addSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item != null && e.item instanceof GraphNode ) {
					setSelected(null);
					setSelected((GraphItem) e.item);
					GraphCoverageCriteriaId option = (GraphCoverageCriteriaId) getSelected().getData();
					Activator.getDefault().getTestRequirementController().selectCoverageCriteria(option);
				} else
					Activator.getDefault().getTestRequirementController().selectCoverageCriteria(null);				
			}
		};	
		graph.addSelectionListener(event);				
	}
	
	private void removeSelectionListener() {
		if(event != null)
			graph.removeSelectionListener(event);
	}

	private GraphItem getSelected() {
		if(graph.getSelection().size() == 0)
			return null;
		return (GraphItem) graph.getSelection().get(0); // return the list with the selected nodes.
	}

	private void setSelected(GraphItem item) {
		graph.setSelection(item == null ? null : new GraphItem[] {item}); // the items selected.
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof TestRequirementSelectedCriteriaEvent)
			setSelected(nodes.get(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()));
		else if(data instanceof RefreshStructuralGraphEvent)
			create();
	}
}