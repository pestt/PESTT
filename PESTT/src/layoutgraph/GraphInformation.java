package layoutgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

import sourcegraph.Graph;
import view.GraphsCreator;
import constants.Colors_ID;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Layer_ID;
import constants.Markers_ID;
import coverage.ICoverageData;
import editor.ActiveEditor;
import editor.Line;

public class GraphInformation {

	private sourcegraph.Graph<Integer> sourceGraph;
	private layoutgraph.Graph layoutGraph;
	private ActiveEditor editor;
	
	@SuppressWarnings("unchecked")
	public GraphInformation() {
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM); // set the sourceGraph.
		layoutGraph =  (layoutgraph.Graph) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.LAYOUT_GRAPH_NUM); // set the layoutGraph.
	}
	
	public void setLayerInformation(int layer) {
		switch(layer) {
			case Layer_ID.EMPTY:
				clear();
				break;
			case Layer_ID.GUARDS:
				addInformationToLayer1();
				break;
			case Layer_ID.INSTRUCTIONS:
				addInformationToLayer2();
				break;
		}
	}
	
	private void clear() {
		sourceGraph.selectMetadataLayer(Layer_ID.EMPTY); // change to the empty layer.
		for(sourcegraph.Node<Integer> node : sourceGraph.getNodes())  // search in the sourceGraph for all node.
			for(sourcegraph.Edge<Integer> edge : sourceGraph.getNodeEdges(node))  // search in the sourceGraph for all edges.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // search in the layoutGraph for all edges.
					if(gconnection.getData().equals(edge)) // when they match.
						gconnection.setText(Description_ID.EMPTY); // clear the visible information.
		new ActiveEditor().deleteALLMarkers(); // removes the marks in the editor.
	}
	
	private void addInformationToLayer1() {
		setLayerInformation(Layer_ID.EMPTY); // clean previous informations.
		for(sourcegraph.Node<Integer> node : sourceGraph.getNodes())  // search in the sourceGraph for all node.
			for(sourcegraph.Edge<Integer> edge : sourceGraph.getNodeEdges(node))   // search in the sourceGraph for all edges.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // search in the layoutGraph for all edges.
					if(gconnection.getData().equals(edge)) { // when they match.
						sourceGraph.selectMetadataLayer(Layer_ID.GUARDS); // change to the layer with the cycles information.
						String info = (String) sourceGraph.getMetadata(edge); // get the information.
						if(info != null)  // if it have information
							gconnection.setText(info); // set the information to the edge.
					}
	}
	
	@SuppressWarnings("unchecked")
	private void addInformationToLayer2() {
		if(!layoutGraph.getSelected().isEmpty()) { // verify if there are nodes selected.
			editor.deleteALLMarkers(); // removes the marks in the editor.
			for(GraphItem item : layoutGraph.getSelected()) // through all graph items.
				if(item instanceof GraphNode) { // verify if is a GraphNode.
					sourcegraph.Node<Integer> node = sourceGraph.getNode(Integer.parseInt(item.getText())); // get the node.
					sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
					HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
					if(map != null) {
						ArrayList<ASTNode> nodes = getASTNodes(map);
						if(nodes != null) 
							regionToSelect(nodes, editor, Markers_ID.LINK_MARKER); // select the area in the editor.
						else 
							editor.deleteALLMarkers(); // removes the marks in the editor.
					}
				}
		} else 
			editor.deleteALLMarkers(); // removes the marks in the editor.
	}
	
	private ArrayList<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		ArrayList<ASTNode> nodesInstructions = new ArrayList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodesInstructions.add(entry.getKey());
		return nodesInstructions;
	}
	
	private void regionToSelect(ArrayList<ASTNode> info, ActiveEditor editor, String markerType) {
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
	
	public int findStartPosition(ArrayList<ASTNode> info) {
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
		for(sourcegraph.Node<Integer> node : sourceGraph.getNodes()) {
			sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				ArrayList<ASTNode> nodesInstructions = getASTNodes(map);
				Entry<ASTNode, Line> entry = map.entrySet().iterator().next();	
				String colorStatus = data.getLineStatus(entry.getValue().getStartLine());
				if(colorStatus.equals(Colors_ID.GRENN_ID))
					regionToSelect(nodesInstructions, editor, Markers_ID.FULL_COVERAGE_MARKER); // select the area in the editor.
				else if(colorStatus.equals(Colors_ID.YELLOW_ID))
					regionToSelect(nodesInstructions, editor, Markers_ID.FULL_COVERAGE_MARKER); // select the area in the editor.
				else if(colorStatus.equals(Colors_ID.RED_ID)) 
					regionToSelect(nodesInstructions, editor, Markers_ID.NO_COVERAGE_MARKER); // select the area in the editor.
			}
		}
	}
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
}