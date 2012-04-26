package adt.graph;

import domain.graph.visitors.IGraphVisitor;

public class Node<V extends Comparable<V>> implements Comparable<Node<V>> {

	private V value;

	public Node(V value) {
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitNode(this);
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int compareTo(Node<V> o) {
		return value.compareTo(o.value);
	}
}
