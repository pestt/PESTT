package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import adt.graph.Graph;
import adt.graph.Path;
import domain.graph.visitors.BreadthFirstGraphVisitor;

public class CompleteRoundTripCoverage<V extends Comparable<V>>  implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	
	public CompleteRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}
	
	public Set<Path<V>> getTestRequirements() {
		CompleteRoundTripCoverageVisitor crtcv = new CompleteRoundTripCoverageVisitor();
		crtcv.visitGraph(graph);
		return crtcv.getPaths();
	}
	
	private class CompleteRoundTripCoverageVisitor extends BreadthFirstGraphVisitor<V> {
	
		private Set<Path<V>> paths;
		private ICoverageAlgorithms<V> algorithm;
		
		CompleteRoundTripCoverageVisitor() {
			algorithm = new PrimePathCoverage<V>(graph);
		}
		
		private Set<Path<V>> getPaths() {
			return paths;
		}
		
		@Override
		public boolean visit(Graph<V> graph) {
			paths = algorithm.getTestRequirements();
			getRoundTrip();
			return true;
		}
		
		private void getRoundTrip() {
			List<Path<V>> toRemove = new ArrayList<Path<V>>();
			for(Path<V> path : paths) 
				if(path.getPathNodes().get(0) != path.getPathNodes().get(path.getPathNodes().size() - 1)) 
					toRemove.add(path);
					
			for(Path<V> path : toRemove)
				paths.remove(path);
		}
	}
}