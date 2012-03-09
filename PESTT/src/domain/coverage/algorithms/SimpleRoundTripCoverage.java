package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.Path;

public class SimpleRoundTripCoverage<V extends Comparable<V>> implements
		ICoverageAlgorithms<V> {

	private Graph<V> graph;

	public SimpleRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<Path<V>> getTestRequirements() {
		Set<Path<V>> paths = new PrimePathCoverage<V>(graph).getTestRequirements();
		Set<Path<V>> result = new TreeSet<Path<V>>();
		Path<V> prevPath = null;
		for(Path<V> path : paths)
			if(path instanceof CyclePath) {
				if(prevPath != null && prevPath.getPathNodes().get(0) != path.getPathNodes().get(0))
					result.add(prevPath);
				prevPath = path;
			}
		if(prevPath != null)
			result.add(prevPath);
		return result;
	}
}