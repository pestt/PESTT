package adt.graph;

import domain.graph.visitors.IGraphVisitor;

public class Node<V extends Comparable<V>> implements Comparable<Node<V>> {

	/**
	 * The value of this Node.
	 */
	private V value;

	/**
	 * Creates a new Node
	 * 
	 * @param value
	 *            - the value associated to this Node.
	 */
	public Node(V value) {
		this.value = value;
	}

	/**
	 * Gets the value of this Node.
	 * 
	 * @return this Node's value.
	 */
	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * Visits this Node.
	 * 
	 * @param visitor
	 */
	public void accept(IGraphVisitor<V> visitor) {
		visitor.visitNode(this);
	}

	/**
	 * Sets the value of this Node.
	 * 
	 * @param value
	 */
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int compareTo(Node<V> o) {
		return value.compareTo(o.value);
	}
}
