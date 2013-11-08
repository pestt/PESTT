package domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import domain.constants.Layer;

public class GraphInformation {
	
	
	/**
	 * Associates the node in the abstract graph representation 
	 * and the correspondent one in the layout graph.
	 * * This information is stored in abstract graph meta-data Layer.EMPTY.
	 * 
	 * @param sourceGraph - The abstract representation of the graph.
	 * @param sourceNode - The abstract representation of the node in the abstract graph.
	 * @param layoutNode - The node representation in the layout graph.
	 */
	public void addInformationToLayer0(Graph<Integer> sourceGraph, Node<Integer> sourceNode, GraphNode layoutNode) {
		sourceGraph.selectMetadataLayer(Layer.EMPTY.getLayer()); // change to node association layer.
		sourceGraph.addMetadata(sourceNode, layoutNode); // associate the the nodes of sourceGraph and layoutGraph.
	}
	
	/**
	 * Associates the program instructions to the corresponding node in the abstract grapg.
	 * This information is stored in abstract graph meta-data Layer.INSTRUCTIONS.
	 * 
	 * @param sourceGraph - The abstract representation of the graph.
	 * @param sourceNode - The abstract representation of the node in the abstract graph.
	 * @param instructions - The program instructions associated to the sourceNode.
	 * @param unit - The compilation unit. 
	 */
	@SuppressWarnings("unchecked")
	public void addInformationToLayer1(Graph<Integer> sourceGraph, Node<Integer> sourceNode, ASTNode instructions, CompilationUnit unit) {
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // change to instruction layer.
		Map<ASTNode, Line> nodeInstructions = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(sourceNode); // contains the instructions associated to the node. 
		if(nodeInstructions == null) { 
    		nodeInstructions = new LinkedHashMap<ASTNode, Line>(); 
    		sourceGraph.addMetadata(sourceNode, nodeInstructions); // add information to metadata.
    	}
		int startLine = unit.getLineNumber(instructions.getStartPosition());
		int endLine = unit.getLineNumber(instructions.getStartPosition() + instructions.getLength());
		int startPosition = instructions.getStartPosition();
		int endPosition = instructions.getStartPosition() + instructions.getLength();
		Line line = new Line(startLine, endLine, startPosition, endPosition);
    	nodeInstructions.put(instructions, line); // add information to node. 
	}
	
	/**
	 * Add guards information to abstract graph edge.
	 * This information is stored in abstract graph meta-data Layer.GUARDS.
	 * 
	 * @param sourceGraph - The abstract representation of the graph.
	 * @param sourceEdge - The abstract representation of the edge in the abstract graph.
	 * @param info - The guards information.
	 */
	public void addInformationToLayer2(Graph<Integer> sourceGraph, Edge<Integer> sourceEdge, String info) {
		sourceGraph.selectMetadataLayer(Layer.GUARDS.getLayer()); // change to cycle layer.
		sourceGraph.addMetadata(sourceEdge, info); // add information to edge.
	}
}