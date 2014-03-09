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

public class AllUsesCoverage implements ICoverageAlgorithms {

	private Graph graph;

	public AllUsesCoverage(Graph graph) {
		this.graph = graph;
	}

	@Override
	public Set<AbstractPath> getTestRequirements() {
		Set<AbstractPath> paths = new AllDuPathsCoverage(graph)
				.getTestRequirements();
		Map<String, List<List<Object>>> defusesByVariable = Activator
				.getDefault().getDefUsesController().getDefUsesByVariable();
		Set<AbstractPath> result = new TreeSet<AbstractPath>();
		for (String key : defusesByVariable.keySet()) {
			List<List<Object>> variableDefUses = defusesByVariable.get(key);
			List<Object> defs = variableDefUses.get(0);
			List<Object> uses = variableDefUses.get(1);
			for (AbstractPath path : paths)
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
								result.add(path);
						}
				}
		}
		return result;
	}
}
