package ui.source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

import ui.constants.Colors;
import ui.constants.Description;
import ui.constants.MarkersType;
import ui.editor.ActiveEditor;
import ui.editor.Line;
import domain.constants.Layer;
import domain.coverage.instrument.ICoverageData;

public class GraphInformation {

	private adt.graph.Graph<Integer> sourceGraph;
	private ui.source.Graph layoutGraph;
	private ActiveEditor editor;
	private ISelectionListener listener;
	
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
		new ActiveEditor().removeALLMarkers(); // removes the marks in the editor.
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
			editor.removeALLMarkers(); // removes the marks in the editor.
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
							editor.removeALLMarkers(); // removes the marks in the editor.
					}
				}
		} else 
			editor.removeALLMarkers(); // removes the marks in the editor.
	}
	
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodes.add(entry.getKey());
		return nodes;
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
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph(); // set the sourceGraph.
		editor = Activator.getDefault().getEditorController().getActiveEditor();
		editor.removeALLMarkers(); // removes the marks in the editor.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes()) {
			sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				List<ASTNode> nodesInstructions = getASTNodes(map);
				Entry<ASTNode, Line> entry = map.entrySet().iterator().next();	
				String colorStatus = data.getLineStatus(entry.getValue().getStartLine());
				if(colorStatus != null)
					if(colorStatus.equals(Colors.GRENN_ID))
						regionToSelect(nodesInstructions, MarkersType.FULL_COVERAGE_MARKER); // select the area in the editor.
					else if(colorStatus.equals(Colors.RED_ID)) 
						regionToSelect(nodesInstructions, MarkersType.NO_COVERAGE_MARKER); // select the area in the editor.
			}
		}
	}
		
	public void creatorSelectToEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// adding a listener
		listener = new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if(selection instanceof ITextSelection) {				
					ITextSelection textSelected = (ITextSelection) selection; // get the text selected in the editor.
					selectNode(textSelected.getOffset());
				}
			}
		};
		page.addSelectionListener(listener);
	}
	
	public void removeSelectToEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); // get the active page.
		page.removeSelectionListener(listener); // remove the listener.
	}

	@SuppressWarnings("unchecked")
	private void selectNode(int position) {
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph(); // set the sourceGraph.
		List<GraphItem> aux = new LinkedList<GraphItem>(); // auxiliary list to store selected items.
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes()) { // through all nodes.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				List<ASTNode> info = getASTNodes(map);
				if(info != null && findNode(info, position)) { // verify if it is the node.
					for(GraphNode gnode : layoutGraph.getGraphNodes()) { // through all GraphNodes.
						if(gnode.getData().equals(node)) { // if matches with the node.
							aux.add(gnode); // adds the GraphNode to the list.
						}
					}
				}
			}
		}
		while(!aux.isEmpty() && aux.size() != 1) // one word is assigned to only one node.
			aux.remove(0); // remove all the others.
		GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length, GraphItem[].class); // convert the aux into an array of GraphItems.
		layoutGraph.setSelected(items); // the list of selected items.
		setLayerInformation(Layer.INSTRUCTIONS); // set the information to the instructions layer.
	}
	
	
	private boolean findNode(List<ASTNode> info, int position) {
		int startPosition = findStartPosition(info); // get the start position.
		int endPosition = 0;
		ASTNode aNode = info.get(0); // the first element of the list.
		switch(aNode.getNodeType()) {
			case ASTNode.IF_STATEMENT:
			case ASTNode.DO_STATEMENT:
				endPosition = aNode.getStartPosition() + 2; // select the if or do words.
				break;
			case ASTNode.FOR_STATEMENT:
			case ASTNode.ENHANCED_FOR_STATEMENT:
				endPosition = aNode.getStartPosition() + 3; // select the for word.
				break;
			case ASTNode.SWITCH_STATEMENT:
				endPosition = aNode.getStartPosition() + 6; // select the switch word.
				break;
			case ASTNode.WHILE_STATEMENT:
				endPosition = aNode.getStartPosition() + 5;; // select the while word.
				break;
			default:
				if(aNode.getStartPosition() <= startPosition)
					endPosition = info.get(info.size() - 1).getStartPosition() + info.get(info.size() - 1).getLength(); // select the block of instructions associated to the selected node.
				else
					endPosition = aNode.getStartPosition() + aNode.getLength(); // select the block of instructions associated to the selected node.
				break;
		}
		if(startPosition <= position && position <= endPosition) // if a position is in his node.
			return true;
		return false;
	}
}