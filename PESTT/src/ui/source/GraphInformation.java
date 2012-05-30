package ui.source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import main.activator.Activator;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
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
import ui.editor.Line;
import domain.constants.Layer;
import domain.coverage.data.ICoverageData;

public class GraphInformation {

	private static final String ALL = "All";
	private static final String TRUE = "True";
	private static final String FALSE = "False";
	
	private adt.graph.Graph<Integer> sourceGraph;
	private ui.source.Graph layoutGraph;
	private ISelectionListener listener;
	
	public GraphInformation(ui.source.Graph layoutGraph) {
		this.layoutGraph = layoutGraph;
	}
	
	public void setLayerInformation(Layer layer) {
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph(); // set the sourceGraph.
		switch(layer) {
		case INSTRUCTIONS:
			addInformationToLayer1();
			break;
			case EMPTY:
				clear();
				break;
			case GUARDS:
				addInformationToLayers2_3_4(ALL);
				break;
			case GUARDS_TRUE:
				addInformationToLayers2_3_4(TRUE);
				break;
			case GUARDS_FALSE:
				addInformationToLayers2_3_4(FALSE);
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
		Activator.getDefault().getEditorController().removeALLMarkers(); // removes the marks in the editor.
	}

	@SuppressWarnings("unchecked")
	private void addInformationToLayer1() {
		if(!layoutGraph.getSelected().isEmpty()) { // verify if there are nodes selected.
			Activator.getDefault().getEditorController().removeALLMarkers(); // removes the marks in the editor.
			for(GraphItem item : layoutGraph.getSelected()) // through all graph items.
				if(item instanceof GraphNode) { // verify if is a GraphNode.
					adt.graph.Node<Integer> node = sourceGraph.getNode(Integer.parseInt(item.getText())); // get the node.
					sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
					HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
					if(map != null) {
						List<ASTNode> instructions = getASTNodes(map);
						if(instructions != null) 
							regionToSelect(instructions, MarkersType.LINK_MARKER); // select the area in the editor.
						else 
							Activator.getDefault().getEditorController().removeALLMarkers(); // removes the marks in the editor.
					}
				}
		} else 
			Activator.getDefault().getEditorController().removeALLMarkers(); // removes the marks in the editor.
	}
	
	private void addInformationToLayers2_3_4(String value) {
		setLayerInformation(Layer.EMPTY); // clean previous informations.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes())  // search in the sourceGraph for all node.
			for(adt.graph.Edge<Integer> edge : sourceGraph.getNodeEdges(node))   // search in the sourceGraph for all edges.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // search in the layoutGraph for all edges.
					if(gconnection.getData().equals(edge)) { // when they match.
						sourceGraph.selectMetadataLayer(Layer.GUARDS.getLayer()); // change to the layer with the cycles information.
						String info = (String) sourceGraph.getMetadata(edge); // get the information.
						if(info != null) 
							if(!value.equals(ALL)) {
								if(info.equals("break;") || info.equals("continue;") || info.equals("return;"))
									gconnection.setText(info);
								else if(value.equals(TRUE) && !info.substring(0,  1).equals("¬"))
									gconnection.setText(info); // set the information to the edge.
								else if(value.equals(FALSE) && info.substring(0,  1).equals("¬"))
									gconnection.setText(info); // set the information to the edge.
							} else
								gconnection.setText(info); // set the information to the edge.
						break;
					}
	}
	
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodes.add(entry.getKey());
		return nodes;
	}
	 
	private void regionToSelect(List<ASTNode> instructions, String markerType) {
		int start = findStartPosition(instructions); // get the start position.
		ASTNode instruction = instructions.get(0); // the first element of the list.
		switch(instruction.getNodeType()) {
			case ASTNode.IF_STATEMENT:
			case ASTNode.DO_STATEMENT:
				Activator.getDefault().getEditorController().createMarker(markerType, start, getLength(start, instruction.getStartPosition(), 2)); // select the if or do words.
				break;
			case ASTNode.FOR_STATEMENT:
			case ASTNode.ENHANCED_FOR_STATEMENT:
				Activator.getDefault().getEditorController().createMarker(markerType, start, getLength(start, instruction.getStartPosition(), 3)); // select the for word.
				break;
			case ASTNode.SWITCH_STATEMENT:
				Activator.getDefault().getEditorController().createMarker(markerType, start, getLength(start, instruction.getStartPosition(), 6)); // select the switch word.
				break;
			case ASTNode.WHILE_STATEMENT:
				Activator.getDefault().getEditorController().createMarker(markerType, start, getLength(start, instruction.getStartPosition(), 5)); // select the while word.
				break;
			default:
				for(ASTNode  instr : instructions) 
					Activator.getDefault().getEditorController().createMarker(markerType, instr.getStartPosition(), getLength(instr.getStartPosition(), instr.getStartPosition(), instr.getLength()));					
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
	public void setVisualCoverageStatus(ICoverageData data, List<GraphItem> items) {
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph(); // set the sourceGraph.
		Activator.getDefault().getEditorController().removeALLMarkers(); // removes the marks in the editor.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes()) {
			sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				List<ASTNode> nodesInstructions = getASTNodes(map);
				Entry<ASTNode, Line> entry = map.entrySet().iterator().next();	
				String colorStatus = data.getLineStatus(entry.getValue().getStartLine());
				if(colorStatus != null)
					if(colorStatus.equals(Colors.GRENN_ID) && isNodeSelected(items, node))
						regionToSelect(nodesInstructions, MarkersType.FULL_COVERAGE_MARKER); // select the area in the editor.
					else if(colorStatus.equals(Colors.RED_ID) || !isNodeSelected(items, node) )
						regionToSelect(nodesInstructions, MarkersType.NO_COVERAGE_MARKER); // select the area in the editor.
			}
		}
	}
	
	private boolean isNodeSelected(List<GraphItem> items, adt.graph.Node<Integer> node) {
		for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
			if(!gnode.isDisposed() && gnode.getData().equals(node)) // if matches.
				if(items.contains(gnode))
					return items.contains(gnode); 
		return false;
	}
		
	public void creatorSelectToEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// adding a listener
		listener = new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if(selection instanceof ITextSelection && Activator.getDefault().getEditorController().isEverythingMatching()) {				
					ITextSelection textSelected = (ITextSelection) selection; // get the text selected in the editor.
					String currentMethod = getSelectedMethod(textSelected);
					if(currentMethod != null) {
						if(textSelected.getLength() != 0 && currentMethod.equals(Activator.getDefault().getEditorController().getSelectedMethod())) {
							selectNode(textSelected.getOffset());
							return;
						}
					}
				}
				Activator.getDefault().getEditorController().removeALLMarkers();
			}
		};
		page.addSelectionListener(listener);
		
	}
	
	public void removeSelectToEditor() {
		if(listener != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); // get the active page.
			page.removeSelectionListener(listener); // remove the listener.
		}
	}
	
	private String getSelectedMethod(ITextSelection textSelected) {
		try {
			for(IType type : Activator.getDefault().getEditorController().getCompilationUnit().getAllTypes())
				for(IMethod method : type.getMethods()) {
					int cursorPosition = textSelected.getOffset();
					int methodStart = method.getSourceRange().getOffset();
					int methodEnd = method.getSourceRange().getOffset() + method.getSourceRange().getLength();
					if(methodStart <= cursorPosition && cursorPosition <= methodEnd)
						return method.getElementName();
				}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
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