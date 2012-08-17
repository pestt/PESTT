package domain.tests.instrument;

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
	private Iterable<AbstractPath<Integer>> infeasibles;
	private Set<AbstractPath<Integer>> visited;
	private Set<AbstractPath<Integer>> original;
	private Path<Integer> simplePath;
	private Node<Integer> begin;
	
	public GenerateTestPaths(Graph<Integer> graph) {
		this.graph = graph;
		paths = new TreeSet<Path<Integer>>();
		requirements = new TreeSet<AbstractPath<Integer>>();
		visited = new TreeSet<AbstractPath<Integer>>();
		Iterable<AbstractPath<Integer>> automatic = Activator.getDefault().getTestRequirementController().getTestRequirements();
		Iterable<Path<Integer>> manually = Activator.getDefault().getTestRequirementController().getTestRequirementsManuallyAdded();
		infeasibles = Activator.getDefault().getTestRequirementController().getInfeasiblesTestRequirements();
		getRequirements(automatic, manually);
		original = requirements;
	}
	
	private void getRequirements(Iterable<AbstractPath<Integer>> automatic, Iterable<Path<Integer>> manually) {
		for(AbstractPath<Integer> path : automatic)
			if(!isInfeasile(path))
				requirements.add(path);
		for(Path<Integer> path : manually)
			if(!isInfeasile(path))
				requirements.add(path);
	}
	
	private boolean isInfeasile(AbstractPath<Integer> path) {
		for(AbstractPath<Integer> p : infeasibles)
			if(p == path)
				return true;
		return false;
	}

	public Set<Path<Integer>> getTestPaths() {
		List<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		for(AbstractPath<Integer> path : requirements)  
			if(!visited.contains(path)) {
				nodes.clear();
				if(graph.isInitialNode(path.from())) {
					if(graph.isFinalNode(path.to())) {
						paths.add(new Path<Integer>(getPathNodes(path)));
						visited.add(path);
					} else 
						addTestPath(path, nodes);
				} else {
					begin = path.from();
					Node<Integer> start = graph.getInitialNodes().iterator().next();
					SimplePathCoverageVisitor visitor = new SimplePathCoverageVisitor(graph);
					start.accept(visitor);
					List<Node<Integer>> pre = getPathNodes(simplePath);
					pre.remove(pre.size() - 1);
					nodes.addAll(pre);
					addTestPath(path, nodes);
				}
			}
    	return paths;
	}

	private void addTestPath(AbstractPath<Integer> path, List<Node<Integer>> nodes) {
		visited.add(path);
		nodes.addAll(getPathNodes(path));
		int i = 0;
		while(!graph.isFinalNode(nodes.get(nodes.size() - 1))) {
			List<Node<Integer>> next = getNextNode(nodes.subList(i, nodes.size()));
			if(next != null && !next.isEmpty()) 
				nodes.addAll(next);
			i++;
			if(i >= nodes.size())
				break;	
		}
		paths.add(new Path<Integer>(nodes));
	}

	private List<Node<Integer>> getNextNode(List<Node<Integer>> list) {
		List<AbstractPath<Integer>> candidates = new ArrayList<AbstractPath<Integer>>();
		List<AbstractPath<Integer>> terminate = new ArrayList<AbstractPath<Integer>>();
		for(AbstractPath<Integer> path : original)
			if(!visited.contains(path)) 
				if(path.from() == list.get(0))
					if(graph.isFinalNode(path.to()))
						terminate.add(path);
					else
						candidates.add(path);	

		List<Node<Integer>> nodes;
		if(candidates.isEmpty() &&  terminate.isEmpty())
			return null;
		
		nodes = getNodes(list, candidates);
		if(nodes != null)
			return nodes;
		else
			return getNodes(list, terminate);
	}

	private List<Node<Integer>> getNodes(List<Node<Integer>> list, List<AbstractPath<Integer>> candidates) {
		boolean find;
		for(AbstractPath<Integer> path : candidates) {
			find = true;
			List<Node<Integer>> lst = getPathNodes(path);
			
			for(int i = 0; i < list.size() && i < lst.size(); i++)
				if(list.get(i) != lst.get(i)) {
					find = false;
					break;
				}
				
			if(find) {
				visited.add(path);
				if(lst.size() > list.size())
					return lst.subList(list.size(), lst.size());
			}
			
		}
		return null;
	}

	private List<Node<Integer>> getPathNodes(AbstractPath<Integer> path) {
		List<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		for(Node<Integer> node : path) 
			nodes.add(node);
		return nodes;
	}
	
	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor<Integer> {
		
		private Deque<Node<Integer>> pathNodes;
		
		public SimplePathCoverageVisitor(Graph<Integer> graph) {
			this.graph = graph;
			pathNodes = new LinkedList<Node<Integer>>();
		}
			
		@Override
		public boolean visit(Node<Integer> node) {
			if(node == begin) {
				pathNodes.addLast(node);
				simplePath = new Path<Integer>(pathNodes);
				return false;
			}
			pathNodes.addLast(node);				 
			return true;
		}
		
		@Override
		public void endVisit(Node<Integer> node) {
			pathNodes.removeLast();
		}
	}

}