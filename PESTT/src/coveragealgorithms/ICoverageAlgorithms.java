package coveragealgorithms;

import graphvisitors.IGraphVisitor;

import java.util.List;

import sourcegraph.Path;

public interface ICoverageAlgorithms<V> extends IGraphVisitor<V> {
	
	public List<Path<V>> getTestRequirements();
}
