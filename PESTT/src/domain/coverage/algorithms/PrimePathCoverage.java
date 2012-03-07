package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.graph.visitors.BreadthFirstGraphVisitor;

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
	
	private class PrimePathCoverageVisitor extends BreadthFirstGraphVisitor<V> {
		
		private Set<Path<V>> paths;
		private Set<Path<V>> temp;
		private Set<Path<V>> primePaths;
	
		public PrimePathCoverageVisitor() {
			paths = new TreeSet<Path<V>>();
			temp = new TreeSet<Path<V>>();
			primePaths = new TreeSet<Path<V>>();
		}
		
		public Set<Path<V>> getPaths() {
			return primePaths;
		}
	
		@Override
		public boolean visit(Graph<V> graph) {
			for(Node<V> node : graph.getNodes())
				if(!graph.isFinalNode(node))
					paths.add(new Path<V>(node));
				else
					primePaths.add(new Path<V>(node));
	
			while(!paths.isEmpty()) {
				for(Path<V> path : paths)
					addNodes(path);
	
				if(temp.isEmpty() && primePaths.isEmpty()) {
					primePaths = paths;
					return true;
				}
				
				paths.clear();
				for(Path<V> path : temp)
					paths.add(path);
				temp.clear();
			}
			getPrimePaths();
			return true;
		}
	
		private void addNodes(Path<V> path) {
			Node<V> firstNode = path.getPathNodes().get(0);
			Node<V> finalNode = path.getPathNodes().get(path.getPathNodes().size() - 1);
			for(Edge<V> edge : graph.getNodeEdges(finalNode)) {
				Path<V> aux = path.clone();
				if(graph.isFinalNode(edge.getEndNode()) || firstNode == edge.getEndNode()) {
					aux.addNode(edge.getEndNode());
					primePaths.add(aux);
				} else if(aux.containsNode(edge.getEndNode())) 
					primePaths.add(aux);
				else if(!aux.containsNode(edge.getEndNode())) {
					aux.addNode(edge.getEndNode());
					temp.add(aux);
				}
			}
		}
		
		public void getPrimePaths() {
			temp.clear();
			for(Path<V> path : primePaths) 
				temp.add(path);				
					
			List<Path<V>> toRemove = new ArrayList<Path<V>>();
			for(Path<V> path : primePaths)
				for(Path<V> p : temp)
					if(path != p && path.isSubPath(p))
						if(!toRemove.contains(p)) {
							toRemove.add(p);
							break;
						}		
			
			for(Path<V> path : toRemove)
				primePaths.remove(path);
		}
	}
}