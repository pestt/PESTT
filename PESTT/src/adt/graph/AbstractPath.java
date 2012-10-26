package adt.graph;

import java.util.Iterator;

public abstract class AbstractPath<V extends Comparable<V>> implements Iterable<Node<V>>, Comparable<AbstractPath<V>> {

	/**
	 * Verifies if a path is sub-path of the current path.
	 * 
	 * @param path - The path to verify.
	 * @return boolean - True if the path is sub-path of the current path or False if not.
	 */
	public abstract boolean isSubPath(AbstractPath<V> path);
	
	/**
	 * Verifies if the current path tours path with Sidetrip tour visit.
	 * 
	 * @param path - The path to verify.
	 * @return boolean - True if current path tours path with Sidetrip tour visit or False If not.
	 */
	public abstract boolean toursWithSideTrip(AbstractPath<V> path);
	
	/**
	 * Verifies if the current path tours path with Detour tour visit.
	 * 
	 * @param path - The path to verify.
	 * @return boolean - True if current path tours path with Detour tour visit or False jf not.
	 */
	public abstract boolean toursWithDetour(AbstractPath<V> path);
	
	/***
	 * Compare two paths.
	 * 
	 * @param other - The other path.
	 * @return boolean - 1 if current path have more nodes than the other.
	 *        			-1 if other have more nodes than the current path.
	 *         			 0 if they are equal.
	 *        			-1 or 1 if the current node of current path is greater or 
	 *        					lesser than the current node of other path.  
	 */
	@Override
	public int compareTo(AbstractPath<V> other) {
		Iterator<Node<V>> iterator = other.iterator();
		for(Node<V> node : this) 
			if(iterator.hasNext()) {
				Node<V> otherNode = iterator.next();
				if(node.compareTo(otherNode) != 0)
					return node.compareTo(otherNode);
			} else 
				return 1;
		return iterator.hasNext() ? -1 : 0;
	}
		
	/**
	 * The first node of he current path.
	 * 
	 * @return Node<V> - The first node of the current path.
	 */
	public Node<V> from() {
		return this.iterator().next();
	}
	
	/**
	 * The last node of current path.
	 * 
	 * @return Node<V> - The last node of the current path.
	 */
	public Node<V> to() {
		Node<V> lastNode = null;
		for(Node<V> node : this) 
			lastNode = node;
		return lastNode;
	}
	
	/**
	 * Verifies if the current path contains the given node.
	 * 
	 * @param node - The node to check.
	 * @return boolean - True if the path contains the node or False if not.
	 */
	public boolean containsNode(Node<V> node) {
		for(Node<V> pathNode : this)
			if(pathNode == node)
				return true;
		return false;
	}
	
	/**
	 * Verifies if the current path contains the given edge.
	 * 
	 * @param edge - The edge to check.
	 * @return boolean - True if the path contains the edge or False if not.
	 */
	public boolean isEdgeOfPath(Edge<V> edge) {
		Iterator<Node<V>> iterator = this.iterator();
		while(iterator.hasNext()) {
			Node<V> current = iterator.next();
			if(edge.getBeginNode() == current && current != to()) {
				current = iterator.next();
				if(edge.getEndNode() == current)
					return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return "rui";
	}
}