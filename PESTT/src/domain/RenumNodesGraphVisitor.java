package domain;

import java.util.HashSet;
import java.util.Set;

import adt.graph.DepthFirstGraphVisitor;
import adt.graph.Node;

public class RenumNodesGraphVisitor extends DepthFirstGraphVisitor<Integer> {

	private int nodeNum = 0;
	private Set<Node<Integer>> visitedNodes; // nodes must be visited just one
												// time

	public RenumNodesGraphVisitor() {
		visitedNodes = new HashSet<Node<Integer>>();
	}

	@Override
	public boolean visit(Node<Integer> node) {
		if (!visitedNodes.contains(node)) {
			visitedNodes.add(node);
			node.setValue(nodeNum);
			nodeNum++;
			return true;
		}
		return false;
	}
}