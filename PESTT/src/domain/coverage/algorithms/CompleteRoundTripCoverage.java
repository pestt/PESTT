package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.Path;

public class CompleteRoundTripCoverage<V extends Comparable<V>> implements
		ICoverageAlgorithms<V> {

	private Graph<V> graph;

	public CompleteRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<Path<V>> getTestRequirements() {
		Set<Path<V>> paths = new PrimePathCoverage<V>(graph).getTestRequirements();
		Set<Path<V>> result = new TreeSet<Path<V>>();
		for(Path<V> path : paths)
			if(path instanceof CyclePath)
				result.add(path);
		return result;
	}
}