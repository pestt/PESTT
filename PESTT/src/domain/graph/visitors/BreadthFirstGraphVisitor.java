package domain.graph.visitors;

import java.util.LinkedList;
import java.util.List;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;



public class BreadthFirstGraphVisitor<V extends Comparable<V>> implements IGraphVisitor<V> {
	
	private Graph<V> graph;
	private List<Node<V>> visitedNodes = new LinkedList<Node<V>>();
	
	@Override
	public void visitEdge(Edge<V> edge) {
		if(visit(edge)) {
			//visit
			endVisit(edge);
		}
	}

	@Override
	public boolean visit(Edge<V> edge) {
		return true;
	}

	@Override
	public void endVisit(Edge<V> edge) {
		// does nothing!
	}

	@Override
	public void visitNode(Node<V> node) {
		// visit if not already visited;
		if(!visitedNodes.contains(node)) {
			visitedNodes.add(node);
			if(visit(node)) {
				endVisit(node);
				for(Edge<V> edge : graph.getNodeEdges(node)) {
					edge.accept(this);
					edge.getEndNode().accept(this);
				}
			}
		}
	}

	@Override
	public boolean visit(Node<V> node) {
		return true;
	}

	@Override
	public void endVisit(Node<V> node) {
		// does nothing!
	}
	
	@Override
	public void visitGraph(Graph<V> graph) {
		this.graph = graph;
		if (visit(graph)) {
			for(Node<V> node : graph.getInitialNodes())
				node.accept(this);
			endVisit(graph);
		}
	}

	@Override
	public boolean visit(Graph<V> graph) {
		return true;
	}

	@Override
	public void endVisit(Graph<V> graph) {
		// does nothing!
	}
}