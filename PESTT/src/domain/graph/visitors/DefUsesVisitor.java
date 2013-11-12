package domain.graph.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Node;
import domain.ast.visitors.DefUsesCollector;
import domain.constants.Layer;

public class DefUsesVisitor<V extends Comparable<V>> extends
		DepthFirstGraphVisitor<Integer> {

	private static final String THIS = "this.";
	private Set<Node<Integer>> visitedNodes; // nodes must be visited just one time.
	private DefUsesCollector visitor;

	public DefUsesVisitor() {
		visitedNodes = new HashSet<Node<Integer>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(Node<Integer> node) {
		if (!visitedNodes.contains(node)) {
			graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer());
			visitedNodes.add(node);
			HashMap<ASTNode, Line> nodeInstructions = (HashMap<ASTNode, Line>) graph
					.getMetadata(node);
			Set<String> defs = new TreeSet<String>();
			Set<String> uses = new TreeSet<String>();
			if (graph.isInitialNode(node))
				addToDefs(defs);
			visitor = new DefUsesCollector(defs, uses);
			if (nodeInstructions != null) {
				List<ASTNode> astNodes = getASTNodes(nodeInstructions);
				if (!isProgramStatement(astNodes))
					for (ASTNode ast : astNodes)
						ast.accept(visitor);
			}
			if (!defs.isEmpty() || !uses.isEmpty()) {
				List<List<String>> defuses = getDefUses(defs, uses);
				Activator.getDefault().getDefUsesController()
						.put(node, defuses);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(Edge<Integer> edge) {
		HashMap<ASTNode, Line> nodeInstructions = (HashMap<ASTNode, Line>) graph
				.getMetadata(edge.getBeginNode());
		Set<String> defs = new TreeSet<String>();
		Set<String> uses = new TreeSet<String>();
		visitor = new DefUsesCollector(defs, uses);
		if (nodeInstructions != null) {
			List<ASTNode> astNodes = getASTNodes(nodeInstructions);
			if (isProgramStatement(astNodes))
				for (ASTNode ast : astNodes)
					ast.accept(visitor);
		}
		if (!defs.isEmpty() || !uses.isEmpty()) {
			List<List<String>> defuses = getDefUses(defs, uses);
			Activator.getDefault().getDefUsesController().put(edge, defuses);
		}
		return false;
	}

	public List<List<String>> getDefUses(Set<String> defsSet,
			Set<String> usesSet) {
		List<List<String>> defuses = new LinkedList<List<String>>();
		List<String> defs = new LinkedList<String>();
		List<String> uses = new LinkedList<String>();
		for (String str : defsSet)
			defs.add(str);
		for (String str : usesSet)
			uses.add(str);
		defuses.add(defs);
		defuses.add(uses);
		return defuses;
	}

	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for (Entry<ASTNode, Line> entry : map.entrySet())
			nodes.add(entry.getKey());
		return nodes;
	}

	private boolean isProgramStatement(List<ASTNode> ast) {
		switch (ast.get(0).getNodeType()) {
		case ASTNode.IF_STATEMENT:
		case ASTNode.DO_STATEMENT:
		case ASTNode.FOR_STATEMENT:
		case ASTNode.ENHANCED_FOR_STATEMENT:
		case ASTNode.SWITCH_STATEMENT:
		case ASTNode.WHILE_STATEMENT:
			return true;
		default:
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private void addToDefs(Set<String> defs) {
		List<EnumDeclaration> enumDeclarations = Activator.getDefault()
				.getSourceGraphController().getEnumClassAttributes();
		List<VariableDeclarationFragment> attributes = Activator.getDefault()
				.getSourceGraphController().getClassAttributes();
		List<SingleVariableDeclaration> params = Activator.getDefault()
				.getSourceGraphController().getMethodParameters();
		for (EnumDeclaration enumDeclaration : enumDeclarations) {
			List<EnumConstantDeclaration> enumFields = enumDeclaration
					.enumConstants();
			for (EnumConstantDeclaration enumField : enumFields)
				defs.add(enumDeclaration.getName().toString() + "."
						+ enumField.toString());
		}
		for (VariableDeclarationFragment attribute : attributes)
			defs.add(THIS + attribute.getName().toString());
		for (SingleVariableDeclaration param : params)
			defs.add(param.getName().toString());
	}
}