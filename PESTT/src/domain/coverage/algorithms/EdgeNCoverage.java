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

public class EdgeNCoverage implements ICoverageAlgorithms {

	private Set<AbstractPath> paths;
	private Deque<Node> deque;
	private Graph graph;
	private int edgeSize;

	public EdgeNCoverage(Graph graph, int edgeSize) {
		this.graph = graph;
		this.edgeSize = edgeSize;
		paths = new TreeSet<AbstractPath>();
		deque = new LinkedList<Node>();
	}

	@Override
	public Set<AbstractPath> getTestRequirements() {
		paths.clear();
		for (Node node : graph.getNodes()) {
			EdgePairCoverageVisitor epc = new EdgePairCoverageVisitor(graph);
			node.accept(epc);
		}
		return paths;
	}

	private class EdgePairCoverageVisitor extends DepthFirstGraphVisitor {

		public EdgePairCoverageVisitor(Graph graph) {
			this.graph = graph;
			deque.clear();
		}

		@Override
		public boolean visit(Node node) {
			deque.addLast(node);
			paths.add(deque.getLast() == deque.getLast() ? new CyclePath(
					deque) : new Path(deque));
			if (deque.size() - 1 == edgeSize) {
				deque.removeLast();
				return false;
			}
			return true;
		}

		@Override
		public void endVisit(Node node) {
			deque.removeLast();
		}
	}
}