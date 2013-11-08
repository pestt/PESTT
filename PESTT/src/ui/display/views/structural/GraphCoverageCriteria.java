package ui.display.views.structural;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.draw2d.Label;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import ui.constants.Colors;
import ui.events.RefreshStructuralGraphEvent;
import ui.source.Edge;
import ui.source.GraphElements;
import ui.source.GraphLayoutAlgorithm;
import ui.source.Node;
import domain.constants.GraphCoverageCriteriaId;
import domain.dot.processor.DotProcess;
import domain.dot.processor.IDotProcess;
import domain.events.TestRequirementSelectedCriteriaEvent;

public class GraphCoverageCriteria implements Observer {
	
	private Graph graph;
	private Map<GraphCoverageCriteriaId, GraphNode> nodes;
	private SelectionAdapter event;	
	private Composite parent;

	public GraphCoverageCriteria(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getCFGController().addObserver(this);
		this.parent = parent;
		create();
	}

	private void create() {
		graph.clear();
		removeSelectionListener();
		IDotProcess dotProcess = new DotProcess(); // the object that parse the information to build the layoutGraph.
		Map<String, List<String>> map = dotProcess.dotToPlain(dotString()); // the information to build the layoutGraph.
		GraphElements elements = new GraphElements(map);
		setLayout(elements);
		setNodes(elements.getNodesInfo());
		setEdges(elements.getEdgesInfo());
		addSelectionListener();
		if(Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected()) 
			setSelected(nodes.get(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()));
	}

	public void dispose() {
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
		Activator.getDefault().getCFGController().deleteObserver(this);
	}

	private class NodeMetaInfo {
		private String label;
		private GraphCoverageCriteriaId criteriaId;
		private String tooltip;
		
		public NodeMetaInfo(String label, GraphCoverageCriteriaId criteriaId,
				String tooltip) {
			this.label = label;
			this.criteriaId = criteriaId;
			this.tooltip = tooltip;
		}
	}
	
	
	private Map<String, NodeMetaInfo> nodesMetaInfo () {
		Map<String, NodeMetaInfo> nodesMetaInfo = new HashMap<String, NodeMetaInfo>();
		
		nodesMetaInfo.put("CPC", new NodeMetaInfo("Complete Path\n      Coverage\n" + insertTrace(14) + "\n            (CPC)",
				GraphCoverageCriteriaId.COMPLETE_PATH, "Complete Path Coverage (CPC):\nTest requirements contains all paths in Graph."));
		nodesMetaInfo.put("PPC", new NodeMetaInfo("Prime Path\n Coverage\n"  + insertTrace(11) + "\n      (PPC)", 
				GraphCoverageCriteriaId.PRIME_PATH, "Prime Path Coverage (PPC):\nTest requirements contains each prime path in Graph."));
		nodesMetaInfo.put("EC", new NodeMetaInfo("     Edge\n Coverage\n" + insertTrace(10) + "\n      (EC)",
				GraphCoverageCriteriaId.EDGE, "Edge Coverage (EC):\nTest requirements contains each reachable path of length up to 1, inclusive, in Graph."));
		nodesMetaInfo.put("ADUPC", new NodeMetaInfo("All-du-Paths\n  Coverage\n " + insertTrace(11) + "\n    (ADUPC)",		
				GraphCoverageCriteriaId.ALL_DU_PATHS, "All-du-Paths Coverage (ADUPC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains every path d in S."));
		nodesMetaInfo.put("AUC", new NodeMetaInfo("  All-Uses\nCoverage\n" + insertTrace(9) + "\n    (AUC)",
				GraphCoverageCriteriaId.ALL_USES, "All-Uses Coverage (AUC):\nFor each def-pair set S = du(ni, nj, v),\nTest requirements contains at least one path d in S."));
		nodesMetaInfo.put("CRTC", new NodeMetaInfo("Complete Round\n   Trip Coverage\n" + insertTrace(16) + "\n           (CRTC)",
				GraphCoverageCriteriaId.COMPLETE_ROUND_TRIP, "Complete Round Trip Coverage (CRTC):\nTest requirements contains all round-trip paths for each reachable node in Graph.")); 
		nodesMetaInfo.put("SRTC", new NodeMetaInfo("Simple Round\nTrip Coverage\n" + insertTrace(13) + "\n        (SRTC)",
				GraphCoverageCriteriaId.SIMPLE_ROUND_TRIP, "Simple Round Trip Coverage (SRTC):\nTest requirements contains at least one round-trip path\n for each reachable node in Graph that begins and ends a round-trip path."));
		nodesMetaInfo.put("ADC", new NodeMetaInfo("  All-Defs\nCoverage\n" + insertTrace(9) + "\n    (ADC)",
				GraphCoverageCriteriaId.ALL_DEFS, "All-Defs Coverage (ADC):\nFor each def-path set S = du(n, v),\nTest requirements contains at least one path d in S."));
		nodesMetaInfo.put("EPC", new NodeMetaInfo("Edge-Pair\nCoverage\n" + insertTrace(9) + "\n     (EPC)",
				GraphCoverageCriteriaId.EDGE_PAIR, "Edge-Pair Coverage (EPC):\nTest requirements contains each reachable path of length up to 2, inclusive, in Graph."));
		nodesMetaInfo.put("NC", new NodeMetaInfo("    Node\nCoverage\n" + insertTrace(9) + "\n     (NC)",
				GraphCoverageCriteriaId.NODE, "Node Coverage (NC):\nTest requirements contains each reachable node in Graph."));

		return nodesMetaInfo;
	}
	
