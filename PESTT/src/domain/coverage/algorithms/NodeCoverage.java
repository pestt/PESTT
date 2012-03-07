package domain.coverage.algorithms;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.graph.visitors.BreadthFirstGraphVisitor;

public class NodeCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	
	public NodeCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<Path<V>> getTestRequirements() {
		NodeCoverageVisitor ncv = new NodeCoverageVisitor();
		ncv.visitGraph(graph);
		return ncv.getPaths();
	}
	
	private class NodeCoverageVisitor extends BreadthFirstGraphVisitor<V> {
		
		private Set<Path<V>> paths;
		private List<Node<V>> visitedNodes;
		
		public NodeCoverageVisitor() {
			visitedNodes = new LinkedList<Node<V>>();
			paths = new TreeSet<Path<V>>();
		}
		
		private Set<Path<V>> getPaths() {
			return paths;
		}
		
		@Override
		public void endVisit(Node<V> node) {
			if(!visitedNodes.contains(node)) { // set the id to all paths in the list.
				visitedNodes.add(node); // add the node to be visited.
				Path<V> path = new Path<V>(node); // create a new path with the first node.
				paths.add(path); // add path to list.
			}
		}
	}
}