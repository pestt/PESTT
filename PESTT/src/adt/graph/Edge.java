package adt.graph;

import domain.graph.visitors.IGraphVisitor;


public class Edge<V extends Comparable<V>> {

	private Node<V> beginNode;
	private Node<V> endNode;

	public Edge(Node<V> begin, Node<V> end) {
		this.beginNode = begin;
		this.endNode = end;
	}

	public Node<V> getBeginNode() {
		return beginNode;
	}

	public Node<V> getEndNode() {
		return endNode;
	}

	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitEdge(this);
	}
	
	@Override
	public String toString() {
		return "(" + beginNode + ", " + endNode + ")";
	}

}