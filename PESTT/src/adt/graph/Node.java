package adt.graph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Node implements Comparable<Node> {

	/**
	 * The value of this Node.
	 */
	@XmlAttribute private int value;

	/**
	 * Creates a new Node
	 * 
	 * @param value
	 *            - the value associated to this Node.
	 */
	public Node(int value) {
		this.value = value;
	}

	/**
	 * Gets the value of this Node.
	 * 
	 * @return this Node's value.
	 */
	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	/**
	 * Visits this Node.
	 * 
	 * @param visitor
	 */
	public void accept(IGraphVisitor visitor) {
		visitor.visitNode(this);
	}

	/**
	 * Sets the value of this Node.
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int compareTo(Node o) {
		return value - o.value;
	}
}
