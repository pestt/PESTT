package layoutgraph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

import view.GraphsCreator;
import constants.Colors_ID;
import constants.Graph_ID;
import editor.ActiveEditor;

public class Graph {
	
	private org.eclipse.zest.core.widgets.Graph graph;
	private sourcegraph.Graph<Integer> sourceGraph;
	private List<GraphNode> graphNodes;
	private List<GraphConnection> graphEdges;
	private int layer;
	private SelectionAdapter event;
	private Composite parent;
	private ActiveEditor editor;
	
	@SuppressWarnings("unchecked")
	public Graph(Composite parent, Map<String, List<String>> elements) {
		this.parent = parent;
		this.sourceGraph = (sourcegraph.Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM);
		graph = new org.eclipse.zest.core.widgets.Graph(parent, SWT.NONE);
		GraphElements graphElements = new GraphElements(elements);
		setNodes(graphElements.getNodesInfo());
		setEdges(graphElements.getEdgesInfo());
		setLayout(graphElements);
		sourceGraph.selectMetadataLayer(0); // the layer that associate the sourceGraph elements to the layoutGraph elements.
	}
	
	private void setNodes(Map<String, Node> nodes) {
		graphNodes = new LinkedList<GraphNode>(); // the list of nodes.
		Set<Entry<String, Node>> set = nodes.entrySet(); // the node properties.
		Iterator<Entry<String, Node>> iterator = set.iterator(); 
		while(iterator.hasNext()) {
			Entry<String, Node> entry = iterator.next();
			Node node = entry.getValue(); // get the current node of the list.
			GraphNode gnode = new GraphNode(graph, SWT.NONE, node.getName()); // create the graph node.
			gnode.setBackgroundColor(node.getBackgroundColor()); // sets the node background color.
			gnode.setForegroundColor(node.getForegroundColor()); // sets the node text color.
			gnode.setBorderColor(Colors_ID.BLACK); // sets the node border color to black.
			gnode.setHighlightColor(Colors_ID.YELLOW); // sets he highlight color.
			gnode.setBorderHighlightColor(Colors_ID.BLACK); // sets the node border highlight color to black.
			sourcegraph.Node<Integer> sourceNode = sourceGraph.getNode(Integer.parseInt(gnode.getText())); // the correspondent source node.
			gnode.setData(sourceNode); // associate the visualization node with the source node.	
			new sourcegraph.GraphInformation().addInformationToLayer0(sourceGraph, sourceNode, gnode); // associate the the nodes of sourceGraph and layoutGraph.
			graphNodes.add(gnode); // add the node to the list.
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
			while((begin == null || end == null) && index < graphNodes.size()) {
				if(edge.getBeginNode().equals(graphNodes.get(index).getText())) {
					begin = graphNodes.get(index); // assign the graph node to the start of the connection..
				} 
				if(edge.getEndNode().equals(graphNodes.get(index).getText())) {
					end = graphNodes.get(index); // assign the the graph node to the end of the connection.
				}
				index++;	
			}
			GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, begin, end); // create the graph connection between the start and the end nodes.
			connection.setLineColor(edge.getColor()); // sets the edge color.
			sourcegraph.Node<Integer> beginNode = sourceGraph.getNode(Integer.parseInt(begin.getText())); // the correspondent begin node.
			sourcegraph.Node<Integer> endNode = sourceGraph.getNode(Integer.parseInt(end.getText())); // the correspondent final node.
			// verify which node is the correct.
			for(sourcegraph.Edge<Integer> sourceEdge : sourceGraph.getNodeEdges(beginNode)) { // get all edges of the begin node.
				if(sourceEdge.getEndNode().equals(endNode)) { // when the edges matches.
					connection.setData(sourceEdge); // associate the visualization edge with the source edge.
					sourceGraph.addMetadata(sourceEdge, connection); // associate the the edge of sourceGraph and layoutGraph.
				}
			}
			graphEdges.add(connection); // add the edge to the list.
		}
	}
	
	private void setLayout(GraphElements graphElements) {
		graph.setLayoutAlgorithm(new GraphLayout(parent, graphElements).setLayout(), true);
	}
	
	public List<GraphNode> getGraphNodes() {
		return graphNodes; // the list of nodes.
	}
	
	public List<GraphConnection> getGraphEdges() {
		return graphEdges; // the list of connections.
	}

	public void createSelectionListener(int layerNum, ActiveEditor activeEditor) {
		layer = layerNum; // assign the current layer.
		editor = activeEditor;
		event = new SelectionAdapter() { // create a new SelectionAdapter event.
				
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item != null && e.item instanceof GraphNode ) {
					getSelected();
					GraphInformation information = new GraphInformation();
					information.setEditor(editor);
					information.setLayerInformation(layer);
				} else if(e.item == null)
					editor.deleteALLMarkers(); // removes the marks in the editor.
			}
		};	
		graph.addSelectionListener(event); // add the SelectionAdapter to the graph. 
	}
	
	public void removeSelectionListener() {
		graph.removeSelectionListener(event); // remove the SelectionAdapter from the graph.
	}
	
	@SuppressWarnings("unchecked")
	public List<GraphItem> getSelected() {
		return graph.getSelection(); // return the list with the selected nodes.
	}
	
	public void setSelected(GraphItem[] items) {
		graph.setSelection(items); // the items selected.
	}
}