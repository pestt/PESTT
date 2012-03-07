package adt.graph;

import java.util.Iterator;
import java.util.List;

public class InfinitePath<V extends Comparable<V>> extends Path<V> implements Iterable<Node<V>> {
	
	private List<Node<V>> nodes;
	
	public InfinitePath(Node<V> node) {
		super(node);
		nodes = super.getPathNodes();
	}
	
	public void addNode(int pos, Node<V> node) {
		nodes.add(pos, node);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder ();
		s.append("[");
		Iterator<Node<V>> it = nodes.iterator();
		s.append(it.next()); // a path has always one node, at least!
		while(it.hasNext()) {
			Node<V> node = it.next();
			if(((Integer) (node.getValue())) == -1)
				s.append(", ...");
			else
				s.append(", " + node);
		}
		s.append("]");
		return s.toString();
	}
}