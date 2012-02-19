package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sourcegraph.Graph;
import sourcegraph.Path;

public class SimpleRoundTripCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private List<Path<V>> paths;
	private ICoverageAlgorithms<V> algorithm;

	public SimpleRoundTripCoverage() {
		algorithm = new CompleteRoundTripCoverage<V>();
	}
	
	@Override
	public boolean visit(Graph<V> graph) {
		algorithm.visitGraph(graph);
		paths = algorithm.getTestRequirements();
		getRoundTrip();
		return true;
	}
	
	private void getRoundTrip() {
		List<Integer> inList = new ArrayList<Integer>();
		List<Integer> toRemove = new ArrayList<Integer>();
		for(Path<V> path : paths) 
			if(!inList.contains(path.getPathNodes().get(0).getValue())) 
				inList.add((Integer) path.getPathNodes().get(0).getValue());
			else
				toRemove.add(paths.indexOf(path));
				
		Collections.reverse(toRemove);
		for(int index : toRemove)
			paths.remove(index);
	}

	public List<Path<V>> getTestRequirements() {
		return paths;
	}

}