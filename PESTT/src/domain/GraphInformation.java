package domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.zest.core.widgets.GraphNode;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import domain.constants.Layer;

public class GraphInformation {
	
	public void addInformationToLayer0(Graph<Integer> sourceGraph, Node<Integer> sourceNode, GraphNode gnode) {
		sourceGraph.selectMetadataLayer(Layer.EMPTY.getLayer()); // change to node association layer.
		sourceGraph.addMetadata(sourceNode, gnode); // associate the the nodes of sourceGraph and layoutGraph.
	}
	
	public void addInformationToLayer1(Graph<Integer> sourceGraph, Edge<Integer> edge, String info) {
		sourceGraph.selectMetadataLayer(Layer.GUARDS.getLayer()); // change to cycle layer.
		sourceGraph.addMetadata(edge, info); // add information to edge.
	}
	
	@SuppressWarnings("unchecked")
	public void addInformationToLayer2(Graph<Integer> sourceGraph, Node<Integer> node, ASTNode instructions, CompilationUnit unit) {
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // change to instruction layer.
		Map<ASTNode, Line> nodeInstructions = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // contains the instructions associated to the node. 
		if(nodeInstructions == null) { 
    		nodeInstructions = new LinkedHashMap<ASTNode, Line>(); 
    		sourceGraph.addMetadata(node, nodeInstructions); // add information to metadata.
    	}
		int startLine = unit.getLineNumber(instructions.getStartPosition());
		int endLine = unit.getLineNumber(instructions.getStartPosition() + instructions.getLength());
		int startPosition = instructions.getStartPosition();
		int endPosition = instructions.getStartPosition() + instructions.getLength();
		Line line = new Line(startLine, endLine, startPosition, endPosition);
    	nodeInstructions.put(instructions, line); // add information to node. 
	}
}