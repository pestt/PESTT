package adt.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import domain.exceptions.NodeNotFoundException;

public class Graph {

	/**
	 * The set of nodes in the Graph.
	 */
	private Set<Node> nodes;

	/**
	 * A map of node edges in the Graph.
	 */
	private Map<Node, Set<Edge>> edges;

	/**
	 * The initial nodes of the Graph.
	 */
	private Set<Node> initialNodes;

	/**
	 * The final nodes of the Graph.
	 */
	private Set<Node> finalNodes;

	/**
	 * The list of Graph meta-data layers.
	 */
	private List<GraphMetadataLayer> metadataLayers;

	/**
	 * The current Graph's layer.
	 */
	private int currentLayer;

	/**
	 * Creates a new Graph Object.
	 */
	public Graph() {
		nodes = new TreeSet<Node>();
		edges = new LinkedHashMap<Node, Set<Edge>>();
		initialNodes = new HashSet<Node>();
		finalNodes = new HashSet<Node>();
		metadataLayers = new ArrayList<GraphMetadataLayer>();
	}

	/**
	 * Creates a Node with the given value and adds it to the Graph.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The new Node.
	 */
	public Node addNode(int value) {
		Node node = new Node(value);
		addNode(node);
		return node;
	}

	/**
	 * Adds a new Node to the Graph.
	 * 
	 * @param Node
	 *            &lt;V&gt; node - The new Node.
	 */
	public void addNode(Node node) {
		if (!containsNode(node)) {
			nodes.add(node);
			edges.put(node, new LinkedHashSet<Edge>());
		}
	}

	/**
	 * Adds a Node to the Graph's initial nodes.
	 * 
	 * @param Node
	 *            &lt;V&gt; - The Node to add to the initial nodes.
	 */
	public void addInitialNode(Node node) {
		addNode(node);
		initialNodes.add(node);
	}

	/**
	 * Creates a Node with the given value and adds it to the Graph's initial
	 * nodes.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The new Node.
	 */
	public Node addInitialNode(int value) {
		Node node = addNode(value);
		addNode(node);
		initialNodes.add(node);
		return node;
	}

	/**
	 * Checks if the Node is an initial Graph Node.
	 * 
	 * @param node
	 *            - The Node to be verified.
	 * @return boolean - True if it is an initial Graph Node or False if not.
	 */
	public boolean isInitialNode(Node node) {
		return initialNodes.contains(node);
	}

	/**
	 * Adds a Node to the Graph final nodes.
	 * 
	 * @param Node
	 *            &lt;V&gt; - The Node to add to the Graph's final nodes.
	 */
	public void addFinalNode(Node node) {
		if (!containsNode(node))
			addNode(node);
		finalNodes.add(node);
	}

	/**
	 * Creates a Node with the given value and add it to the Graph's final
	 * nodes.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The new Node.
	 */
	public Node addFinalNode(int value) {
		Node node = getNode(value);
		if (node == null)
			addNode(value);
		finalNodes.add(node);
		return node;
	}

	/**
	 * Checks if the Node is a final Graph Node.
	 * 
	 * @param node
	 *            - The Node to be verified.
	 * @return boolean - True if it is a final Graph Node or False if not.
	 */
	public boolean isFinalNode(Node node) {
		return finalNodes.contains(node);
	}

	/**
	 * Gets a Graph Node.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The Node with the given value.
	 */
	public Node getNode(int value) {
		for (Node node : nodes) {
			if (node.getValue() == value)
				return node;
		}
		new NodeNotFoundException().printStackTrace();
		return null;
	}

	/**
	 * Adds a new Edge to the Graph.
	 * 
	 * @param edge
	 *            - The Edge to add.
	 */
	public void addEdge(Edge edge) {
		getNodeEdges(edge.getBeginNode()).add(edge);
	}

	/**
	 * Creates a new Edge with the given nodes and adds it to the Graph.
	 * 
	 * @param from
	 *            - The Edge's begin Node.
	 * @param to
	 *            - The Edge's end Node.
	 * @return Edge&lt;V&gt; - The new Edge.
	 */
	public Edge addEdge(Node from, Node to) {
		Edge edge = new Edge(from, to);
		addEdge(edge);
		return edge;
	}

	/**
	 * Gets the set of Edges for the given Node, where the given Node is the
	 * Edge begin Node.
	 * 
	 * @param node
	 *            - The Node to get the edges.
	 * @return Set&lt;Edge&lt;V&gt;&gt; - The set of edges for the Node.
	 */
	public Set<Edge> getNodeEdges(Node node) {
		Set<Edge> nodeEdges = edges.get(node);
		if (nodeEdges == null) {
			addNode(node);
			nodeEdges = edges.get(node);
		}
		return nodeEdges;
	}

	/**
	 * Gets the set of edges for the Node, where the given Node is the Edge end
	 * Node.
	 * 
	 * @param node
	 *            - The Node to get the Edges.
	 * @return Set&lt;Edge&lt;V&gt;&gt; - The set of Edges for the node.
	 */
	public Set<Edge> getNodeEndEdges(Node node) {
		Set<Edge> nodeEndEdges = new HashSet<Edge>();
		for (Set<Edge> edgesNode : edges.values())
			for (Edge edge : edgesNode)
				if (edge.getEndNode() == node)
					nodeEndEdges.add(edge);

		return nodeEndEdges;
	}

