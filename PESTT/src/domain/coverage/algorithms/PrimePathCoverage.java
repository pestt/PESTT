package domain.coverage.algorithms;

import java.util.Deque;
import java.util.Iterator;
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
	
	public PrimePathCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<Path<V>> getTestRequirements() {
		PrimePathCoverageVisitor ppc = new PrimePathCoverageVisitor();
		ppc.visitGraph(graph);
		return ppc.getPaths();
	}
	
	private class PrimePathCoverageVisitor extends DepthFirstGraphVisitor<V> {
		
		private Deque<Node<V>> deque;
		private Set<Path<V>> primePaths;
	
		public PrimePathCoverageVisitor() {
			deque = new LinkedList<Node<V>>();
			primePaths = new TreeSet<Path<V>>();
		}
		
		public Set<Path<V>> getPaths() {
			return primePaths;
		}
	
		@Override
		public boolean visit(Graph<V> graph) {
			for(Node<V> node : graph.getNodes()) {
				deque.clear();
				node.accept(this);
			}
			removeSubPaths();
			return false;
		}

		private void removeSubPaths() {
			TreeSet<Path<V>> temp = new TreeSet<Path<V>>();
			Iterator<Path<V>> iterator = primePaths.iterator();
			if(iterator.hasNext()) {
				Path<V> primeAnt = iterator.next();
				while(iterator.hasNext()) {
					Path<V> primePath = iterator.next();
					if(!primeAnt.isSubPath(primePath))
						temp.add(primeAnt);
					primeAnt = primePath;
				}
				temp.add(primeAnt);
				primePaths = temp;
			}
		}

		@Override
		public boolean visit(Node<V> node) {
			if(deque.contains(node)) {
				if(deque.getFirst() == node)
					primePaths.add(new CyclePath<V>(deque)); 
				else	
					primePaths.add(new SimplePath<V>(deque)); 
				return false;
			}
			deque.addLast(node);
			if(graph.isFinalNode(node))
				primePaths.add(new SimplePath<V>(deque)); 
			return true;
		}
		
		@Override
		public void endVisit(Node<V> node) {
			deque.removeLast();
		}
	}
}