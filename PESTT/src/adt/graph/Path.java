package adt.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path<V extends Comparable<V>> implements Iterable<Node<V>>, Comparable<Path<V>> {

	private List<Node<V>> nodes;

	public Path(Node<V> node) {
		nodes = new LinkedList<Node<V>>();
		nodes.add(node);
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

	public Path<V> clone() {
		Path<V> copy = null;
		for(Node<V> node : nodes)
			if(copy == null)
				copy = new Path<V>(node);
			else
				copy.addNode(node);
		return copy;
	}

	public boolean toursWithSideTrip(Path<V> path) {
		if(containsLoop())
			if(containsAllNodesInOrder(path))
				return getSidetrips(path);
			else if(isSubPath(path))
				return true;
		return false;
	}

	public boolean toursWithDeTour(Path<V> path) {
		return containsAllNodesInOrder(path);
	}

	private Path<V> createPath(List<Integer> indexes) {
		Path<V> path = null;
		for(int i = 0; i < nodes.size(); i++)
			if(i <= indexes.get(0) || i >= indexes.get(1) + 1)
				if(path == null)
					path = new Path<V>(nodes.get(i));
				else
					path.addNode(nodes.get(i));
		return path;
	}

	private boolean getSidetrips(Path<V> path) {
		for(Node<V> node : path)
			if(isLoopNode(node)) {
				List<Integer> indexes = getLoopIndexes(node);
				Path<V> fakePath = createPath(indexes);
				if(fakePath.isSubPath(path))
					return true;
			}
		return false;
	}

	private boolean containsLoop() {
		for(Node<V> node : getPathNodes())
			if(getNumberOfOccurrences(node) >= 2)
				return true;
		return false;
	}

	private boolean isLoopNode(Node<V> node) {
		if(getNumberOfOccurrences(node) >= 2)
			return true;
		return false;
	}

	private List<Integer> getLoopIndexes(Node<V> node) {
		List<Integer> indexes = new LinkedList<Integer>();
		int last = -1;
		for(int i = 0; i < getPathNodes().size(); i++) {
			Node<V> n = getPathNodes().get(i);
			if(n == node)
				if(indexes.isEmpty())
					indexes.add(i);
				else
					last = i;
		}
		indexes.add(last);
		return indexes;
	}

	private int getNumberOfOccurrences(Node<V> node) {
		int occurrences = 0;
		for(Node<V> n : getPathNodes())
			if(n == node)
				occurrences++;
		return occurrences;
	}

	private boolean containsAllNodesInOrder(Path<V> path) {
		int index = 0;
		for(Node<V> node : path.getPathNodes())
			if(!containsNode(node))
				return false;
			else {
				List<Node<V>> executedPathNodes = getPathNodes();
				while(index < executedPathNodes.size() && executedPathNodes.get(index) != node)
					index++;
				if(index == executedPathNodes.size())
					return false;
			}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		Iterator<Node<V>> it = nodes.iterator();
		s.append(it.next()); // a path has always one node, at least!
		while(it.hasNext())
			s.append(", " + it.next());
		s.append("]");
		return s.toString();
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