package ui.source;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ui.constants.Colors;
import ui.constants.Description;
import ui.constants.Messages;
import ui.constants.Preferences;
import ui.events.LayerChangeEvent;
import ui.events.LinkChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.constants.Layer;
import domain.dot.processor.DotProcess;
import domain.dot.processor.IDotProcess;
import domain.events.CFGCreateEvent;
import domain.events.DefUsesSelectedEvent;
import domain.events.TestPathSelectedEvent;
import domain.events.TestRequirementSelectedEvent;
import domain.graph.visitors.DotGraphVisitor;

public class Graph implements Observer {

	private org.eclipse.gef4.zest.core.widgets.Graph graph;
	private adt.graph.Graph<Integer> sourceGraph;
	private List<GraphNode> graphNodes;
	private List<GraphConnection> graphEdges;
	private SelectionAdapter event;
	private Composite parent;

	public Graph(Composite parent) {
		this.parent = parent;
		graph = new org.eclipse.gef4.zest.core.widgets.Graph(parent, SWT.NONE);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getDefUsesController().addObserver(this);
		Activator.getDefault().getSourceGraphController()
				.addObserverSourceGraph(this);
		Activator.getDefault().getCFGController().addObserver(this);
		Activator.getDefault().getEditorController()
				.setGraphInformation(new VisualInformation(this));
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String dot = preferenceStore.getString(Preferences.DOT_PATH);
		if (dot != null && !dot.equals(Description.EMPTY)) {
			create(Activator.getDefault().getSourceGraphController()
					.getSourceGraph());
		}
	}

	private void create(adt.graph.Graph<Integer> sourceGraph) {
		graph.clear();
		this.sourceGraph = sourceGraph;
		DotGraphVisitor<Integer> visitor = new DotGraphVisitor<Integer>(); // creates the visitor to the graph.
		Activator.getDefault().getSourceGraphController().applyVisitor(visitor);
		String dotGraph = "digraph grafo {\nrankdir=TD\nsize=\"10,10\"\n"
				+ visitor.getDotString() + "}\n"; // creates the string to be passed to Graphviz.
		IDotProcess dotProcess = new DotProcess(); // the object that parse the information to build the layoutGraph.
		Map<String, List<String>> map = dotProcess.dotToPlain(dotGraph); // the information to build the layoutGraph.
		GraphElements elements = new GraphElements(map);
		setLayout(elements);
		setNodes(elements.getNodesInfo());
		setEdges(elements.getEdgesInfo());
	}

	public void dispose() {
		Activator.getDefault().getTestRequirementController()
				.deleteObserver(this);
		Activator.getDefault().getTestPathController().deleteObserver(this);
		Activator.getDefault().getDefUsesController().deleteObserver(this);
		Activator.getDefault().getSourceGraphController()
				.deleteObserverSourceGraph(this);
		Activator.getDefault().getCFGController().deleteObserver(this);
	}

