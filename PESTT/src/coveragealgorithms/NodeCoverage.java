package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;
import java.util.ArrayList;
import java.util.LinkedList;
import sourcegraph.Node;
import sourcegraph.Path;

public class NodeCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private ArrayList<Path<V>> paths;
	private LinkedList<Node<V>> visitedNodes = new LinkedList<Node<V>>();
	
	public NodeCoverage() {
		visitedNodes = new LinkedList<Node<V>>();
		paths = new ArrayList<Path<V>>();
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
	public ArrayList<Path<V>> getTestRequirements() {
		paths = new SortPaths().sort(paths); // sort the paths.
		setIds(); // set the id of the path.
		return paths;
	}
	
	private void setIds() {
		int id = 0;
		for(Path<V> path : paths) { // set the id to all paths in the list.
			path.setPathId(id);
			id++;
		}
	}
}