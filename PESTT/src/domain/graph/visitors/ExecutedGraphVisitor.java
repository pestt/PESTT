package domain.graph.visitors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import domain.constants.Colors;
import domain.constants.Layer;
import domain.coverage.instrument.ICoverageData;

public class ExecutedGraphVisitor<V extends Comparable<V>> extends DepthFirstGraphVisitor<V> {

	private Graph<V> graph;
	private Graph<V> executedGraph;
	private ICoverageData data;

	public ExecutedGraphVisitor(ICoverageData data) {
		this.data = data;
		executedGraph = new Graph<V>(); 
	}
	
	private ASTNode loopBody(ASTNode node) {
		switch(node.getNodeType()) {
			case ASTNode.WHILE_STATEMENT:
				return ((WhileStatement) node).getBody();
			case ASTNode.DO_STATEMENT:
				return ((DoStatement) node).getBody();
			case ASTNode.FOR_STATEMENT:
				return ((ForStatement) node).getBody();
			case ASTNode.ENHANCED_FOR_STATEMENT:
				return ((EnhancedForStatement) node).getBody();
			default:
				return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(Node<V> node) {
		for(Edge<V> edge : graph.getNodeEdges(node)) {
			graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
			Node<V> finalEdgeNode = edge.getEndNode();
			Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(finalEdgeNode); // get the information in this layer to this node.
			if(map != null) {
				int line = map.entrySet().iterator().next().getValue().getStartLine();
				map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(node); // get the information in this layer to this node.
				ASTNode aNode = map.entrySet().iterator().next().getKey();
				if(data.getLineStatus(line).equals(Colors.GRENN_ID)) {
					executedGraph.addNode(finalEdgeNode);
					if(executedGraph.containsNode(edge.getBeginNode())) {
						ASTNode loopBody = loopBody(aNode);
						if(loopBody != null) {
							LoopBreakGraphVisitor<V> visitor = new LoopBreakGraphVisitor<V>(data, graph);
							loopBody.accept(visitor);
							graph.selectMetadataLayer(Layer.GUARDS.getLayer()); // select the layer to get the information.
							executedGraph.addEdge(edge);
						}
						else
							executedGraph.addEdge(edge);
					}	
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(Graph<V> graph) {
		this.graph = graph;
		Node<V> initialNode = graph.getInitialNodes().iterator().next();
		graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(initialNode); // get the information in this layer to this node.
		int line = map.entrySet().iterator().next().getValue().getStartLine();
		if(data.getLineStatus(line).equals(Colors.GRENN_ID)) {
			executedGraph.addInitialNode(initialNode);
			return true;
		}
		return false;
	}
	
	public Graph<V> getExecutedGraph() {
		return executedGraph;
	}
}