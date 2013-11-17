package adt.graph;

import java.util.Collection;

public class CyclePath<V extends Comparable<V>> extends Path<V> {

	/**
	 * Create a new CyclePath Object.
	 * 
	 * @param nodes
	 *            - The list of nodes.
	 */
	public CyclePath(Collection<Node<V>> nodes) {
		super(nodes);
	}

}
