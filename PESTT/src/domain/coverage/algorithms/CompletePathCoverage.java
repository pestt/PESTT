package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Graph;
import adt.graph.InfinitePath;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SequencePath;

public class CompletePathCoverage implements ICoverageAlgorithms {

	private Graph graph;
	private LinkedList<Node> pathNodes;
	private Set<AbstractPath> completePaths;

	public CompletePathCoverage(Graph graph) {
		this.graph = graph;
		completePaths = new TreeSet<AbstractPath>();
		pathNodes = new LinkedList<Node>();
	}

	public Set<AbstractPath> getTestRequirements() {
		CompletePathCoverageVisitor cpcv = new CompletePathCoverageVisitor(
				graph);
		graph.accept(cpcv);
		return completePaths;
	}

	private class CompletePathCoverageVisitor extends DepthFirstGraphVisitor {

		private Stack<CyclePath> stack;

		public CompletePathCoverageVisitor(Graph graph) {
			this.graph = graph;
			stack = new Stack<CyclePath>();
			stack.push(new CyclePath(new ArrayList<Node>()));
		}

		@Override
		public boolean visit(Node node) {
			CyclePath currentCycle = stack.peek();
			if (currentCycle.containsNode(node))
				return false;
			if (pathNodes.contains(node) && !graph.isInitialNode(node))
				stack.push(new CyclePath(pathNodes.subList(
						pathNodes.lastIndexOf(node), pathNodes.size())));
			pathNodes.addLast(node);
			if (graph.isFinalNode(node))
				completePaths.add(parseNodes(pathNodes));
			return true;
		}

		@Override
		public void endVisit(Node node) {
			pathNodes.removeLast();
			CyclePath topPath = stack.peek();
			if (topPath.iterator().hasNext() && stack.peek().from() == node) {
				System.out.println(topPath.toString());
				System.out.println(topPath.iterator().hasNext());
				stack.pop();
			}
		}

		private AbstractPath parseNodes(List<Node> nodes) {
			int cycleStart = hasCycle(nodes);
			if (cycleStart == -1)
				return new Path(nodes);
			else { // assert nodes.size() > 1
				int cycleEnd = nodes.lastIndexOf(nodes.get(cycleStart));

				AbstractPath preCycle = null;
				if (cycleStart > 0)
					preCycle = new Path(nodes.subList(0, cycleStart));

				int innerCycleStart = hasCycle(nodes.subList(cycleStart + 1,
						cycleEnd));
				AbstractPath innerPath = null;
				if (innerCycleStart != -1) {
					innerCycleStart = cycleStart + innerCycleStart + 1; // absolute index 
					int innerCycleEnd = nodes.lastIndexOf(nodes
							.get(innerCycleStart));
					InfinitePath innerPathInfinite = new InfinitePath();
					innerPathInfinite.addSubPath(new Path(nodes.subList(
							cycleStart, innerCycleStart)));
					innerPathInfinite.addSubPath(parseNodes(nodes.subList(
							innerCycleStart, innerCycleEnd + 1)));
					if (innerCycleEnd + 1 < cycleEnd)
						innerPathInfinite.addSubPath(parseNodes(nodes.subList(
								innerCycleEnd + 1, cycleEnd + 1)));
					innerPath = innerPathInfinite;
				} else
					innerPath = new CyclePath(nodes.subList(cycleStart,
							cycleEnd + 1));

				AbstractPath posCycle = null;
				if (cycleEnd + 1 < nodes.size())
					posCycle = parseNodes(nodes.subList(cycleEnd + 1,
							nodes.size()));

				if (preCycle != null || posCycle != null) {
					SequencePath result = new SequencePath();
					if (preCycle != null)
						result.addSubPath(preCycle);
					result.addSubPath(innerPath);
					if (posCycle != null)
						result.addSubPath(posCycle);
					return result;
				} else
					return innerPath;
			}
		}

		private int hasCycle(List<Node> nodes) {
			int i = 0;
			for (Node node : nodes) {
				if (nodes.indexOf(node) != nodes.lastIndexOf(node))
					return i;
				i++;
			}
			return -1;
		}

	};
}