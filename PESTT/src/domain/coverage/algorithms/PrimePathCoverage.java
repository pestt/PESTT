package domain.coverage.algorithms;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;

public class PrimePathCoverage implements ICoverageAlgorithms {

	private Graph graph;
	private Set<AbstractPath> primePaths;
	private Deque<Node> pathNodes;

	public PrimePathCoverage(Graph graph) {
		this.graph = graph;
		primePaths = new TreeSet<AbstractPath>();
		pathNodes = new LinkedList<Node>();
	}

	public Set<AbstractPath> getTestRequirements() {
		for (Node node : graph.getNodes()) {
			SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(
					graph);
			node.accept(visitor);
		}
		return primePaths;
	}

	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor {

		public SimplePathCoverageVisitor(Graph graph) {
			this.graph = graph;
			pathNodes.clear();
		}

		@Override
		public boolean visit(Node node) {
			if (pathNodes.contains(node)) {
				if (pathNodes.getFirst() == node) {
					pathNodes.addLast(node);
					addPath(pathNodes);
					pathNodes.removeLast();
				} else
					addPath(pathNodes);
				return false;
			}
			pathNodes.addLast(node);
			if (graph.isFinalNode(node))
				addPath(pathNodes);
			return true;
		}

		private void addPath(Deque<Node> nodes) {
			Path toAdd = pathNodes.getFirst() == pathNodes.getLast() ? new CyclePath(
					nodes) : new Path(nodes);
			if (!isSubPathInSet(toAdd))
				primePaths.add(toAdd);
		}

		private boolean isSubPathInSet(Path path) {
			for (AbstractPath setPath : primePaths)
				if (setPath.isSubPath(path))
					return true;
			return false;
		}

		@Override
		public void endVisit(Node node) {
			pathNodes.removeLast();
		}
	}
}