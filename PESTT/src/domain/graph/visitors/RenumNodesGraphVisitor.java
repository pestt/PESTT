package domain.graph.visitors;

import adt.graph.Node;

public class RenumNodesGraphVisitor extends DepthFirstGraphVisitor<Integer> {

	private int nodeNum = 0;

	@Override
	public boolean visit(Node<Integer> node) {
		if(super.visit(node)) {
			node.setValue(nodeNum);
			nodeNum++;
			return true;
		}
		return false;
	}
}