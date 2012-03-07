package domain.coverage.algorithms;

import java.util.Set;

import adt.graph.Path;

public interface ICoverageAlgorithms<V extends Comparable<V>> {
	
	public Set<Path<V>> getTestRequirements();
	
}
