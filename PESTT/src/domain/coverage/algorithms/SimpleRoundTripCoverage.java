package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;

public class SimpleRoundTripCoverage<V extends Comparable<V>> implements
		ICoverageAlgorithms<V> {

	private Graph<V> graph;

	public SimpleRoundTripCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<AbstractPath<V>> getTestRequirements() {
		Set<AbstractPath<V>> paths = new PrimePathCoverage<V>(graph)
				.getTestRequirements();
		Set<AbstractPath<V>> result = new TreeSet<AbstractPath<V>>();
		AbstractPath<V> prevPath = null;
		for (AbstractPath<V> path : paths)
			if (path instanceof CyclePath) {
				if (prevPath != null && prevPath.from() != path.from())
					result.add(prevPath);
				prevPath = path;
			}
		if (prevPath != null)
			result.add(prevPath);
		return result;
	}
}