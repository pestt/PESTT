package adt.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SequencePath<V extends Comparable<V>> extends Path<V> implements Iterable<Node<V>> {
	
	private List<Node<V>> nodes;
	
	public SequencePath(Node<V> node) {
		super(node);
		nodes = super.getPathNodes();
	}
	
	public void addNode(int pos, Node<V> node) {
		nodes.add(pos, node);
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