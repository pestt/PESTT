package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;

public class CompleteRoundTripCoverage implements ICoverageAlgorithms {

	private Graph graph;

	public CompleteRoundTripCoverage(Graph graph) {
		this.graph = graph;
	}

	public Set<AbstractPath> getTestRequirements() {
		Set<AbstractPath> paths = new PrimePathCoverage(graph)
				.getTestRequirements();
		Set<AbstractPath> result = new TreeSet<AbstractPath>();
		for (AbstractPath path : paths)
			if (path instanceof CyclePath)
				result.add(path);
		return result;
	}
}