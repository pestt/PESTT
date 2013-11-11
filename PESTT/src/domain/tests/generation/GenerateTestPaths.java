package domain.tests.generation;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class GenerateTestPaths<V extends Comparable<V>> {

	private Graph<Integer> graph;
	private Set<Path<Integer>> paths;
	private Set<AbstractPath<Integer>> requirements;
	private Set<AbstractPath<Integer>> visited;
	private Set<AbstractPath<Integer>> original;
	private Path<Integer> simplePath;
	private Node<Integer> begin;
	private List<AbstractPath<Integer>> terminate;

	public GenerateTestPaths(Graph<Integer> graph) {
		this.graph = graph;
		paths = new TreeSet<Path<Integer>>();
		requirements = new TreeSet<AbstractPath<Integer>>();
		visited = new TreeSet<AbstractPath<Integer>>();
		terminate = new ArrayList<AbstractPath<Integer>>();
		Iterable<AbstractPath<Integer>> automatic = Activator.getDefault()
				.getTestRequirementController().getTestRequirements();
		Iterable<Path<Integer>> manually = Activator.getDefault()
				.getTestRequirementController()
				.getTestRequirementsManuallyAdded();
		getAllRequirements(automatic, manually);
		original = requirements;
	}

	private void getAllRequirements(Iterable<AbstractPath<Integer>> automatic,
			Iterable<Path<Integer>> manually) {
		for (AbstractPath<Integer> path : automatic)
			if (!Activator.getDefault().getTestRequirementController()
					.isInfeasiblesTestRequirements(path))
				requirements.add(path);
		for (Path<Integer> path : manually)
			if (!Activator.getDefault().getTestRequirementController()
					.isInfeasiblesTestRequirements(path))
				requirements.add(path);
		Set<AbstractPath<Integer>> temp = requirements;
		List<AbstractPath<Integer>> toRemove = new ArrayList<AbstractPath<Integer>>();
		for (AbstractPath<Integer> current : requirements)
			for (AbstractPath<Integer> path : temp)
				if (current.compareTo(path) != 0 && path.isSubPath(current))
					if (!toRemove.contains(current))
						toRemove.add(current);
		requirements.removeAll(toRemove);
	}

	public Set<Path<Integer>> getTestPaths() {
		List<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		for (AbstractPath<Integer> path : requirements)
			if (!visited.contains(path)) {
				nodes.clear();
				if (graph.isInitialNode(path.from())) {
					if (graph.isFinalNode(path.to())) {
						paths.add(new Path<Integer>(getPathNodes(path)));
						visited.add(path);
					} else
						addTestPath(path, nodes);
				} else {
					begin = path.from();
					Node<Integer> start = graph.getInitialNodes().iterator()
							.next();
					SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(
							graph);
					start.accept(visitor);
					List<Node<Integer>> missingNodes = getPathNodes(simplePath);
					missingNodes.remove(missingNodes.size() - 1);
					nodes.addAll(missingNodes);
					addTestPath(path, nodes);
				}
			}
		return paths;
	}

	private void addTestPath(AbstractPath<Integer> path,
			List<Node<Integer>> nodes) {
		visited.add(path);
		nodes.addAll(getPathNodes(path));
		int i = 0;
		AbstractPath<Integer> last = null;
		int pos = -1;
		while (!graph.isFinalNode(nodes.get(nodes.size() - 1))) {
			List<Node<Integer>> next = getNextNode(nodes.subList(i,
					nodes.size()));
			if (next != null && !next.isEmpty()) {
				nodes.addAll(next);
				last = null;
				pos = -1;
			} else {
				if (last == null && !terminate.isEmpty()) {
					last = terminate.get(0);

					pos = i;
				}
			}
			terminate.clear();
			i++;
			if (i >= nodes.size())
				break;
		}
		if (last != null && pos != -1) {
			visited.add(last);
			List<Node<Integer>> selectedPathNodes = getPathNodes(last);
			i = 0;
			while (nodes.get(pos) == selectedPathNodes.get(i)) {
				i++;
				pos++;
				if (pos >= nodes.size())
					break;
			}

			nodes.addAll(selectedPathNodes.subList(i, selectedPathNodes.size()));
		}
		if (!graph.isFinalNode(nodes.get(nodes.size() - 1))) {
			SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(
					graph);
			nodes.get(nodes.size() - 1).accept(visitor);
			nodes.remove(nodes.size() - 1);
			nodes.addAll(getPathNodes(simplePath));
		}
		paths.add(new Path<Integer>(nodes));
	}

	private List<Node<Integer>> getNextNode(List<Node<Integer>> nodesSublist) {
		List<AbstractPath<Integer>> candidates = new ArrayList<AbstractPath<Integer>>();
		for (AbstractPath<Integer> path : original)
			if (!visited.contains(path))
				if (isCandidate(nodesSublist, path))
					if (graph.isFinalNode(path.to()))
						terminate.add(path);
					else
						candidates.add(path);

		List<Node<Integer>> nodesToAdd;
		if (!candidates.isEmpty()) {
			AbstractPath<Integer> selectedPath = candidates.get(candidates
					.size() - 1);
			List<Node<Integer>> selectedPathNodes = getPathNodes(selectedPath);
			visited.add(selectedPath);
			nodesToAdd = selectedPathNodes.subList(nodesSublist.size(),
					selectedPathNodes.size());
			return nodesToAdd;
		} else
			return null;
	}

	private boolean isCandidate(List<Node<Integer>> nodesSublist,
			AbstractPath<Integer> path) {
		List<Node<Integer>> pathNodes = getPathNodes(path);
		for (int i = 0; i < nodesSublist.size() && i < pathNodes.size(); i++)
			if (pathNodes.get(i) != nodesSublist.get(i))
				return false;
		return true;
	}

	private List<Node<Integer>> getPathNodes(AbstractPath<Integer> path) {
		List<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		for (Node<Integer> node : path)
			nodes.add(node);
		return nodes;
	}

	private class SimplePathCoverageVisitor extends
			DepthFirstGraphVisitor<Integer> {

		private Deque<Node<Integer>> pathNodes;
		private List<Node<Integer>> visitedNodes;

		public SimplePathCoverageVisitor(Graph<Integer> graph) {
			this.graph = graph;
			pathNodes = new LinkedList<Node<Integer>>();
			visitedNodes = new ArrayList<Node<Integer>>();
		}

		@Override
		public boolean visit(Node<Integer> node) {
			if (!visitedNodes.contains(node)) {
				visitedNodes.add(node);
				if (node == begin || graph.isFinalNode(node)) {
					pathNodes.addLast(node);
					simplePath = new Path<Integer>(pathNodes);
					return false;
				}
				pathNodes.addLast(node);
				return true;
			}
			return false;
		}

		@Override
		public void endVisit(Node<Integer> node) {
			pathNodes.removeLast();
		}
	}

}