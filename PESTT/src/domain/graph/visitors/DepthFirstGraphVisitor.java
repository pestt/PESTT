package domain.graph.visitors;

import java.util.HashSet;
import java.util.Set;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;

public class DepthFirstGraphVisitor<V extends Comparable<V>> implements
		IGraphVisitor<V> {

	private Set<Node<V>> visitedNodes;
	protected Graph<V> graph;

	public DepthFirstGraphVisitor() {
		visitedNodes = new HashSet<Node<V>>();
	}

	@Override
	public void visitEdge(Edge<V> edge) {
		if (visit(edge)) {
			// visit
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
		if (visit(node)) {
			visitedNodes.add(node);
			for (Edge<V> edge : graph.getNodeEdges(node)) {
				edge.accept(this);
				edge.getEndNode().accept(this);
			}
			visitedNodes.remove(node);
			endVisit(node);
		}
	}

	@Override
	public boolean visit(Node<V> node) {
		return !alreadyVisited(node);
	}

	@Override
	public void endVisit(Node<V> node) {
		// does nothing!
	}

	@Override
	public void visitGraph(Graph<V> graph) {
		if (visit(graph)) {
			this.graph = graph;
			for (Node<V> node : graph.getInitialNodes())
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

	@Override
	public boolean alreadyVisited(Node<V> node) {
		return visitedNodes.contains(node);
	}
}