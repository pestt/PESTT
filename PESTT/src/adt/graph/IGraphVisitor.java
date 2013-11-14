package adt.graph;


public interface IGraphVisitor<V extends Comparable<V>> {

	public void visitEdge(Edge<V> edge);

	public boolean visit(Edge<V> edge);

	public void endVisit(Edge<V> edge);

	public void visitNode(Node<V> node);

	public boolean visit(Node<V> node);

	public void endVisit(Node<V> node);

	public void visitGraph(Graph<V> graph);

	public boolean visit(Graph<V> graph);

	public void endVisit(Graph<V> graph);

	public boolean alreadyVisited(Node<V> node);
}
