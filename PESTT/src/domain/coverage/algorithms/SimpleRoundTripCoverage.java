package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import adt.graph.Graph;
import adt.graph.Path;
import domain.graph.visitors.BreadthFirstGraphVisitor;

public class SimpleRoundTripCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	
	public SimpleRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}
	
	public Set<Path<V>> getTestRequirements() {
		SimpleRoundTripCoverageVisitor srtcv = new SimpleRoundTripCoverageVisitor();
		srtcv.visitGraph(graph);
		return srtcv.getPaths();
	}
	private class SimpleRoundTripCoverageVisitor extends BreadthFirstGraphVisitor<V> {
		
		private Set<Path<V>> paths;
		private ICoverageAlgorithms<V> algorithm;
	
		public SimpleRoundTripCoverageVisitor() {
			algorithm = new CompleteRoundTripCoverage<V>(graph);
		}
		
		public Set<Path<V>> getPaths() {
			return paths;
		}
		
		@Override
		public boolean visit(Graph<V> graph) {
			paths = algorithm.getTestRequirements();
			getRoundTrip();
			return true;
		}
		
		private void getRoundTrip() {
			List<Integer> inList = new ArrayList<Integer>();
			List<Path<V>> toRemove = new ArrayList<Path<V>>();
			for(Path<V> path : paths) 
				if(!inList.contains(path.getPathNodes().get(0).getValue())) 
					inList.add((Integer) path.getPathNodes().get(0).getValue());
				else
					toRemove.add(path);
				for(Path<V> path : toRemove)
					paths.remove(path);
		}
	}
}