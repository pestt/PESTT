package adt.graph;

import java.util.Collection;

public class CyclePath extends Path {

	/**
	 * Create a new CyclePath Object.
	 * 
	 * @param nodes
	 *            - The list of nodes.
	 */
	public CyclePath(Collection<Node> nodes) {
		super(nodes);
	}

}
