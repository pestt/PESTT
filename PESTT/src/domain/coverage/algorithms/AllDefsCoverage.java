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

public class AllDefsCoverage implements ICoverageAlgorithms {

	private Graph graph;
	private Stack<Node> nodes;

	public AllDefsCoverage(Graph graph) {
		this.graph = graph;
		nodes = new Stack<Node>();
	}

	@Override
	public Set<AbstractPath> getTestRequirements() {
		Set<AbstractPath> paths = new AllDuPathsCoverage(graph)
				.getTestRequirements();
		Map<String, List<List<Object>>> defusesByVariable = Activator
				.getDefault().getDefUsesController().getDefUsesByVariable();
		Set<AbstractPath> result = new TreeSet<AbstractPath>();
		for (String key : defusesByVariable.keySet()) {
			nodes.clear();
			List<List<Object>> variableDefUses = defusesByVariable.get(key);
			List<Object> defs = variableDefUses.get(0);
			List<Object> uses = variableDefUses.get(1);
			for (AbstractPath path : paths)
				if (isTestRequirement(defs, uses, path))
					for (Object obj : defs) {
						Node node;
						if (obj instanceof Edge)
							node = ((Edge) obj).getBeginNode();
						else
							node = ((Node) obj);
						if (node == path.from() && !nodes.contains(node)) {
							nodes.add(node);
							result.add(path);
						}
					}
		}
		return result;
	}

	private boolean isTestRequirement(List<Object> defs, List<Object> uses,
			AbstractPath path) {
		for (Object def : defs) {
			Node begin;
			if (def instanceof Edge)
				begin = ((Edge) def).getBeginNode();
			else
				begin = ((Node) def);
			if (begin == path.from())
				for (Object use : uses) {
					Node end;
					if (use instanceof Edge)
						end = ((Edge) use).getEndNode();
					else
						end = ((Node) use);
					if (end == path.to())
						return true;
				}
		}
		return false;
	}
}
