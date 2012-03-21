package domain.coverage.algorithms;

import java.util.Set;

import adt.graph.AbstractPath;

public interface ICoverageAlgorithms<V extends Comparable<V>> {
	
	public Set<AbstractPath<V>> getTestRequirements();
	
}