	private void setNodes(Map<String, Node> nodeElements) {
		nodes = new HashMap<GraphCoverageCriteriaId, GraphNode>();
		Map<String, NodeMetaInfo> nodesMetaInfo = nodesMetaInfo();
		
		for(Entry<String, Node> entry : nodeElements.entrySet()) {
			NodeMetaInfo nmi = nodesMetaInfo.get(entry.getValue().getName());
			GraphNode gnode = new GraphNode(graph, SWT.SINGLE, nmi.label);
			gnode.setData(nmi.criteriaId);
			gnode.setTooltip(new Label(nmi.tooltip));
			gnode.setBackgroundColor(Colors.WHITE); 
			gnode.setForegroundColor(Colors.BLACK); 
			gnode.setBorderColor(Colors.BLACK); 
			gnode.setHighlightColor(Colors.YELLOW); 
			gnode.setBorderHighlightColor(Colors.BLACK); 
			nodes.put(nmi.criteriaId, gnode); 
		}
	}
	
	private void setEdges(Map<String, Edge> edges) {
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.COMPLETE_PATH), nodes.get(GraphCoverageCriteriaId.PRIME_PATH));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.COMPLETE_PATH), nodes.get(GraphCoverageCriteriaId.EDGE_PAIR));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.PRIME_PATH), nodes.get(GraphCoverageCriteriaId.ALL_DU_PATHS));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(GraphCoverageCriteriaId.PRIME_PATH), nodes.get(GraphCoverageCriteriaId.EDGE));
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

	
	/**
	 * DEfine the Graph Layout Algorithm.
	 * 
	 * @param graphElements - The Graph elements.
	 */
	private void setLayout(GraphElements graphElements) {
		 graph.setLayoutAlgorithm(new GraphLayoutAlgorithm(parent, graphElements), true);
	}
	
/*	private void setLayout() {
		TreeLayoutAlgorithm la = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN);
		la.setResizing(false);
		graph.setLayoutAlgorithm(la, true);
	}
*/
	
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
	
	private String dotString() {
		return "digraph finite_state_machine { " +
	                "rankdir=TD;" +
	                "size=\"10,10\";" +
	                "node [shape = box];" +
	                "CPC [label=\"Complete Path\\nCoverage\"];" +
	                "PPC [label=\"Prime Path\\nCoverage\"];" +	
	                "EC [label=\"Edge\\nCoverage\"];" +
	                "ADUPC [label=\"All DU Paths\\nCoverage\"];" +
	                "AUC [label=\"All Uses\\nCoverage\"];" +
	                "CRTC [label = \"Complete Round\\nTrip Coverage\"];"+
	                "SRTC [label = \"Simple Round\\nTrip Coverage\"];" +
	                "ADC [label = \"All Defs\\nCoverage\"];" +
	                "NC [label = \"Node\\nCoverage\"];" +
	                "EPC [label = \"Edge pair\\nCoverage\"];" +
	                "CPC -> EPC;" +
	                "CPC -> PPC;" +
	                "PPC -> ADUPC;" +
	                "PPC -> EC;" +
	                "PPC -> CRTC;" +
	                "CRTC -> SRTC;" +
	                "ADUPC -> AUC;" +
	                "AUC -> ADC;" +
	                "AUC -> EC;" +
	                "EPC -> EC;" +
	                "EC -> NC;" +
	                "}\n";
	}
	
}