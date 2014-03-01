package domain.tests.generation;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;
import adt.graph.AbstractPath;
import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;

public class GenerateTestPaths<V extends Comparable<V>> {

	private Graph graph;
	private Set<Path> paths;
	private Set<AbstractPath> requirements;
	private Set<AbstractPath> visited;
	private Set<AbstractPath> original;
	private Path simplePath;
	private Node begin;
	private List<AbstractPath> terminate;

	public GenerateTestPaths(Graph graph) {
		this.graph = graph;
		paths = new TreeSet<Path>();
		requirements = new TreeSet<AbstractPath>();
		visited = new TreeSet<AbstractPath>();
		terminate = new ArrayList<AbstractPath>();
		Iterable<AbstractPath> automatic = Activator.getDefault()
				.getTestRequirementController().getTestRequirements();
		Iterable<Path> manually = Activator.getDefault()
				.getTestRequirementController()
				.getTestRequirementsManuallyAdded();
		getAllRequirements(automatic, manually);
		original = requirements;
	}

	private void getAllRequirements(Iterable<AbstractPath> automatic,
			Iterable<Path> manually) {
		for (AbstractPath path : automatic)
			if (!Activator.getDefault().getTestRequirementController()
					.isInfeasiblesTestRequirements(path))
				requirements.add(path);
		for (Path path : manually)
			if (!Activator.getDefault().getTestRequirementController()
					.isInfeasiblesTestRequirements(path))
				requirements.add(path);
		Set<AbstractPath> temp = requirements;
		List<AbstractPath> toRemove = new ArrayList<AbstractPath>();
		for (AbstractPath current : requirements)
			for (AbstractPath path : temp)
				if (current.compareTo(path) != 0 && path.isSubPath(current))
					if (!toRemove.contains(current))
						toRemove.add(current);
		requirements.removeAll(toRemove);
	}

	public Set<Path> getTestPaths() {
		List<Node> nodes = new ArrayList<Node>();
		for (AbstractPath path : requirements)
			if (!visited.contains(path)) {
				nodes.clear();
				if (graph.isInitialNode(path.from())) {
					if (graph.isFinalNode(path.to())) {
						paths.add(new Path(getPathNodes(path)));
						visited.add(path);
					} else
						addTestPath(path, nodes);
				} else {
					begin = path.from();
					Node start = graph.getInitialNodes().iterator()
							.next();
					SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(
							graph);
					start.accept(visitor);
					List<Node> missingNodes = getPathNodes(simplePath);
					missingNodes.remove(missingNodes.size() - 1);
					nodes.addAll(missingNodes);
					addTestPath(path, nodes);
				} 
			}
		return paths;
	}

	private void addTestPath(AbstractPath path,
			List<Node> nodes) {
		visited.add(path);
		nodes.addAll(getPathNodes(path));
		int i = 0;
		AbstractPath last = null;
		int pos = -1;
		while (!graph.isFinalNode(nodes.get(nodes.size() - 1))) {
			List<Node> next = getNextNode(nodes.subList(i,
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
			List<Node> selectedPathNodes = getPathNodes(last);
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
		paths.add(new Path(nodes));
	}

	private List<Node> getNextNode(List<Node> nodesSublist) {
		List<AbstractPath> candidates = new ArrayList<AbstractPath>();
		for (AbstractPath path : original)
			if (!visited.contains(path))
				if (isCandidate(nodesSublist, path))
					if (graph.isFinalNode(path.to()))
						terminate.add(path);
					else
						candidates.add(path);

		List<Node> nodesToAdd;
		if (!candidates.isEmpty()) {
			AbstractPath selectedPath = candidates.get(candidates
					.size() - 1);
			List<Node> selectedPathNodes = getPathNodes(selectedPath);
			visited.add(selectedPath);
			nodesToAdd = selectedPathNodes.subList(nodesSublist.size(),
					selectedPathNodes.size());
			return nodesToAdd;
		} else
			return null; //!= null checks
	}

	private boolean isCandidate(List<Node> nodesSublist,
			AbstractPath path) {
		List<Node> pathNodes = getPathNodes(path);
		for (int i = 0; i < nodesSublist.size() && i < pathNodes.size(); i++)
			if (pathNodes.get(i) != nodesSublist.get(i))
				return false;
		return true;
	}

	private List<Node> getPathNodes(AbstractPath path) {
		List<Node> nodes = new ArrayList<Node>();
		for (Node node : path)
			nodes.add(node);
		return nodes;
	}

	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor {

		private Deque<Node> pathNodes;
		private List<Node> visitedNodes;

		public SimplePathCoverageVisitor(Graph graph) {
			this.graph = graph;
			pathNodes = new LinkedList<Node>();
			visitedNodes = new ArrayList<Node>();
		}

		@Override
		public boolean visit(Node node) {
			if (!visitedNodes.contains(node)) {
				visitedNodes.add(node);
				if (node == begin || graph.isFinalNode(node)) {
					pathNodes.addLast(node);
					simplePath = new Path(pathNodes);
					return false;
				}
				pathNodes.addLast(node);
				return true;
			}
			return false;
		}

		@Override
		public void endVisit(Node node) {
			pathNodes.removeLast();
		}
	}

}