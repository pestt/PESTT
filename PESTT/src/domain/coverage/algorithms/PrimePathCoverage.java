package domain.coverage.algorithms;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class PrimePathCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	private Set<AbstractPath<V>> primePaths;
	private Deque<Node<V>> pathNodes;
	
	public PrimePathCoverage(Graph<V> graph) {
		this.graph = graph;
		primePaths = new TreeSet<AbstractPath<V>>();
		pathNodes = new LinkedList<Node<V>>();
	}

	public Set<AbstractPath<V>> getTestRequirements() {
		for(Node<V> node : graph.getNodes()) {
			SimplePathCoverageVisitor ppc = new SimplePathCoverageVisitor(graph);
			node.accept(ppc);
		}
		return primePaths;
	}
	
	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor<V> {
			
		public SimplePathCoverageVisitor(Graph<V> graph) {
			this.graph = graph;
			pathNodes.clear();
		}
			
		@Override
		public boolean visit(Node<V> node) {
			if(pathNodes.contains(node)) {
				if(pathNodes.getFirst() == node) {
					pathNodes.addLast(node);
					addPath(pathNodes);
					pathNodes.removeLast();
				} else
					addPath(pathNodes);
				return false;
			}
			pathNodes.addLast(node);
			if(graph.isFinalNode(node)) {
				addPath(pathNodes); 
			}
			return true;
		}

		private void addPath(Deque<Node<V>> nodes) {
			Path<V> toAdd = pathNodes.getFirst() == pathNodes.getLast() ? new CyclePath<V>(nodes) : new Path<V>(nodes);
			if(!isSubPathInSet(toAdd))
				primePaths.add(toAdd);			
		}

		private boolean isSubPathInSet(Path<V> path) {
			for(AbstractPath<V> setPath : primePaths)
				if(setPath.isSubPath(path))
					return true;
			return false;
		}
		
		@Override
		public void endVisit(Node<V> node) {
			pathNodes.removeLast();
		}
	}
}