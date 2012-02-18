package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;

import java.util.LinkedList;
import java.util.List;

import sourcegraph.Node;
import sourcegraph.Path;

public class NodeCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private List<Path<V>> paths;
	private List<Node<V>> visitedNodes;
	
	public NodeCoverage() {
		visitedNodes = new LinkedList<Node<V>>();
		paths = new LinkedList<Path<V>>();
	}
	
	@Override
	public void endVisit(Node<V> node) {
		if(!visitedNodes.contains(node)) { // set the id to all paths in the list.
			visitedNodes.add(node); // add the node to be visited.
			Path<V> path = new Path<V>(node); // create a new path with the first node.
			paths.add(path); // add path to list.
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Path<V>> getTestRequirements() {
		paths = new SortPaths().sort(paths); // sort the paths.
		return paths;
	}
}