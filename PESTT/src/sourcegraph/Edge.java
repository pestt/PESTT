package sourcegraph;

import graphvisitors.IGraphVisitor;


public class Edge<N> {

	private Node<N> beginNode;
	private Node<N> endNode;

	public Edge(Node<N> begin, Node<N> end) {
		this.beginNode = begin;
		this.endNode = end;
	}

	public Node<N> getBeginNode() {
		return beginNode;
	}

	public Node<N> getEndNode() {
		return endNode;
	}

	public void accept(IGraphVisitor<N> visitor) {
		visitor.visitEdge(this);
	}

}