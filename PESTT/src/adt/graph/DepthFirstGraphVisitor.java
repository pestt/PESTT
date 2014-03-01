package adt.graph;

import java.util.HashSet;
import java.util.Set;

public class DepthFirstGraphVisitor implements IGraphVisitor {

	private Set<Node> visitedNodes;
	protected Graph graph;

	public DepthFirstGraphVisitor() {
		visitedNodes = new HashSet<Node>();
	}

	@Override
	public void visitEdge(Edge edge) {
		if (visit(edge)) {
			//visit
			endVisit(edge);
		}
	}

	@Override
	public boolean visit(Edge edge) {
		return true;
	}

	@Override
	public void endVisit(Edge edge) {
		// does nothing!
	}

	@Override
	public void visitNode(Node node) {
		if (visit(node)) {
			visitedNodes.add(node);
			for (Edge edge : graph.getNodeEdges(node)) {
				edge.accept(this);
				edge.getEndNode().accept(this);
			}
			visitedNodes.remove(node);
			endVisit(node);
		}
	}

	@Override
	public boolean visit(Node node) {
		return !alreadyVisited(node);
	}

	@Override
	public void endVisit(Node node) {
		// does nothing!
	}

	@Override
	public void visitGraph(Graph graph) {
		if (visit(graph)) {
			this.graph = graph;
			for (Node node : graph.getInitialNodes())
				node.accept(this);
			endVisit(graph);
		}
	}

	@Override
	public boolean visit(Graph graph) {
		return true;
	}

	@Override
	public void endVisit(Graph graph) {
		// does nothing!
	}

	@Override
	public boolean alreadyVisited(Node node) {
		return visitedNodes.contains(node);
	}
}