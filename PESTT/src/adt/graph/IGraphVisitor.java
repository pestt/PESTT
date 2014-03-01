package adt.graph;

public interface IGraphVisitor {

	public void visitEdge(Edge edge);

	public boolean visit(Edge edge);

	public void endVisit(Edge edge);

	public void visitNode(Node node);

	public boolean visit(Node node);

	public void endVisit(Node node);

	public void visitGraph(Graph graph);

	public boolean visit(Graph graph);

	public void endVisit(Graph graph);

	public boolean alreadyVisited(Node node);
}
