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
	 * The current Graph layer. 
	 */
	private int currentLayer;

	/**
	 * Create a new Graph Object.
	 */
	public Graph() {
		nodes = new TreeSet<Node<V>>();
		edges = new LinkedHashMap<Node<V>, Set<Edge<V>>>();
		initialNodes = new HashSet<Node<V>>();
		finalNodes = new HashSet<Node<V>>();
		metadataLayers = new ArrayList<GraphMetadataLayer>();
	}

	/**
	 * Create a Node with the given value and 
	 * add it to the Graph.
	 * 
	 * @param value - The value of the Node.
	 * @return Node<V> - The new Node.
	 */
	public Node<V> addNode(V value) {
		Node<V> node = new Node<V>(value);
		addNode(node);
		return node;
	}

	/**
	 * Add a new Node to the Graph.
	 * 
	 * @param Node<V> - The new Node.
	 */
	public void addNode(Node<V> node) {
		if(!containsNode(node)){
			nodes.add(node);
			edges.put(node, new LinkedHashSet<Edge<V>>());
		}
	}

	/**
	 * Add a Node to the Graph initial nodes.
	 * 
	 * @param Node<V> - The Node to add to the initial nodes.
	 */
	public void addInitialNode(Node<V> node) {
		addNode(node);
		initialNodes.add(node);
	}

	/**
	 * Create a Node with the given value and 
	 * add it to the Graph initial nodes.
	 * 
	 * @param value - The value of the Node.
	 * @return Node<V> - The new Node.
	 */
	public Node<V> addInitialNode(V value) {
		Node<V> node = addNode(value);
		addNode(node);
		initialNodes.add(node);
		return node;
	}
	
	/**
	 * Checks if the Node is a initial Graph Node.
	 * 
	 * @param node - The Node to verified.
	 * @return boolean - True if is a initial Graph Node or False if not.
	 */
	public boolean isInitialNode(Node<V> node) {
		return initialNodes.contains(node);
	}

	/**
	 * Add a Node to the Graph final nodes.
	 * 
	 * @param Node<V> - The Node to add to the final nodes.
	 */
	public void addFinalNode(Node<V> node) {
		if(!containsNode(node))
			addNode(node);
		finalNodes.add(node);
	}

	/**
	 * Create a Node with the given value and 
	 * add it to the Graph final nodes.
	 * 
	 * @param value - The value of the Node.
	 * @return Node<V> - The new Node.
	 */
	public Node<V> addFinalNode(V value) {
		Node<V> node = getNode(value);
		if(node == null)
			addNode(value);
		finalNodes.add(node);
		return node;
	}

	/**
	 * Checks if the Node is a final Graph Node.
	 * 
	 * @param node - The Node to verified.
	 * @return boolean - True if is a final Graph Node or False if not.
	 */
	public boolean isFinalNode(Node<V> node) {
		return finalNodes.contains(node);
	}

	/**
	 * Get a Graph Node.
	 * 
	 * @param value - The value of the Node.
	 * @return Node<V> - The Node with the given value.
	 */
	public Node<V> getNode(V value){
		for(Node<V> node : nodes) {
			if(node.getValue().equals(value)) 
				return node;
		}
		return null;
	}
	
	/**
	 * Add a new Edge to the Graph.
	 * 
	 * @param edge - The Edge to add.
	 */
	public void addEdge(Edge<V> edge) {
		getNodeEdges(edge.getBeginNode()).add(edge);
	}
	
	/**
	 * Create a new Edge with the given nodes and 
	 * add it to the Graph. 
	 *  
	 * @param from - The Edge begin Node.
	 * @param to - The Edge end Node.
	 * @return Edge<V> - The new Edge.
	 */
	public Edge<V> addEdge(Node<V> from, Node<V> to) {
		Edge<V> edge = new Edge<V>(from, to);
		addEdge(edge);
		return edge;
	}

	/**
	 * Get the set of Edges for the given Node.
	 * Where the given Node is the Edge begin Node.
	 * 
	 * @param node - The Node to get the edges.
	 * @return Set<Edge<V>> - The set of edges for the Node.
	 */
	public Set<Edge<V>> getNodeEdges(Node<V> node) {
		Set<Edge<V>> nodeEdges = edges.get(node);
		if(nodeEdges == null) {
			addNode(node);
			nodeEdges = edges.get(node);
		}
		return nodeEdges;
	}

	/**
	 * Get the set of edges for the Node.
	 * Where the given Node is the Edge end Node.
	 * 
	 * @param node - The Node to get the Edges.
	 * @return Set<Edge<V>> - The set of Edges for the node.
	 */
	public Set<Edge<V>> getNodeEndEdges(Node<V> node) {
		Set<Edge<V>> nodeEndEdges = new HashSet<Edge<V>>();	
		for(Set<Edge<V>> edgesNode : edges.values())
			for (Edge<V> edge : edgesNode)
				if (edge.getEndNode() == node)
					nodeEndEdges.add(edge);
					
		return nodeEndEdges;
	}
	
	/**
	 * Remove the given Edge.
	 * 
	 * @param edge - The Edge to remove.
	 */
	public void removeEdge(Edge<V> edge) {
		getNodeEdges(edge.getBeginNode()).remove(edge);
	}

	/**
	 * Remove the given Node.
	 * 
	 * @param node - The Node to remove.
	 */
	public void removeNode(Node<V> node) {
		nodes.remove(node);
		initialNodes.remove(node);
		finalNodes.remove(node);
		edges.remove(node);
	}

	/**
	 * Get the initial nodes of of the Graph.
	 * 
	 * @return Iterable<Node<V>> - The Graph initial nodes.
	 */
	public Iterable<Node<V>> getInitialNodes() {
		return initialNodes;
	}
	
	/**
	 * Get the final nodes of of the Graph.
	 * 
	 * @return Iterable<Node<V>> - The Graph final nodes.
	 */
	public Iterable<Node<V>> getFinalNodes() {
		return finalNodes;
	}

	/**
	 * Checks if the Graph contains the given Node.
	 * 
	 * @param node - The Node to be verified.
	 * @return boolean - True if Graph contains he Node and False if not.
	 */
	public boolean containsNode(Node<V> node) {
		return nodes.contains(node);
	}

	/**
	 * Checks if the Graph contains the given Edge.
	 * 
	 * @param edge - The Edge to be verified.
	 * @return boolean - True if Graph contains he Edge and False if not.
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
	 * Select the Graph meta-data layer.
	 * 
	 * @param i - The Graph meta-data layer idetifier.
	 */
	public void selectMetadataLayer(int i) {
		currentLayer = i;
	}

	/**
	 * Adds data to Node.
	 * 
	 * @param node - The Node to add the data.
	 * @param data - The data to be added to the Node.
	 */
	public void addMetadata(Node<V> node, Object data) {
		getCurrentLayer().addMetadata(node, data);
	}

	/**
	 * Adds data to Edge.
	 * 
	 * @param edge - The Edge to add the data.
	 * @param data - The data to be added to the Edge.
	 */
	public void addMetadata(Edge<V> edge, Object data) {
		getCurrentLayer().addEdgeMetadata(edge, data);
	}

	/**
	 * Get the data to the given Node.
	 * 
	 * @param node - The Node to get the data.
	 * @return Object - The data associated to the Node.
	 */
	public Object getMetadata(Node<?> node) {
		return getCurrentLayer().getMetadata(node);
	}

	/**
	 * Get the data to the given Edge.
	 * 
	 * @param node - The Edge to get the data.
	 * @return Object - The data associated to the Edge.
	 */
	public Object getMetadata(Edge<?> edge) {
		return getCurrentLayer().getMetadata(edge);
	}

	/**
	 * Get the current Graph meta-data layer.
	 * 
	 * @return GraphMetadataLayer - The Graph current layer.
	 */
	private GraphMetadataLayer getCurrentLayer() {
		return metadataLayers.get(currentLayer);
	}

	/**
	 * The Graph nodes.
	 * 
	 * @return Iterable<Node<V>> - The Graph nodes.
	 */
	public Iterable<Node<V>> getNodes() {		
		return nodes;
	}
	
	/**
	 * Sort the Graph nodes.
	 */
	public void sortNodes() {
		Set<Node<V>> sorted = new TreeSet<Node<V>>();
		for(Node<V> node : nodes)
			sorted.add(node);
		nodes.clear();
		nodes = sorted;
	}
	
	/**
	 * The size of the Graph.
	 * 
	 * @return int - The Graph number of nodes.
	 */
	public int size() {
		return nodes.size();
	}
	
	/**
	 * Checks if the given Path is a Path of the Graph.
	 * 
	 * @param path - The Path to be verified.
	 * @return boolean - True if the Path is a Path of Graph and False if not.
	 */
	public boolean isPath(Path<V> path) {
		Iterator<Node<V>> iterator = path.iterator(); 			
		if(!iterator.hasNext()) 
			return true;
		Node<V> previousNode = iterator.next();
		if(!containsNode(previousNode))
			return false;
		while(iterator.hasNext()) {
			Node<V> currentNode = iterator.next();
			Iterator<Edge<V>> edgeIterator = getNodeEdges(previousNode).iterator();
			boolean found = false;
			while(edgeIterator.hasNext() && !found) {
				if(edgeIterator.next().getEndNode() == currentNode)
					found = true;
			}
			if(!found)
				return false;
			previousNode = currentNode;
		}
		return true;
	}

	/**
	 * Visit the Graph.
	 * 
	 * @param visitor - The visitor to apply.
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
		StringBuilder s = new StringBuilder ();
		s.append("[");
		Iterator<Node<V>> it = nodes.iterator();
		if(nodes.size() >= 1) {
			s.append(it.next()); // a path has always one node, at least!
			while(it.hasNext())
				s.append(" - " + it.next());
		}
		s.append("]");
		return s.toString();
	}
}