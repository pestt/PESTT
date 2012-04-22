package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;

public class CompleteRoundTripCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;

	public CompleteRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<AbstractPath<V>> getTestRequirements() {
		Set<AbstractPath<V>> paths = new PrimePathCoverage<V>(graph).getTestRequirements();
		Set<AbstractPath<V>> result = new TreeSet<AbstractPath<V>>();
		for(AbstractPath<V> path : paths)
			if(path instanceof CyclePath)
				result.add(path);
		return result;
	}
}