	/**
	 * Removes the given Edge.
	 * 
	 * @param edge
	 *            - The Edge to remove.
	 */
	public void removeEdge(Edge edge) {
		getNodeEdges(edge.getBeginNode()).remove(edge);
	}

	/**
	 * Removes the given Node.
	 * 
	 * @param node
	 *            - The Node to remove.
	 */
	public void removeNode(Node node) {
		nodes.remove(node);
		initialNodes.remove(node);
		finalNodes.remove(node);
		edges.remove(node);
	}

	/**
	 * Gets the initial nodes of of the Graph.
	 * 
	 * @return Iterable&lt;Node&lt;V&gt&gt; - The Graph initial nodes.
	 */
	public Iterable<Node> getInitialNodes() {
		return initialNodes;
	}

	/**
	 * Gets the final nodes of of the Graph.
	 * 
	 * @return Iterable&lt;Node&lt;V&gt;&gt; - The Graph final nodes.
	 */
	public Iterable<Node> getFinalNodes() {
		return finalNodes;
	}

	/**
	 * Checks if the Graph contains the given Node.
	 * 
	 * @param node
	 *            - The Node to be verified.
	 * @return boolean - True if Graph contains the Node and False if not.
	 */
	public boolean containsNode(Node node) {
		return nodes.contains(node);
	}

	/**
	 * Checks if the Graph contains the given Edge.
	 * 
	 * @param edge
	 *            - The Edge to be verified.
	 * @return boolean - True if Graph contains the Edge and False if not.
	 */
	public boolean containsEdge(Edge edge) {
		return getNodeEdges(edge.getBeginNode()).contains(edge);
	}

	/**
	 * Adds a new meta-data layer to the Graph.
	 * 
	 * @return int - The layer identifier.
	 */
	public int addMetadataLayer() {
		metadataLayers.add(new GraphMetadataLayer());
		currentLayer = metadataLayers.size() - 1;
		return currentLayer;
	}

	/**
	 * Selects the Graph meta-data layer.
	 * 
	 * @param i
	 *            - The Graph meta-data layer identifier.
	 */
	public void selectMetadataLayer(int i) {
		currentLayer = i;
	}

	/**
	 * Adds data to Node.
	 * 
	 * @param node
	 *            - The Node to add the data to.
	 * @param data
	 *            - The data to be added to the Node.
	 */
	public void addMetadata(Node node, Object data) {
		getCurrentLayer().addMetadata(node, data);
	}

	/**
	 * Adds data to Edge.
	 * 
	 * @param edge
	 *            - The Edge to add the data to.
	 * @param data
	 *            - The data to be added to the Edge.
	 */
	public void addMetadata(Edge edge, Object data) {
		getCurrentLayer().addEdgeMetadata(edge, data);
	}

	/**
	 * Gets the data of the given Node.
	 * 
	 * @param node
	 *            - The Node to get the data from.
	 * @return Object - The data associated to the Node.
	 */
	public Object getMetadata(Node node) {
		return getCurrentLayer().getMetadata(node);
	}

	/**
	 * Gets the data of the given Edge.
	 * 
	 * @param node
	 *            - The Edge to get the data from.
	 * @return Object - The data associated to the Edge.
	 */
	public Object getMetadata(Edge edge) {
		return getCurrentLayer().getMetadata(edge);
	}

	/**
	 * Gets the current Graph meta-data layer.
	 * 
	 * @return GraphMetadataLayer - The Graph's current layer.
	 */
	private GraphMetadataLayer getCurrentLayer() {
		return metadataLayers.get(currentLayer);
	}

	/**
	 * The Graph nodes.
	 * 
	 * @return Iterable&lt;Node&lt;V&gt;&gt; - The Graph nodes.
	 */
	public Iterable<Node> getNodes() {
		return nodes;
	}

	/**
	 * Sort the Graph nodes.
	 */
	public void sortNodes() {
		Set<Node> sorted = new TreeSet<Node>();
		for (Node node : nodes)
			sorted.add(node);
		nodes.clear();
		nodes = sorted;
	}

	/**
	 * The size of the Graph.
	 * 
	 * @return int - The Graph's number of nodes.
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * Checks if the given Path is a Path of the Graph.
	 * 
	 * @param path
	 *            - The Path to be verified.
	 * @return boolean - True if the Path is a Path of Graph and False if not.
	 */
	public boolean isPath(Path path) {
		Iterator<Node> iterator = path.iterator();
		if (!iterator.hasNext())
			return true;
		Node previousNode = iterator.next();
		if (!containsNode(previousNode))
			return false;
		while (iterator.hasNext()) {
			Node currentNode = iterator.next();
			Iterator<Edge> edgeIterator = getNodeEdges(previousNode)
					.iterator();
			boolean found = false;
			while (edgeIterator.hasNext() && !found) {
				if (edgeIterator.next().getEndNode() == currentNode)
					found = true;
			}
			if (!found)
				return false;
			previousNode = currentNode;
		}
		return true;
	}

	/**
	 * Visits the Graph.
	 * 
	 * @param visitor
	 *            - The visitor to apply.
	 */
	public void accept(IGraphVisitor visitor) {
		visitor.visitGraph(this);
	}

	/**
	 * The Graph representation.
	 * 
	 * @return String - The Graph representation.
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		Iterator<Node> it = nodes.iterator();
		if (nodes.size() >= 1) {
			s.append(it.next()); // a path has always one node, at least!
			while (it.hasNext())
				s.append(" - " + it.next());
		}
		s.append("]");
		return s.toString();
	}
}