package adt.graph;

import java.util.Collection;

public class CyclePath<V extends Comparable<V>> extends SimplePath<V> {

	public CyclePath(Collection<Node<V>> nodes) {
		super(nodes);
	}

}
