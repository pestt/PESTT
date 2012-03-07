package ui.source;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

import ui.editor.ActiveEditor;
import ui.editor.Line;
import domain.constants.Colors;
import domain.constants.Description;
import domain.constants.Layer;
import domain.constants.MarkersType;
import domain.coverage.instrument.ICoverageData;

public class GraphInformation {

	private adt.graph.Graph<Integer> sourceGraph;
	private ui.source.Graph layoutGraph;
	private ActiveEditor editor;
	
	public GraphInformation(ui.source.Graph layoutGraph) {
		this.layoutGraph = layoutGraph;
	}
	
	public void setLayerInformation(Layer layer) {
		editor = Activator.getDefault().getEditorController().getActiveEditor();
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph(); // set the sourceGraph.
		switch(layer) {
			case EMPTY:
				clear();
				break;
			case GUARDS:
				addInformationToLayer1();
				break;
			case INSTRUCTIONS:
				addInformationToLayer2();
				break;
		}
	}
	
	private void clear() {
		sourceGraph.selectMetadataLayer(Layer.EMPTY.getLayer()); // change to the empty layer.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes())  // search in the sourceGraph for all node.
			for(adt.graph.Edge<Integer> edge : sourceGraph.getNodeEdges(node))  // search in the sourceGraph for all edges.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // search in the layoutGraph for all edges.
					if(gconnection.getData().equals(edge)) { // when they match.
						gconnection.setText(Description.EMPTY); // clear the visible information.
						break;
					}
		new ActiveEditor().deleteALLMarkers(); // removes the marks in the editor.
	}
	
	private void addInformationToLayer1() {
		setLayerInformation(Layer.EMPTY); // clean previous informations.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes())  // search in the sourceGraph for all node.
			for(adt.graph.Edge<Integer> edge : sourceGraph.getNodeEdges(node))   // search in the sourceGraph for all edges.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // search in the layoutGraph for all edges.
					if(gconnection.getData().equals(edge)) { // when they match.
						sourceGraph.selectMetadataLayer(Layer.GUARDS.getLayer()); // change to the layer with the cycles information.
						String info = (String) sourceGraph.getMetadata(edge); // get the information.
						if(info != null)  // if it have information
							gconnection.setText(info); // set the information to the edge.
						break;
					}
	}
	

	@SuppressWarnings("unchecked")
	private void addInformationToLayer2() {
		if(!layoutGraph.getSelected().isEmpty()) { // verify if there are nodes selected.
			editor.deleteALLMarkers(); // removes the marks in the editor.
			for(GraphItem item : layoutGraph.getSelected()) // through all graph items.
				if(item instanceof GraphNode) { // verify if is a GraphNode.
					adt.graph.Node<Integer> node = sourceGraph.getNode(Integer.parseInt(item.getText())); // get the node.
					sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
					HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
					if(map != null) {
						List<ASTNode> nodes = getASTNodes(map);
						if(nodes != null) 
							regionToSelect(nodes, MarkersType.LINK_MARKER); // select the area in the editor.
						else 
							editor.deleteALLMarkers(); // removes the marks in the editor.
					}
				}
		} else 
			editor.deleteALLMarkers(); // removes the marks in the editor.
	}
	
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodesInstructions = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodesInstructions.add(entry.getKey());
		return nodesInstructions;
	}
	
	private void regionToSelect(List<ASTNode> info, String markerType) {
		int start = findStartPosition(info); // get the start position.
		ASTNode instructions = info.get(0); // the first element of the list.
		switch(instructions.getNodeType()) {
			case ASTNode.IF_STATEMENT:
			case ASTNode.DO_STATEMENT:
				editor.createMarker(markerType, start, getLength(start, instructions.getStartPosition(), 2)); // select the if or do words.
				break;
			case ASTNode.FOR_STATEMENT:
			case ASTNode.ENHANCED_FOR_STATEMENT:
				editor.createMarker(markerType, start, getLength(start, instructions.getStartPosition(), 3)); // select the for word.
				break;
			case ASTNode.SWITCH_STATEMENT:
				editor.createMarker(markerType, start, getLength(start, instructions.getStartPosition(), 6)); // select the switch word.
				break;
			case ASTNode.WHILE_STATEMENT:
				editor.createMarker(markerType, start, getLength(start, instructions.getStartPosition(), 5)); // select the while word.
				break;
			default:
				editor.createMarker(markerType, start, getLength(start, info.get(info.size() - 1).getStartPosition(), info.get(info.size() - 1).getLength())); // select the block of instructions associated to the selected node.
				break;
		}
	}
	
	public int findStartPosition(List<ASTNode> info) {
		int start = info.get(0).getStartPosition(); // the start position of the first element in the list.
		for(ASTNode instructions : info)  // through all.
			if(instructions.getStartPosition() < start) // if the start position of the node begins first than the position stored.
				start = instructions.getStartPosition(); // update the start position.
		return start;
	}
	
	private int getLength(int start, int nodeStart, int len) {
		return ((nodeStart + len) - start); // calculates the length.
	}
	
	@SuppressWarnings("unchecked")
	public void setVisualCoverageStatus(ICoverageData data) {
		editor.deleteALLMarkers(); // removes the marks in the editor.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes()) {
			sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				List<ASTNode> nodesInstructions = getASTNodes(map);
				Entry<ASTNode, Line> entry = map.entrySet().iterator().next();	
				String colorStatus = data.getLineStatus(entry.getValue().getStartLine());
				if(colorStatus.equals(Colors.GRENN_ID))
					regionToSelect(nodesInstructions, MarkersType.FULL_COVERAGE_MARKER); // select the area in the editor.
				else if(colorStatus.equals(Colors.RED_ID)) 
					regionToSelect(nodesInstructions, MarkersType.NO_COVERAGE_MARKER); // select the area in the editor.
			}
		}
	}
}