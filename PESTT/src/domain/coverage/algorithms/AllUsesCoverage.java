package domain.coverage.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;

public class AllUsesCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {
	
	private Graph<V> graph;
	
	public AllUsesCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<AbstractPath<V>> getTestRequirements() {
		Set<AbstractPath<V>> paths = new AllDuPathsCoverage<V>(graph).getTestRequirements();
		Map<String, List<List<Object>>> defusesByVariable = Activator.getDefault().getDefUsesController().getDefUsesByVariable();
		Set<AbstractPath<V>> result = new TreeSet<AbstractPath<V>>();
		for(String key : defusesByVariable.keySet()) {
			List<List<Object>> variableDefUses = defusesByVariable.get(key);
			List<Object> defs = variableDefUses.get(0);
			List<Object> uses = variableDefUses.get(1);
			for(AbstractPath<V> path : paths)
				for(Object def : defs) {
					Node<V> begin;
					if(def instanceof Edge<?>) 
						begin = ((Edge<V>) def).getBeginNode();
					else 
						begin = ((Node<V>) def);
					if(begin == path.from())
						for(Object use : uses) {
							Node<V> end;
							if(use instanceof Edge<?>) 
								end = ((Edge<V>) use).getEndNode();
							else 
								end = ((Node<V>) use);
							if(end == path.to()) 
								result.add(path);
						}
				}
		}
		return result;
	}
}
