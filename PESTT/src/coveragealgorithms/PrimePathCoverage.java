package coveragealgorithms;

import graphvisitors.BreadthFirstGraphVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;

public class PrimePathCoverage<V> extends BreadthFirstGraphVisitor<V> implements ICoverageAlgorithms<V> {

	private List<Path<V>> paths;
	private List<Path<V>> temp;
	private List<Path<V>> primePaths;
	private Graph<V> graph;

	public PrimePathCoverage() {
		paths = new LinkedList<Path<V>>();
		temp = new LinkedList<Path<V>>();
		primePaths = new LinkedList<Path<V>>();
	}

	@Override
	public boolean visit(Graph<V> graph) {
		this.graph = graph;
		for(Node<V> node : graph.getNodes())
			if(!graph.isFinalNode(node))
				paths.add(new Path<V>(node));
			else
				primePaths.add(new Path<V>(node));

		while(!paths.isEmpty()) {
			for(Path<V> path : paths)
				addNodes(path);

			paths.clear();
			for(Path<V> path : temp)
				paths.add(path);
			temp.clear();
		}
		getPrimePaths();
		return true;
	}

	private void addNodes(Path<V> path) {
		Node<V> firstNode = path.getPathNodes().get(0);
		Node<V> finalNode = path.getPathNodes().get(path.getPathNodes().size() - 1);
		for(Edge<V> edge : graph.getNodeEdges(finalNode)) {
			Path<V> aux = path.clone();
			if(graph.isFinalNode(edge.getEndNode()) || (firstNode == edge.getEndNode())) {
				aux.addNode(edge.getEndNode());
				primePaths.add(aux);
			} else if(!aux.containsNode(edge.getEndNode())) {
				aux.addNode(edge.getEndNode());
				temp.add(aux);
			}
		}
	}
	
	public void getPrimePaths() {
		temp.clear();
		for(Path<V> path : primePaths) 
			temp.add(path);				
				
		List<Integer> toRemove = new ArrayList<Integer>();		
		for(int i = 0; i < primePaths.size(); i++)
			for(int j = 0; j < temp.size(); j++)
				if(i != j && primePaths.get(i).isSubPath(temp.get(j))) 
					if(!toRemove.contains(j)) {
						toRemove.add(j);
						break;
					}

		Collections.sort(toRemove);
		Collections.reverse(toRemove);
		for(int index : toRemove) 
			primePaths.remove(index);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Path<V>> getTestRequirements() {
		primePaths = new SortPaths().sort(primePaths); // sort the paths.
		return primePaths;
	}

}