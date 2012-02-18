package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;

import java.util.LinkedList;
import java.util.List;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;

public class EdgeCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private List<Path<V>> paths;
	private List<Node<V>> visitedNodes;
	private Graph<V> graph;
	
	public EdgeCoverage() {
		paths = new LinkedList<Path<V>>();
		visitedNodes = new LinkedList<Node<V>>();
	}
	
	@Override
	public void endVisit(Node<V> node) {
		if(!visitedNodes.contains(node)) { // set the id to all paths in the list.
			visitedNodes.add(node); // add the node to be visited.
			Path<V> path = new Path<V>(node); // create a new path with the first node.
			paths.add(path); // add path to the list.
			for(Edge<V> edge : graph.getNodeEdges(node)) { // get the paths of length two.
				path = new Path<V>(node); // create a new path with the first node.
				path.addNode(edge.getEndNode()); //add the second node.
				paths.add(path); // add path to the list.
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Path<V>> getTestRequirements() {
		paths = new SortPaths().sort(paths); // sort the paths.
		return paths;
	}
	
	@Override
	public boolean visit(Graph<V> graph) {
		this.graph = graph;
		return true;
	}
}