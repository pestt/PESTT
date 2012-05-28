package domain.coverage.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;

public class AllUsesCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {
	
	private Graph<V> graph;
	private Stack<Node<V>> nodes;
	
	public AllUsesCoverage(Graph<V> graph) {
		this.graph = graph;
		nodes = new Stack<Node<V>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<AbstractPath<V>> getTestRequirements() {
		Set<AbstractPath<V>> paths = new AllDuPathsCoverage<V>(graph).getTestRequirements();
		Map<String, List<List<Object>>> defusesByVariable = Activator.getDefault().getDefUsesController().getDefUsesByVariable();
		Set<AbstractPath<V>> result = new TreeSet<AbstractPath<V>>();
		for(String key : defusesByVariable.keySet()) {
			nodes.clear();
			List<List<Object>> variableDefUses = defusesByVariable.get(key);
			List<Object> defs = variableDefUses.get(0);
			List<Object> uses = variableDefUses.get(1);
			for(AbstractPath<V> path : paths)
				if(isTestRequirement(defs, uses, path))
					for(Object obj : uses) {
						Node<V> node;
						if(obj instanceof Edge<?>) 
							node = ((Edge<V>) obj).getEndNode();
						else 
							node = ((Node<V>) obj);
						if(node == path.to() && !nodes.contains(node)) {
							nodes.add(node);
							result.add(path);
						}
					}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean isTestRequirement(List<Object> defs, List<Object> uses, AbstractPath<V> path) {
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
						return true;
			} 			
		}
		return false;
	}
}
