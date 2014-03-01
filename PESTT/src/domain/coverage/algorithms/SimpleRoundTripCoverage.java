package domain.coverage.algorithms;

import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;

public class SimpleRoundTripCoverage implements ICoverageAlgorithms {

	private Graph graph;

	public SimpleRoundTripCoverage(Graph graph) {
		this.graph = graph;
	}

	public Set<AbstractPath> getTestRequirements() {
		Set<AbstractPath> paths = new PrimePathCoverage(graph)
				.getTestRequirements();
		Set<AbstractPath> result = new TreeSet<AbstractPath>();
		AbstractPath prevPath = null;
		for (AbstractPath path : paths)
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