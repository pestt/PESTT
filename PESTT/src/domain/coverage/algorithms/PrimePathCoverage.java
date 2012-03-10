package domain.coverage.algorithms;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SimplePath;
import domain.graph.visitors.DepthFirstGraphVisitor;
;

public class PrimePathCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	private Set<Path<V>> primePaths;
	private Deque<Node<V>> deque;
	private Set<Path<V>> paths;
	
	public PrimePathCoverage(Graph<V> graph) {
		this.graph = graph;
		primePaths = new TreeSet<Path<V>>();
		deque = new LinkedList<Node<V>>();
		paths = new TreeSet<Path<V>>();
	}

	public Set<Path<V>> getTestRequirements() {
		for(Node<V> node : graph.getNodes()) {
			SimplePathCoverageVisitor ppc = new SimplePathCoverageVisitor(graph);
			node.accept(ppc);
			primePaths.addAll(ppc.getPaths());
		}
		return primePaths;
	}
	
	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor<V> {
			
		public SimplePathCoverageVisitor(Graph<V> graph) {
			this.graph = graph;
			deque.clear();
			paths.clear();
		}
		
		public Set<Path<V>> getPaths() {
			return paths;
		}
	
		@Override
		public boolean visit(Node<V> node) {
			if(deque.contains(node)) {
				if(deque.getFirst() == node) {
					deque.addLast(node);
					addPath(deque);
					deque.removeLast();
				} else
					addPath(deque);
				return false;
			}
			deque.addLast(node);
			if(graph.isFinalNode(node)) {
				addPath(deque); 
			}
			return true;
		}

		private void addPath(Deque<Node<V>> nodes) {
			Path<V> toAdd = deque.getFirst() == deque.getLast() ?
					new CyclePath<V>(nodes) : new SimplePath<V>(nodes);
			if (!isSubPathInSet(toAdd))
				paths.add(toAdd);			
		}

		private boolean isSubPathInSet(Path<V> path) {
			for (Path<V> setPath : primePaths)
				if (setPath.isSubPath(path))
					return true;
			return false;
		}
		
		@Override
		public void endVisit(Node<V> node) {
			deque.removeLast();
		}
	}
}