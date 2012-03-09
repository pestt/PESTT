package adt.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Path<V extends Comparable<V>> implements Iterable<Node<V>>, Comparable<Path<V>>, Cloneable {

	private List<Node<V>> nodes;

	public Path(Node<V> node) {
		nodes = new LinkedList<Node<V>>();
		nodes.add(node);
	}
	
	public Path(Collection<Node<V>> nodes) {
		this.nodes = new LinkedList<Node<V>>(nodes);
	}

	public void addNode(Node<V> node) {
		nodes.add(node);
	}

	@Override
	public Iterator<Node<V>> iterator() {
		return nodes.iterator();
	}

	public List<Node<V>> getPathNodes() {
		return nodes;
	}

	public boolean containsNode(Node<V> node) {
		return nodes.contains(node);
	}

	public boolean isSubPath(Path<V> path) {
		boolean match = true;
		if(nodes.size() < path.getPathNodes().size())
			return false;

		for(Node<V> node : path.getPathNodes())
			if(!containsNode(node))
				return false;

		Node<V> first = path.getPathNodes().get(0);
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < nodes.size(); i++) {
			Node<V> node = nodes.get(i);
			if(node == first && i + path.getPathNodes().size() - 1 < nodes.size())
				indexes.add(i);
		}

		for(int index : indexes) {
			for (int i = index, j = 0; i < i + path.getPathNodes().size() && j < path.getPathNodes().size(); i++, j++) {
				if(nodes.get(i) != path.getPathNodes().get(j)) {
					match = false;
					break;
				}
			}
			if(match)
				return match;
			else
				match = true;
		}
		return false;
	}
	
	public abstract boolean toursWithSideTrip(Path<V> path);
		

	public abstract boolean toursWithDeTour(Path<V> path);
	
	@SuppressWarnings("unchecked")
	@Override 
	public Object clone() throws CloneNotSupportedException {
		Path<V> newPath = (Path<V>) super.clone();
		newPath = this;
		return newPath;
	}

	@Override
	public int compareTo(Path<V> other) {
		Iterator<Node<V>> iterator = other.iterator();
		for(Node<V> node : nodes) 
			if(iterator.hasNext()) {
				Node<V> otherNode = iterator.next();
				if(node.compareTo(otherNode) != 0)
					return node.compareTo(otherNode);
			} else 
				return 1;
		return iterator.hasNext() ? -1 : 0;
	}
}