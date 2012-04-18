package domain.coverage.algorithms;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import main.activator.Activator;

import adt.graph.AbstractPath;
import adt.graph.Graph;
import adt.graph.Node;
import domain.graph.visitors.DepthFirstGraphVisitor;

public class AllDuPathCoverage <V extends Comparable<V>> implements ICoverageAlgorithms<V> {
	
	private Graph<V> graph;
	private Set<AbstractPath<V>> allDuPaths;
	private Deque<Node<V>> pathNodes;

	
	public AllDuPathCoverage(Graph<V> graph) {
		this.graph = graph;
		allDuPaths = new TreeSet<AbstractPath<V>>();
		pathNodes = new LinkedList<Node<V>>();
	}
	
	


	public Set<AbstractPath<V>> getTestRequirements() {
		for(Node<V> node : graph.getNodes()) {
			SimplePathCoverageVisitor ppc = new SimplePathCoverageVisitor(graph);
			node.accept(ppc);
		}
		return allDuPaths;
	}
	
	private class SimplePathCoverageVisitor extends DepthFirstGraphVisitor<V> {
		public SimplePathCoverageVisitor(Graph<V> graph) {
			this.graph = graph;
			pathNodes.clear();
		}
	}

}