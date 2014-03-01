package domain.dot.processor;

import main.activator.Activator;
import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;

public class DotGraphVisitor extends DepthFirstGraphVisitor {

	private StringBuilder dotString = null;
	private Graph graph;

	public DotGraphVisitor() {
		dotString = new StringBuilder();
	}

	@Override
	public void endVisit(Edge edge) {
		dotString.append(edge.getBeginNode() + " -> " + edge.getEndNode()
				+ "\n");
	}

	@Override
	public void endVisit(Node node) {
		if (Activator.getDefault().getSourceGraphController().numberOfNodes() == 1)
			dotString.append(node.getValue()
					+ " [style=filled, fillcolor=violet]\n");
		else if (graph.isInitialNode(node))
			dotString.append(node.getValue()
					+ " [style=filled, fillcolor=green]\n");
		else if (graph.isFinalNode(node))
			dotString.append(node.getValue()
					+ " [style=filled, fillcolor=red]\n");
	}

	@Override
	public boolean visit(Graph graph) {
		this.graph = graph;
		return true;
	}

	public String getDotString() {
		return dotString.toString();
	}
}