	private void setNodes(Map<String, Node> nodes) {
		graphNodes = new LinkedList<GraphNode>(); // the list of nodes.
		Set<Entry<String, Node>> set = nodes.entrySet(); // the node properties.
		Iterator<Entry<String, Node>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Node> entry = iterator.next();
			Node node = entry.getValue(); // get the current node of the list.
			GraphNode layoutNode = new GraphNode(graph, SWT.NONE,
					node.getName()); // create the graph node.
			layoutNode.setBackgroundColor(node.getBackgroundColor()); // sets the node background color.
			layoutNode.setForegroundColor(node.getForegroundColor()); // sets the node text color.
			layoutNode.setBorderColor(Colors.BLACK); // sets the node border color to black.
			layoutNode.setHighlightColor(Colors.YELLOW); // sets he highlight color.
			layoutNode.setBorderHighlightColor(Colors.BLACK); // sets the node border highlight color to black.
			adt.graph.Node<Integer> sourceNode = sourceGraph.getNode(Integer
					.parseInt(layoutNode.getText())); // the correspondent source node.
			layoutNode.setData(sourceNode); // associate the visualization node with the source node.	
			new domain.GraphInformation().addInformationToLayer0(sourceGraph,
					sourceNode, layoutNode); // associate the the nodes of sourceGraph and layoutGraph.
			graphNodes.add(layoutNode); // add the node to the list.
		}
	}

	private void setEdges(Map<String, Edge> edges) {
		graphEdges = new LinkedList<GraphConnection>(); // the list of edges.
		GraphNode begin; // the initial node of the edge.
		GraphNode end; // the final node of the edge.
		int index = 0;
		Set<Entry<String, Edge>> set = edges.entrySet(); // the edge properties.
		Iterator<Entry<String, Edge>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Edge> entry = iterator.next();
			Edge edge = entry.getValue(); // get the current connection of the list.
			index = 0; // initialize the index.
			begin = null; // initialize the begin node of the connection.
			end = null; // initialize the end node of the connection.
			while ((begin == null || end == null) && index < graphNodes.size()) {
				if (edge.getBeginNode().equals(graphNodes.get(index).getText())) {
					begin = graphNodes.get(index); // assign the graph node to the start of the connection..
				}
				if (edge.getEndNode().equals(graphNodes.get(index).getText())) {
					end = graphNodes.get(index); // assign the the graph node to the end of the connection.
				}
				index++;
			}
			GraphConnection connection = new GraphConnection(graph,
					ZestStyles.CONNECTIONS_DIRECTED, begin, end); // create the graph connection between the start and the end nodes.
			connection.setLineColor(edge.getColor()); // sets the edge color.
			adt.graph.Node<Integer> beginNode = sourceGraph.getNode(Integer
					.parseInt(begin.getText())); // the correspondent begin node.
			adt.graph.Node<Integer> endNode = sourceGraph.getNode(Integer
					.parseInt(end.getText())); // the correspondent final node.
			// verify which node is the correct.
			for (adt.graph.Edge<Integer> sourceEdge : sourceGraph
					.getNodeEdges(beginNode)) { // get all edges of the begin node.
				if (sourceEdge.getEndNode().equals(endNode)) { // when the edges matches.
					connection.setData(sourceEdge); // associate the visualization edge with the source edge.
					sourceGraph.addMetadata(sourceEdge, connection); // associate the the edge of sourceGraph and layoutGraph.
				}
			}
			graphEdges.add(connection); // add the edge to the list.
		}
	}

	/**
	 * The Graph nodes.
	 * 
	 * @return List<GraphNode> - The Graph nodes.
	 */
	public List<GraphNode> getGraphNodes() {
		return graphNodes; // the list of nodes.
	}

	/**
	 * The Graph edges.
	 * 
	 * @return List<GraphConnection> - The Graph edges.
	 */
	public List<GraphConnection> getGraphEdges() {
		return graphEdges; // the list of connections.
	}

	/**
	 * Define the Graph's Layout Algorithm.
	 * 
	 * @param graphElements
	 *            - The Graph elements.
	 */
	private void setLayout(GraphElements graphElements) {
		graph.setLayoutAlgorithm(
				new GraphLayoutAlgorithm(parent, graphElements), true);
	}

	/**
	 * The Graph's selected items.
	 * 
	 * @return List&lt;GraphItem&gt; - The Graph selected items.
	 */
	@SuppressWarnings("unchecked")
	public List<GraphItem> getSelected() {
		return graph.getSelection();
	}

	/**
	 * Selects the items in the Graph.
	 * 
	 * @param items
	 *            - The items to select in the Graph.
	 */
	public void setSelected(GraphItem[] items) {
		graph.setSelection(items); // the items selected.
	}

	/**
	 * Unselects all select items in the Graph.
	 */
	private void unselectAll() {
		GraphItem[] items = {};
		setSelected(items);
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof CFGCreateEvent) {
			create(((CFGCreateEvent) data).sourceGraph);
			Activator.getDefault().getEditorController().everythingMatch();
		} else if (data instanceof LayerChangeEvent)
			Activator.getDefault().getEditorController()
					.setLayerInformation(((LayerChangeEvent) data).layer);
		else if (data instanceof LinkChangeEvent) {
			if (((LinkChangeEvent) data).state) {
				Activator.getDefault().getEditorController()
						.createSelectToEditor(); // create the SelectionListener to the editor.
				createSelectionListener(); // create the SelectionAdapter event to the nodes.
			} else {
				Activator.getDefault().getEditorController()
						.removeSelectToEditor(); // remove the SelectionListener to the editor.
				removeSelectionListener(); // remove the SelectionAdapter event to the nodes.
			}
		} else if (data instanceof TestRequirementSelectedEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching())
				if (((TestRequirementSelectedEvent) data).selectedTestRequirement == null)
					unselectAll();
				else
					selectTestRequirement(((TestRequirementSelectedEvent) data));
			else if (((TestRequirementSelectedEvent) data).selectedTestRequirement != null)
				graphNeedToBeUpdate();
		} else if (data instanceof TestPathSelectedEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching())
				if (((TestPathSelectedEvent) data).selectedTestPaths == null
						|| ((TestPathSelectedEvent) data).selectedTestPaths
								.isEmpty())
					unselectAll();
				else
					selectTestPath(((TestPathSelectedEvent) data));
			else if (((TestPathSelectedEvent) data).selectedTestPaths != null)
				graphNeedToBeUpdate();
		} else if (data instanceof DefUsesSelectedEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching())
				if (((DefUsesSelectedEvent) data).selectedDefUse == null)
					unselectAll();
			else
					selecDefUses();
			else if (((DefUsesSelectedEvent) data).selectedDefUse != null)
				graphNeedToBeUpdate();
		}
	}

	/**
	 * Informs users that the Graph needs to be updated.
	 */
	private void graphNeedToBeUpdate() {
		unselectAll();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		MessageDialog.openInformation(window.getShell(),
				Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);
	}

	/**
	 * Shows the selected test requirement paths in the Graph.
	 * 
	 * @param data
	 *            - The selected test requirement paths.
	 */
	private void selectTestRequirement(TestRequirementSelectedEvent data) {
		List<GraphItem> aux = selectInGraph(data.selectedTestRequirement);
		GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length,
				GraphItem[].class); // convert the aux into an array of GraphItems.
		setSelected(items); // the list of selected items.
		Activator.getDefault().getEditorController()
				.addVisualCoverage(data, aux);
	}

	/**
	 * Shows the selected test paths in the Graph.
	 * 
	 * @param data
	 *            - The selected test paths.
	 */
	private void selectTestPath(TestPathSelectedEvent data) {
		List<GraphItem> aux = selectTestPathSet(data.selectedTestPaths);
		GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length,
				GraphItem[].class); // convert the aux into an array of GraphItems.
		setSelected(items); // the list of selected items.
		Activator.getDefault().getEditorController()
				.addVisualCoverage(data, aux);
	}

	/**
	 * Show the selected test path in the Graph.
	 */
	private void selecDefUses() {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		Set<List<Object>> selectedDefUse = Activator.getDefault()
				.getDefUsesController().getSelectedDefUse();
		for (List<Object> list : selectedDefUse)
			for (Object obj : list)
				aux.addAll(selectInGraph(obj));
		GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length,
				GraphItem[].class); // convert the aux into an array of GraphItems.
		setSelected(items); // the list of selected items.
		Activator.getDefault().getEditorController()
				.addVisualCoverage(selectedDefUse, aux);
	}

	private List<GraphItem> selectInGraph(
			AbstractPath<Integer> selectedTestRequirement) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		// select the nodes in the graph.	
		Iterator<adt.graph.Node<Integer>> it = selectedTestRequirement
				.iterator();
		adt.graph.Node<Integer> node = it.next();
		while (it.hasNext()) { // through all node in the path.
			adt.graph.Node<Integer> nextNode = it.next();
			getGraphNode(aux, node);

			// select the edges in the graph.
			for (adt.graph.Edge<Integer> edge : sourceGraph
					.getNodeEdges((adt.graph.Node<Integer>) node))
				// through all edges of the node.
				if (edge.getEndNode() == nextNode) // the end node of the edge (the next node in the path).
					for (GraphConnection gconnection : graphEdges)
						// through all connections in the graph.
						if (gconnection.getData().equals(edge)) { // if matches.
							aux.add(gconnection); // add connection item to the list.
							break;
						}
			node = nextNode;
		}
		getGraphNode(aux, node);
		return aux;
	}

	@SuppressWarnings("unchecked")
	private List<GraphItem> selectInGraph(Object selectedDefUse) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		if (selectedDefUse instanceof adt.graph.Node<?>) {
			adt.graph.Node<Integer> node = (adt.graph.Node<Integer>) selectedDefUse;
			getGraphNode(aux, node);
		} else if (selectedDefUse instanceof adt.graph.Edge<?>) {
			adt.graph.Edge<Integer> edge = (adt.graph.Edge<Integer>) selectedDefUse;
			for (adt.graph.Edge<Integer> e : sourceGraph.getNodeEdges(edge
					.getBeginNode()))
				if (edge == e)
					for (GraphConnection gconnection : graphEdges)
						// through all connections in the graph.
						if (gconnection.getData().equals(edge)) { // if matches.
							aux.add(gconnection); // add connection item to the list.
							break;
						}
		}
		return aux;
	}

	private void getGraphNode(List<GraphItem> aux, adt.graph.Node<Integer> node) {
		for (GraphNode gnode : graphNodes)
			// through all nodes in the graph.
			if (!gnode.isDisposed())
				if (gnode.getData().equals(node)) { // if matches.
					aux.add(gnode); // add node item to the list.
					break;
				}
	}

	private List<GraphItem> selectTestPathSet(
			Set<Path<Integer>> selectedTestPaths) {
		List<GraphItem> total = new LinkedList<GraphItem>();
		List<GraphItem> aux = null;
		for (Path<Integer> path : selectedTestPaths) {
			aux = selectInGraph(path);
			for (GraphItem item : aux)
				if (!total.contains(item))
					total.add(item);
			aux.clear();
		}
		return total;
	}

	private void createSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item != null
						&& (e.item instanceof GraphNode || e.item instanceof GraphConnection)) {
					automaticEdgeSelectopn();
					if (Activator.getDefault().getEditorController()
							.isEverythingMatching())
						Activator.getDefault().getEditorController()
								.setLayerInformation(Layer.INSTRUCTIONS);
					else
						graphNeedToBeUpdate();
				} else if (e.item == null) {
					Activator.getDefault().getEditorController()
							.removeVisualCoverage(); // removes all visual information in the editor.
					Activator.getDefault().getTestPathController()
							.unSelectTestPaths();
					Activator.getDefault().getTestRequirementController()
							.unSelectTestRequirements();
					Activator.getDefault().getDefUsesController()
							.unSelectDefUses();
				}
			}
		};
		graph.addSelectionListener(event); // add the SelectionAdapter to the graph. 
	}

	private void removeSelectionListener() {
		graph.removeSelectionListener(event); // remove the SelectionAdapter from the graph.
	}

	private void automaticEdgeSelectopn() {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		List<GraphItem> selected = getSelected();
		for (GraphConnection gconnection : graphEdges)
			if (!selected.contains(gconnection)
					&& selected.contains(gconnection.getSource())
					&& selected.contains(gconnection.getDestination()))
				aux.add(gconnection);

		aux.addAll(selected);
		GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length,
				GraphItem[].class);
		setSelected(items);
	}
}