package handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import layoutgraph.Graph;
import layoutgraph.GraphInformation;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

import sourcegraph.Node;
import view.GraphsCreator;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Layer_ID;
import constants.Messages_ID;
import editor.ActiveEditor;
import editor.Line;

public class LinkHandler extends AbstractHandler {
	
	private boolean oldValue;
	private ISelectionListener listener;
	private ActiveEditor editor;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    if(GraphsCreator.INSTANCE.isDisplayed()) {
	    	ViewRequirementSet viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET); // get the view requirement set.
	    	editor = viewRequirementSet.getEditor();
	    	oldValue = HandlerUtil.toggleCommandState(event.getCommand()); 
		    Graph layoutGraph = (Graph) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.LAYOUT_GRAPH_NUM);
		    if(!oldValue) {
		    	creatorSelectToEditor(); // create the SelectionListener to the editor.
		    	layoutGraph.createSelectionListener(Layer_ID.INSTRUCTIONS, editor); // create the SelectionAdapter event to the nodes.
		    } else {
		    	removeSelectToEditor(); // remove the SelectionListener to the editor.
		    	layoutGraph.removeSelectionListener(); // remove the SelectionAdapter event to the nodes.
		    	editor.deleteALLMarkers(); // removes the marks in the editor.
		    }
	    } else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		}
	    return null; 
	}
	

	private void creatorSelectToEditor() {
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
	
	
	private void removeSelectToEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); // get the active page.
		page.removeSelectionListener(listener); // remove the listener.
	}
	
	@SuppressWarnings("unchecked")
	private void selectNode(int position) {
		List<GraphItem> aux = new LinkedList<GraphItem>(); // auxiliary list to store selected items.
		sourcegraph.Graph<Integer> sourceGraph = (sourcegraph.Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM); // the sourceGraph;.
		Graph layoutGraph = (Graph) GraphsCreator.INSTANCE.getGraphs().get(constants.Graph_ID.LAYOUT_GRAPH_NUM); // the layoutGraph.
		sourceGraph.selectMetadataLayer(Layer_ID.INSTRUCTIONS); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) { // through all nodes.
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
		GraphInformation information = new GraphInformation();
		information.setEditor(editor);
		information.setLayerInformation(Layer_ID.INSTRUCTIONS); // set the information to the instructions layer.
	}
	
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodes.add(entry.getKey());
		return nodes;
	}
	
	private boolean findNode(List<ASTNode> info, int position) {
		int startPosition = new GraphInformation().findStartPosition(info); // get the start position.
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