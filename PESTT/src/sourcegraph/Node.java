package sourcegraph;

import graphvisitors.IGraphVisitor;

public class Node<V> {

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
}
