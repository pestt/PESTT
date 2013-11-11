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

import domain.graph.visitors.IGraphVisitor;

public class Graph<V extends Comparable<V>> {

	/**
	 * The set of nodes in the Graph.
	 */
	private Set<Node<V>> nodes;

	/**
	 * A map of node edges in the Graph.
	 */
	private Map<Node<V>, Set<Edge<V>>> edges;

	/**
	 * The initial nodes of the Graph.
	 */
	private Set<Node<V>> initialNodes;

	/**
	 * The final nodes of the Graph.
	 */
	private Set<Node<V>> finalNodes;

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
		nodes = new TreeSet<Node<V>>();
		edges = new LinkedHashMap<Node<V>, Set<Edge<V>>>();
		initialNodes = new HashSet<Node<V>>();
		finalNodes = new HashSet<Node<V>>();
		metadataLayers = new ArrayList<GraphMetadataLayer>();
	}

	/**
	 * Creates a Node with the given value and adds it to the Graph.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The new Node.
	 */
	public Node<V> addNode(V value) {
		Node<V> node = new Node<V>(value);
		addNode(node);
		return node;
	}

	/**
	 * Adds a new Node to the Graph.
	 * 
	 * @param Node&lt;V&gt; node - The new Node.
	 */
	public void addNode(Node<V> node) {
		if (!containsNode(node)) {
			nodes.add(node);
			edges.put(node, new LinkedHashSet<Edge<V>>());
		}
	}

	/**
	 * Adds a Node to the Graph's initial nodes.
	 * 
	 * @param Node&lt;V&gt; - The Node to add to the initial nodes.
	 */
	public void addInitialNode(Node<V> node) {
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
	public Node<V> addInitialNode(V value) {
		Node<V> node = addNode(value);
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
	public boolean isInitialNode(Node<V> node) {
		return initialNodes.contains(node);
	}

	/**
	 * Adds a Node to the Graph final nodes.
	 * 
	 * @param Node&lt;V&gt; - The Node to add to the Graph's final nodes.
	 */
	public void addFinalNode(Node<V> node) {
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
	public Node<V> addFinalNode(V value) {
		Node<V> node = getNode(value);
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
	public boolean isFinalNode(Node<V> node) {
		return finalNodes.contains(node);
	}

	/**
	 * Gets a Graph Node.
	 * 
	 * @param value
	 *            - The value of the Node.
	 * @return Node&lt;V&gt; - The Node with the given value.
	 */
	public Node<V> getNode(V value) {
		for (Node<V> node : nodes) {
			if (node.getValue().equals(value))
				return node;
		}
		return null;
	}

	/**
	 * Adds a new Edge to the Graph.
	 * 
	 * @param edge
	 *            - The Edge to add.
	 */
	public void addEdge(Edge<V> edge) {
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
	public Edge<V> addEdge(Node<V> from, Node<V> to) {
		Edge<V> edge = new Edge<V>(from, to);
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
	public Set<Edge<V>> getNodeEdges(Node<V> node) {
		Set<Edge<V>> nodeEdges = edges.get(node);
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
	public Set<Edge<V>> getNodeEndEdges(Node<V> node) {
		Set<Edge<V>> nodeEndEdges = new HashSet<Edge<V>>();
		for (Set<Edge<V>> edgesNode : edges.values())
			for (Edge<V> edge : edgesNode)
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
	public void removeEdge(Edge<V> edge) {
		getNodeEdges(edge.getBeginNode()).remove(edge);
	}

	/**
	 * Removes the given Node.
	 * 
	 * @param node
	 *            - The Node to remove.
	 */
	public void removeNode(Node<V> node) {
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
	public Iterable<Node<V>> getInitialNodes() {
		return initialNodes;
	}

	/**
	 * Gets the final nodes of of the Graph.
	 * 
	 * @return Iterable&lt;Node&lt;V&gt;&gt; - The Graph final nodes.
	 */
	public Iterable<Node<V>> getFinalNodes() {
		return finalNodes;
	}

	/**
	 * Checks if the Graph contains the given Node.
	 * 
	 * @param node
	 *            - The Node to be verified.
	 * @return boolean - True if Graph contains the Node and False if not.
	 */
	public boolean containsNode(Node<V> node) {
		return nodes.contains(node);
	}

	/**
	 * Checks if the Graph contains the given Edge.
	 * 
	 * @param edge
	 *            - The Edge to be verified.
	 * @return boolean - True if Graph contains the Edge and False if not.
	 */
	public boolean containsEdge(Edge<V> edge) {
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
	public void addMetadata(Node<V> node, Object data) {
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
	public void addMetadata(Edge<V> edge, Object data) {
		getCurrentLayer().addEdgeMetadata(edge, data);
	}

	/**
	 * Gets the data of the given Node.
	 * 
	 * @param node
	 *            - The Node to get the data from.
	 * @return Object - The data associated to the Node.
	 */
	public Object getMetadata(Node<?> node) {
		return getCurrentLayer().getMetadata(node);
	}

	/**
	 * Gets the data of the given Edge.
	 * 
	 * @param node
	 *            - The Edge to get the data from.
	 * @return Object - The data associated to the Edge.
	 */
	public Object getMetadata(Edge<?> edge) {
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
	public Iterable<Node<V>> getNodes() {
		return nodes;
	}

	/**
	 * Sort the Graph nodes.
	 */
	public void sortNodes() {
		Set<Node<V>> sorted = new TreeSet<Node<V>>();
		for (Node<V> node : nodes)
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
	public boolean isPath(Path<V> path) {
		Iterator<Node<V>> iterator = path.iterator();
		if (!iterator.hasNext())
			return true;
		Node<V> previousNode = iterator.next();
		if (!containsNode(previousNode))
			return false;
		while (iterator.hasNext()) {
			Node<V> currentNode = iterator.next();
			Iterator<Edge<V>> edgeIterator = getNodeEdges(previousNode)
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
	public void accept(IGraphVisitor<V> visitor) {
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
		Iterator<Node<V>> it = nodes.iterator();
		if (nodes.size() >= 1) {
			s.append(it.next()); // a path has always one node, at least!
			while (it.hasNext())
				s.append(" - " + it.next());
		}
		s.append("]");
		return s.toString();
	}
}