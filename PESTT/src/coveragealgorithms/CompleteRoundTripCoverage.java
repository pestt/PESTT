package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sourcegraph.Graph;
import sourcegraph.Path;

public class CompleteRoundTripCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private List<Path<V>> paths;
	private ICoverageAlgorithms<V> algorithm;

	public CompleteRoundTripCoverage() {
		algorithm = new PrimePathCoverage<V>();
	}
	
	@Override
	public boolean visit(Graph<V> graph) {
		algorithm.visitGraph(graph);
		paths = algorithm.getTestRequirements();
		getRoundTrip();
		return true;
	}
	
	private void getRoundTrip() {
		List<Integer> toRemove = new ArrayList<Integer>();
		for(Path<V> path : paths) 
			if(path.getPathNodes().get(0) != path.getPathNodes().get(path.getPathNodes().size() - 1)) 
				toRemove.add(paths.indexOf(path));
			
		Collections.reverse(toRemove);
		for(int index : toRemove)
			paths.remove(index);
	}

	public List<Path<V>> getTestRequirements() {
		return paths;
	}

}