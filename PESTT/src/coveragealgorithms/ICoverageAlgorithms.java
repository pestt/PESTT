package coveragealgorithms;

import graphvisitors.IGraphVisitor;
import java.util.ArrayList;
import sourcegraph.Path;

public interface ICoverageAlgorithms<V> extends IGraphVisitor<V> {
	
	public ArrayList<Path<V>> getTestRequirements();
}
