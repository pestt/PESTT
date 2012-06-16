package adt.graph;

import domain.graph.visitors.IGraphVisitor;

public class Edge<V extends Comparable<V>> {

	/**
	 * The Edge begin Node.
	 */
	private Node<V> beginNode;
	
	/**
	 * The Edge end Node.
	 */
	private Node<V> endNode;

	/**
	 * Create a new Edge Object.
	 * 
	 * @param begin - The begin Node of the Edge.
	 * @param end - The end Node of the Edge.
	 */
	public Edge(Node<V> begin, Node<V> end) {
		this.beginNode = begin;
		this.endNode = end;
	}

	/**
	 * Get the begin Node of the Edge.
	 * 
	 * @return Node<V> - The begin Node of the Edge.
	 */
	public Node<V> getBeginNode() {
		return beginNode;
	}

	/**
	 * Get the end Node of the Edge.
	 * 
	 * @return Node<V> - The end Node of the Edge.
	 */
	public Node<V> getEndNode() {
		return endNode;
	}

	/**
	 * Visit the Edge in the Graph.
	 * 
	 * @param visitor - The visitor to apply.
	 */
	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitEdge(this);
	}
	
	/***
	 * 
	 * @param o - The other Edge;
	 * @return int - 0 If the two edges are equal.
	 * 				 1 If this Edge is bigger than the other.
	 * 			    -1 If this Edge is lesser than the other. 
	 */
	public int compareTo(Edge<V> o) {
		if(beginNode.compareTo(o.getBeginNode()) == 0)
			return endNode.compareTo(o.getEndNode());
		else
			return beginNode.compareTo(o.getBeginNode());
	}
	
	/**
	 * The Edge representation.
	 * 
	 * @return String - The Edge representation.
	 */
	@Override
	public String toString() {
		return "(" + beginNode + ", " + endNode + ")";
	}
}