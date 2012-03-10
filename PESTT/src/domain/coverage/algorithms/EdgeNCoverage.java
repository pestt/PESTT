package domain.coverage.algorithms;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SimplePath;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class EdgeNCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {
	
	private Set<Path<V>> paths;
	private Deque<Node<V>> deque;
	private Graph<V> graph;
	private int edgeSize;
	
	public EdgeNCoverage(Graph<V> graph, int edgeSize) {
		this.graph = graph;
		this.edgeSize = edgeSize;
		paths = new HashSet<Path<V>>();
		deque = new LinkedList<Node<V>>();
	}

	public Set<Path<V>> getTestRequirements() {
    	paths.clear();
		for(Node<V> node : graph.getNodes()) {
			EdgePairCoverageVisitor epc = new EdgePairCoverageVisitor(graph);
			node.accept(epc);
		}
    	return paths;
	}
	
	private class EdgePairCoverageVisitor extends DepthFirstGraphVisitor<V> {	
		
		public EdgePairCoverageVisitor(Graph<V> graph) {
			this.graph = graph;
			deque.clear();
		}
		
		@Override
		public boolean visit(Node<V> node) { 
			deque.addLast(node);
			paths.add(deque.getLast() == deque.getLast() ?
					new CyclePath<V>(deque) : new SimplePath<V>(deque));
			if (deque.size() - 1 == edgeSize) {
				deque.removeLast();
				return false;
			}
			return true;
		}

		@Override
		public void endVisit(Node<V> node) {
			deque.removeLast();
		}
	}
}