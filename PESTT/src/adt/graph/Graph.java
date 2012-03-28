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

	private Set<Node<V>> nodes;
	private Map<Node<V>, Set<Edge<V>>> edges;
	private Set<Node<V>> initialNodes;
	private Set<Node<V>> finalNodes;
	private List<GraphMetadataLayer> metadataLayers;
	private int currentLayer;

	public Graph() {
		nodes = new TreeSet<Node<V>>();
		edges = new LinkedHashMap<Node<V>, Set<Edge<V>>>();
		initialNodes = new HashSet<Node<V>>();
		finalNodes = new HashSet<Node<V>>();
		metadataLayers = new ArrayList<GraphMetadataLayer>();
	}

	public Node<V> addNode(V value) {
		Node<V> node = new Node<V>(value);
		addNode(node);
		return node;
	}

	public void addNode(Node<V> node) {
		if(!containsNode(node)){
			nodes.add(node);
			edges.put(node, new LinkedHashSet<Edge<V>>());
		}
	}

	public void addInitialNode(Node<V> node) {
		addNode(node);
		initialNodes.add(node);
	}

	public Node<V> addInitialNode(V value) {
		Node<V> node = addNode(value);
		addNode(node);
		initialNodes.add(node);
		return node;
	}
	
	public boolean isInitialNode(Node<V> node) {
		return initialNodes.contains(node);
	}

	public void addFinalNode(Node<V> node) {
		if(!containsNode(node))
			addNode(node);
		finalNodes.add(node);
	}

	public Node<V> addFinalNode(V value) {
		Node<V> node = getNode(value);
		if(node == null)
			addNode(value);
		finalNodes.add(node);
		return node;
	}

	public boolean isFinalNode(Node<V> node) {
		return finalNodes.contains(node);
	}

	public Node<V> getNode(V value){
		for(Node<V> node : nodes) {
			if(node.getValue().equals(value)) 
				return node;
		}
		return null;
	}
	
	public void addEdge(Edge<V> edge) {
		getNodeEdges(edge.getBeginNode()).add(edge);
	}

	public Edge<V> addEdge(Node<V> from, Node<V> to) {
		Edge<V> edge = new Edge<V>(from, to);
		addEdge(edge);
		return edge;
	}

	public Set<Edge<V>> getNodeEdges(Node<V> node) {
		Set<Edge<V>> nodeEdges = edges.get(node);
		if(nodeEdges == null) {
			addNode(node);
			nodeEdges = edges.get(node);
		}
		return nodeEdges;
	}

	public Set<Edge<V>> getNodeEndEdges(Node<V> node) {
		Set<Edge<V>> nodeEndEdges = new HashSet<Edge<V>>();	
		for(Set<Edge<V>> edgesNode : edges.values())
			for (Edge<V> edge : edgesNode)
				if (edge.getEndNode() == node)
					nodeEndEdges.add(edge);
					
		return nodeEndEdges;
	}
	
	public void removeEdge(Edge<V> edge) {
		getNodeEdges(edge.getBeginNode()).remove(edge);
	}

	public void removeNode(Node<V> node) {
		nodes.remove(node);
		initialNodes.remove(node);
		finalNodes.remove(node);
		edges.remove(node);
	}

	public Iterable<Node<V>> getInitialNodes() {
		return initialNodes;
	}
	
	public Iterable<Node<V>> getFinalNodes() {
		return finalNodes;
	}

	public boolean containsNode(Node<V> node) {
		return nodes.contains(node);
	}

	public boolean containsEdge(Edge<V> edge) {
		return getNodeEdges(edge.getBeginNode()).contains(edge);
	}

	public int addMetadataLayer() {
		metadataLayers.add(new GraphMetadataLayer());
		currentLayer = metadataLayers.size() - 1;
		return currentLayer;
	}

	public void selectMetadataLayer(int i) {
		currentLayer = i;
	}

	public void addMetadata(Node<V> node, Object data) {
		getCurrentLayer().addMetadata(node, data);
	}

	public void addMetadata(Edge<V> edge, Object data) {
		getCurrentLayer().addEdgeMetadata(edge, data);
	}

	public Object getMetadata(Node<?> node) {
		return getCurrentLayer().getMetadata(node);
	}

	public Object getMetadata(Edge<?> edge) {
		return getCurrentLayer().getMetadata(edge);
	}

	private GraphMetadataLayer getCurrentLayer() {
		return metadataLayers.get(currentLayer);
	}

	public Iterable<Node<V>> getNodes() {		
		return nodes;
	}
	
	public void sort() {
		Set<Node<V>> sorted = new TreeSet<Node<V>>();
		for(Node<V> node : nodes)
			sorted.add(node);
		nodes.clear();
		nodes = sorted;
	}
	
	public int size() {
		return nodes.size();
	}
	
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

	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitGraph(this);
	}
	
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