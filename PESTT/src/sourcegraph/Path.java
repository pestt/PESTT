package sourcegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path<V> implements Iterable<Node<V>> {
	
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
			for(int i = index, j = 0; i < i + path.getPathNodes().size() && j < path.getPathNodes().size(); i++, j++) {
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

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder ();
		s.append("[");
		Iterator<Node<V>> it = nodes.iterator();
		s.append(it.next()); // a path has always one node, at least!
		while (it.hasNext())
			s.append(", " + it.next());
		s.append("]");
		return s.toString();
	}
}