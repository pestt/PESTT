package graphvisitors;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;


public interface IGraphVisitor<V> {
	
	public void visitEdge(Edge<V> edge);
	public boolean visit(Edge<V> edge);
	public void endVisit (Edge<V> edge);
	public void visitNode(Node<V> node);
	public boolean visit(Node<V> node);
	public void endVisit(Node<V> node);
	public void visitGraph(Graph<V> graph);
	public boolean visit(Graph<V> graph);
	public void endVisit(Graph<V> graph);
}
