package adt.graph;

import domain.graph.visitors.IGraphVisitor;

public class Edge<V extends Comparable<V>> {

	/**
	 * The Edge's begin Node.
	 */
	private Node<V> beginNode;

	/**
	 * The Edge's end Node.
	 */
	private Node<V> endNode;

	/**
	 * Creates a new Edge Object.
	 * 
	 * @param begin
	 *            - The begin Node of the Edge.
	 * @param end
	 *            - The end Node of the Edge.
	 */
	public Edge(Node<V> begin, Node<V> end) {
		this.beginNode = begin;
		this.endNode = end;
	}

	/**
	 * Gets the begin Node of the Edge.
	 * 
	 * @return Node&lt;V&gt; - The begin Node of the Edge.
	 */
	public Node<V> getBeginNode() {
		return beginNode;
	}

	/**
	 * Gets the end Node of the Edge.
	 * 
	 * @return Node&lt;V&gt; - The end Node of the Edge.
	 */
	public Node<V> getEndNode() {
		return endNode;
	}

	/**
	 * Visits the Edge in the Graph.
	 * 
	 * @param visitor
	 *            - The visitor to apply.
	 */
	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitEdge(this);
	}

	/***
	 * Compares two Edges.
	 * 
	 * @param o
	 *            - The other Edge;
	 *         <ul>
	 * @return <li>0 If the two edges are equal.</li> <li>1 If this Edge is
	 *         bigger than the other.</li> <li>-1 If this Edge is smaller than
	 *         the other.</li>
	 *         </ul>
	 */
	public int compareTo(Edge<V> o) {
		if (beginNode.compareTo(o.getBeginNode()) == 0)
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