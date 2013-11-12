package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import adt.graph.AbstractPath;
import adt.graph.CyclePath;
import adt.graph.Graph;
import adt.graph.InfinitePath;
import adt.graph.Node;
import adt.graph.Path;
import adt.graph.SequencePath;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class CompletePathCoverage<V extends Comparable<V>> implements
		ICoverageAlgorithms<V> {

	private Graph<V> graph;
	private LinkedList<Node<V>> pathNodes;
	private Set<AbstractPath<V>> completePaths;

	public CompletePathCoverage(Graph<V> graph) {
		this.graph = graph;
		completePaths = new TreeSet<AbstractPath<V>>();
		pathNodes = new LinkedList<Node<V>>();
	}

	public Set<AbstractPath<V>> getTestRequirements() {
		CompletePathCoverageVisitor cpcv = new CompletePathCoverageVisitor(
				graph);
		graph.accept(cpcv);
		return completePaths;
	}

	private class CompletePathCoverageVisitor extends DepthFirstGraphVisitor<V> {

		private Stack<CyclePath<V>> stack;

		public CompletePathCoverageVisitor(Graph<V> graph) {
			this.graph = graph;
			stack = new Stack<CyclePath<V>>();
			stack.push(new CyclePath<V>(new ArrayList<Node<V>>()));
		}

		@Override
		public boolean visit(Node<V> node) {
			CyclePath<V> currentCycle = stack.peek();
			if (currentCycle.containsNode(node))
				return false;
			if (pathNodes.contains(node) && !graph.isInitialNode(node))
				stack.push(new CyclePath<V>(pathNodes.subList(
						pathNodes.lastIndexOf(node), pathNodes.size())));
			pathNodes.addLast(node);
			if (graph.isFinalNode(node))
				completePaths.add(parseNodes(pathNodes));
			return true;
		}

		@Override
		public void endVisit(Node<V> node) {
			pathNodes.removeLast();
			CyclePath<V> topPath = stack.peek();
			if (topPath.iterator().hasNext() && stack.peek().from() == node) {
				System.out.println(topPath.toString());
				System.out.println(topPath.iterator().hasNext());
				stack.pop();
			}
		}

		private AbstractPath<V> parseNodes(List<Node<V>> nodes) {
			int cycleStart = hasCycle(nodes);
			if (cycleStart == -1)
				return new Path<V>(nodes);
			else { // assert nodes.size() > 1
				int cycleEnd = nodes.lastIndexOf(nodes.get(cycleStart));

				AbstractPath<V> preCycle = null;
				if (cycleStart > 0)
					preCycle = new Path<V>(nodes.subList(0, cycleStart));

				int innerCycleStart = hasCycle(nodes.subList(cycleStart + 1,
						cycleEnd));
				AbstractPath<V> innerPath = null;
				if (innerCycleStart != -1) {
					innerCycleStart = cycleStart + innerCycleStart + 1; // absolute index 
					int innerCycleEnd = nodes.lastIndexOf(nodes
							.get(innerCycleStart));
					InfinitePath<V> innerPathInfinite = new InfinitePath<V>();
					innerPathInfinite.addSubPath(new Path<V>(nodes.subList(
							cycleStart, innerCycleStart)));
					innerPathInfinite.addSubPath(parseNodes(nodes.subList(
							innerCycleStart, innerCycleEnd + 1)));
					if (innerCycleEnd + 1 < cycleEnd)
						innerPathInfinite.addSubPath(parseNodes(nodes.subList(
								innerCycleEnd + 1, cycleEnd + 1)));
					innerPath = innerPathInfinite;
				} else
					innerPath = new CyclePath<V>(nodes.subList(cycleStart,
							cycleEnd + 1));

				AbstractPath<V> posCycle = null;
				if (cycleEnd + 1 < nodes.size())
					posCycle = parseNodes(nodes.subList(cycleEnd + 1,
							nodes.size()));

				if (preCycle != null || posCycle != null) {
					SequencePath<V> result = new SequencePath<V>();
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

		private int hasCycle(List<Node<V>> nodes) {
			int i = 0;
			for (Node<V> node : nodes) {
				if (nodes.indexOf(node) != nodes.lastIndexOf(node))
					return i;
				i++;
			}
			return -1;
		}

	};
}