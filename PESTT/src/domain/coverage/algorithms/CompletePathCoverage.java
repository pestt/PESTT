package domain.coverage.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.InfinitePath;
import adt.graph.Node;
import adt.graph.Path;
import domain.graph.visitors.BreadthFirstGraphVisitor;

public class CompletePathCoverage<V extends Comparable<V>> implements ICoverageAlgorithms<V> {

	private Graph<V> graph;
	
	public CompletePathCoverage(Graph<V> graph) {
		this.graph = graph;
	}

	public Set<Path<V>> getTestRequirements() {
		CompletePathCoverageVisitor cpcv = new CompletePathCoverageVisitor();
		cpcv.visitGraph(graph);
		return cpcv.getPaths();
	}
	
	private class CompletePathCoverageVisitor extends BreadthFirstGraphVisitor<V> {
		
		private static final int SPECIAL_NODE = -1;
		private Set<Path<V>> paths;
		private Set<Path<V>> temp;
		private Set<Path<V>> completePaths;
		
		public CompletePathCoverageVisitor() {
			paths = new TreeSet<Path<V>>();
			temp = new TreeSet<Path<V>>();
			completePaths = new TreeSet<Path<V>>();
		}
		
		private Set<Path<V>> getPaths() {
			return completePaths;
		}
		
		@Override
		public boolean visit(Graph<V> graph) {
			for(Node<V> node : graph.getNodes())
				if(graph.isInitialNode(node))
					paths.add(new Path<V>(node));

			while(!paths.isEmpty()) {
				for(Path<V> path : paths)
					addNodes(path);
		
				if(temp.isEmpty() && completePaths.isEmpty()) {
					completePaths = paths;
					return true;
				}
					
				paths.clear();
				for(Path<V> path : temp)
					paths.add(path);
					temp.clear();
			}
			insertSpecialPaths();
			return true;
		}
		
		@SuppressWarnings("unchecked")
		private void insertSpecialPaths() {
			Map<Path<V>, InfinitePath<V>> replace = new LinkedHashMap<Path<V>, InfinitePath<V>>();
			for(Path<V> path : completePaths) {
				List<Integer> index = new ArrayList<Integer>();
				boolean inLoop = false;
				for(Node<V> node : path) {
					if(getNumberOfOccurrences(path, node) == 2 && inLoop == false) {
						inLoop = true;
						int i = getIndexSpecialNode(path, node);
						if(!index.contains(i))
							index.add(i);
					} else if(getNumberOfOccurrences(path, node) == 2)
						inLoop = true;
					else
						inLoop = false;
				}
				if(!index.isEmpty()) {
					InfinitePath<V> infinite = null;
					for(Node<V> node : path)
						if(infinite == null)
							infinite = new InfinitePath<V>(node);
						else
							infinite.addNode(node);
					Node<Integer> n = new Node<Integer>(SPECIAL_NODE);
					for(int x : index) {
						infinite.addNode(x, (Node<V>) n);
						infinite.addNode(x, infinite.getPathNodes().get(x + 1));
					}
							
					replace.put(path, infinite);
				}			
			}
			Set<Entry<Path<V>, InfinitePath<V>>> set = replace.entrySet(); // the node properties.
			Iterator<Entry<Path<V>, InfinitePath<V>>> iterator = set.iterator(); 
			while(iterator.hasNext()) {
				Entry<Path<V>, InfinitePath<V>> entry = iterator.next();
				Path<V> p = entry.getKey();
				completePaths.remove(p);
				completePaths.add(entry.getValue());
			}
		}

		private void addNodes(Path<V> path) {
			Node<V> finalNode = path.getPathNodes().get(path.getPathNodes().size() - 1);
			for(Edge<V> edge : graph.getNodeEdges(finalNode)) {
				Path<V> aux = path.clone();
				aux.addNode(edge.getEndNode());
				if(graph.isFinalNode(edge.getEndNode())) 
					completePaths.add(aux);
				else if(!isLop(aux))
					temp.add(aux);
			}
		}
		
		public boolean isLop(Path<V> path) {
			for(Node<V> node : path) 
				if(getNumberOfOccurrences(path, node) > 2)
					return true;
			return false;
		}

		private int getNumberOfOccurrences(Path<V> path, Node<V> node) {
			int occurrences = 0;
			for(Node<V> n : path)
				if(n == node)
					occurrences++;
			return occurrences;
		}
		
		private int getIndexSpecialNode(Path<V> path, Node<V> node) {
			int index = -1;
			for(int i = 0; i < path.getPathNodes().size(); i++) {
				Node<V> n = path.getPathNodes().get(i);
				if(n == node)
					index = i;
			}
			return index;
		}
	};
}