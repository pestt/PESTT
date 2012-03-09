package adt.graph;

import java.util.Collection;
import java.util.Iterator;

public class SimplePath<V extends Comparable<V>> extends Path<V> {
	
	public SimplePath(Node<V> node) {
		super(node);
	}
	
	public SimplePath(Collection<Node<V>> nodes) {
		super(nodes);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		Iterator<Node<V>> it = super.iterator();
		s.append(it.next()); // a path has always one node, at least!
		while(it.hasNext())
			s.append(", " + it.next());
		s.append("]");
		return s.toString();
	}
	
	@Override
	public boolean toursWithSideTrip(Path<V> path) {
		return false;
	}
	
	@Override
	public boolean toursWithDeTour(Path<V> path) {
		return false;
	}
}
