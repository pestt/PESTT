package domain.coverage.algorithms;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;

public class AllDuPathsCoverage implements ICoverageAlgorithms {

	private Graph graph;
	private Set<AbstractPath> allDuPaths;
	private Deque<Node> pathNodes;
	private Map<String, List<List<Object>>> defuses;

	public AllDuPathsCoverage(Graph graph) {
		this.graph = graph;
		allDuPaths = new TreeSet<AbstractPath>();
		pathNodes = new LinkedList<Node>();
		defuses = Activator.getDefault().getDefUsesController()
				.getDefUsesByVariable();
	}

	@SuppressWarnings("unchecked")
	public Set<AbstractPath> getTestRequirements() {
		for (String key : defuses.keySet()) {
			List<List<Object>> variableDefUses = defuses.get(key);
			List<Object> defs = variableDefUses.get(0);
			List<Object> uses = variableDefUses.get(1);
			if (!uses.isEmpty())
				for (Object obj : defs) {
					Node node;
					if (obj instanceof Edge)
						node = ((Edge) obj).getBeginNode();
					else
						node = ((Node) obj);
					SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(
							graph, defs, uses);
					node.accept(visitor);
				}
		}
		return allDuPaths;
	}

	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor {

		private List<Object> defs;
		private List<Object> uses;

		public SimplePathCoverageVisitor(Graph graph, List<Object> defs,
				List<Object> uses) {
			this.graph = graph;
			this.defs = defs;
			this.uses = uses;
			pathNodes.clear();
		}

		@Override
		public boolean visit(Node node) {
			if (!isClearPath(node))
				return false;
			if (pathNodes.contains(node)) {
				if (pathNodes.getFirst() == node) {
					pathNodes.addLast(node);
					addPath(pathNodes);
					pathNodes.removeLast();
				}
				return false;
			}
			pathNodes.addLast(node);
			if (isUseNode(node))
				addPath(pathNodes);
			return true;
		}

		private boolean isClearPath(Node node) {
			if (!pathNodes.isEmpty())
				if (isDefNode(node))
					if (pathNodes.getFirst() != node) {
						pathNodes.addLast(node);
						if (!isUseNode(node)) {
							pathNodes.removeLast();
						} else {
							addPath(pathNodes);
							pathNodes.removeLast();
						}
						return false;
					}
			return true;
		}

		@SuppressWarnings("unchecked")
		private boolean isDefNode(Node node) {
			for (Object obj : defs) {
				Node n;
				if (obj instanceof Edge)
					n = ((Edge) obj).getBeginNode();
				else
					n = ((Node) obj);
				if (n == node)
					return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		private boolean isUseNode(Node node) {
			for (Object obj : uses) {
				Node n = null;
				if (obj instanceof Edge) {
					if (pathNodes.size() > 1) {
						pathNodes.removeLast();
						if (pathNodes.getLast() == ((Edge) obj)
								.getBeginNode())
							n = ((Edge) obj).getEndNode();
						pathNodes.addLast(node);
					}
				} else
					n = ((Node) obj);
				if (n != null)
					if (n == node)
						return true;
			}
			return false;
		}

		private void addPath(Deque<Node> nodes) {
			Path toAdd = new Path(nodes);
			if (nodes.size() > 1)
				allDuPaths.add(toAdd);
		}

		@Override
		public void endVisit(Node node) {
			pathNodes.removeLast();
		}
	}
}