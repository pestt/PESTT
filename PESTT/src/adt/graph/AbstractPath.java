package adt.graph;

import java.util.Iterator;



public abstract class AbstractPath<V extends Comparable<V>> implements Iterable<Node<V>>, Comparable<AbstractPath<V>> {

	public abstract boolean isSubPath(AbstractPath<V> path);
	public abstract boolean toursWithSideTrip(AbstractPath<V> path);
	public abstract boolean toursWithDetour(AbstractPath<V> path);

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
	
	public Node<V> from() {
		return this.iterator().next();
	}
	
	public Node<V> to() {
		Node<V> lastNode = null;
		for(Node<V> node : this) 
			lastNode = node;
		return lastNode;
	}
	
	public boolean containsNode(Node<V> node) {
		for(Node<V> pathNode : this)
			if(pathNode == node)
				return true;
		return false;
	}
	
	public boolean isEdgeOfPath(Edge<V> edge) {
		Iterator<Node<V>> iterator = this.iterator();
		while(iterator.hasNext()) {
			Node<V> current = iterator.next();
			if(edge.getBeginNode() == current) {
				current = iterator.next();
				if(edge.getEndNode() == current)
					return true;
			}
		}
		return false;
	}
}