package ui.source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

import ui.editor.ActiveEditor;
import ui.editor.Line;
import adt.graph.Path;
import domain.ControlFlowGraphGeneratedEvent;
import domain.LayerChangeEvent;
import domain.LinkChangeEvent;
import domain.TestPathSelected;
import domain.TestRequirementSelected;
import domain.constants.Colors;
import domain.constants.Description;
import domain.constants.Layer;
import domain.constants.Messages;
import domain.dot.processor.DotProcess;
import domain.dot.processor.IDotProcess;
import domain.graph.visitors.DotGraphVisitor;

public class Graph implements Observer {
	
	private org.eclipse.zest.core.widgets.Graph graph;
	private adt.graph.Graph<Integer> sourceGraph;
	private List<GraphNode> graphNodes;
	private List<GraphConnection> graphEdges;
	private SelectionAdapter event;
	private Composite parent;
	private ISelectionListener listener;
	private ActiveEditor editor;
	private GraphInformation information;
	
	public Graph(Composite parent) {
		this.parent = parent;
		graph = new org.eclipse.zest.core.widgets.Graph(parent, SWT.NONE);
		information = new GraphInformation(this);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getSourceGraphController().addObserverSourceGraph(this);
		Activator.getDefault().getCFGController().addObserver(this);
		create(Activator.getDefault().getSourceGraphController().getSourceGraph());
	}
	
	private void create(adt.graph.Graph<Integer> sourceGraph) {
		graph.clear();
		this.sourceGraph = sourceGraph;
		DotGraphVisitor<Integer> visitor = new DotGraphVisitor<Integer>(); // creates the visitor to the graph.
		Activator.getDefault().getSourceGraphController().applyVisitor(visitor);
		String dotGraph = "digraph grafo {\nrankdir=TD\nsize=\"10,10\"\n" + visitor.getDotString()  + "}\n"; // creates the string to be passed to Graphviz.
		IDotProcess dotProcess = new DotProcess(); // the object that parse the information to build the layoutGraph.
		Map<String, List<String>> map = dotProcess.DotToPlain(dotGraph); // the information to build the layoutGraph.
		GraphElements elements = new GraphElements(map);
		setLayout(elements);
		setNodes(elements.getNodesInfo());
		setEdges(elements.getEdgesInfo());
	}
	
	public void dispose() {
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
		Activator.getDefault().getTestPathController().deleteObserver(this);
		Activator.getDefault().getSourceGraphController().deleteObserverSourceGraph(this);
		Activator.getDefault().getCFGController().deleteObserver(this);
	}
	
	private void setNodes(Map<String, Node> nodes) {
		graphNodes = new LinkedList<GraphNode>(); // the list of nodes.
		Set<Entry<String, Node>> set = nodes.entrySet(); // the node properties.
		Iterator<Entry<String, Node>> iterator = set.iterator(); 
		while(iterator.hasNext()) {
			Entry<String, Node> entry = iterator.next();
			Node node = entry.getValue(); // get the current node of the list.
			GraphNode gnode = new GraphNode(graph, SWT.NONE, node.getName()); // create the graph node.
			gnode.setBackgroundColor(node.getBackgroundColor()); // sets the node background color.
			gnode.setForegroundColor(node.getForegroundColor()); // sets the node text color.
			gnode.setBorderColor(Colors.BLACK); // sets the node border color to black.
			gnode.setHighlightColor(Colors.YELLOW); // sets he highlight color.
			gnode.setBorderHighlightColor(Colors.BLACK); // sets the node border highlight color to black.
			adt.graph.Node<Integer> sourceNode = sourceGraph.getNode(Integer.parseInt(gnode.getText())); // the correspondent source node.
			gnode.setData(sourceNode); // associate the visualization node with the source node.	
			new domain.GraphInformation().addInformationToLayer0(sourceGraph, sourceNode, gnode); // associate the the nodes of sourceGraph and layoutGraph.
			graphNodes.add(gnode); // add the node to the list.
		}
	}
	
	private void setEdges(Map<String, Edge> edges) {
		graphEdges = new LinkedList<GraphConnection>(); // the list of edges.
		GraphNode begin; // the initial node of the edge.
		GraphNode end; // the final node of the edge.
		int index = 0;
		Set<Entry<String, Edge>> set = edges.entrySet(); // the edge properties.
		Iterator<Entry<String, Edge>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Edge> entry = iterator.next();
			Edge edge = entry.getValue(); // get the current connection of the list.
			index = 0; // initialize the index.
			begin = null; // initialize the begin node of the connection.
			end = null; // initialize the end node of the connection.
			while((begin == null || end == null) && index < graphNodes.size()) {
				if(edge.getBeginNode().equals(graphNodes.get(index).getText())) {
					begin = graphNodes.get(index); // assign the graph node to the start of the connection..
				} 
				if(edge.getEndNode().equals(graphNodes.get(index).getText())) {
					end = graphNodes.get(index); // assign the the graph node to the end of the connection.
				}
				index++;	
			}
			GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, begin, end); // create the graph connection between the start and the end nodes.
			connection.setLineColor(edge.getColor()); // sets the edge color.
			adt.graph.Node<Integer> beginNode = sourceGraph.getNode(Integer.parseInt(begin.getText())); // the correspondent begin node.
			adt.graph.Node<Integer> endNode = sourceGraph.getNode(Integer.parseInt(end.getText())); // the correspondent final node.
			// verify which node is the correct.
			for(adt.graph.Edge<Integer> sourceEdge : sourceGraph.getNodeEdges(beginNode)) { // get all edges of the begin node.
				if(sourceEdge.getEndNode().equals(endNode)) { // when the edges matches.
					connection.setData(sourceEdge); // associate the visualization edge with the source edge.
					sourceGraph.addMetadata(sourceEdge, connection); // associate the the edge of sourceGraph and layoutGraph.
				}
			}
			graphEdges.add(connection); // add the edge to the list.
		}
	}
	
	public List<GraphConnection> getGraphEdges() {
		return graphEdges; // the list of connections.
	}
	
	private void setLayout(GraphElements graphElements) {
		 graph.setLayoutAlgorithm(new CFGLayoutAlgorithm(parent, graphElements), true);
	}
	
	@SuppressWarnings("unchecked")
	public List<GraphItem> getSelected() {
		return graph.getSelection(); // return the list with the selected nodes.
	}
	
	private void setSelected(GraphItem[] items) {
		graph.setSelection(items); // the items selected.
	}
	
	private void unselectAll() {
		GraphItem[] items = {};
		setSelected(items);
	}
	
	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof ControlFlowGraphGeneratedEvent)
			create(((ControlFlowGraphGeneratedEvent) data).sourceGraph);
		else if(data instanceof TestRequirementSelected) {
			if(((TestRequirementSelected) data).selected == null)
				unselectAll();
			else
				selectTestRequirement(((TestRequirementSelected) data));
		} else if(data instanceof TestPathSelected) {
			if(((TestPathSelected) data).selected == null)
				unselectAll();
			else
				selectTestPath(((TestPathSelected) data));
		}
		else if(data instanceof LinkChangeEvent) {
			editor = Activator.getDefault().getEditorController().getActiveEditor();
			if(((LinkChangeEvent) data).state) {
				creatorSelectToEditor(); // create the SelectionListener to the editor.
		    	createSelectionListener(); // create the SelectionAdapter event to the nodes.
		    } else {
		    	removeSelectToEditor(); // remove the SelectionListener to the editor.
		    	removeSelectionListener(); // remove the SelectionAdapter event to the nodes.
		    	editor.deleteALLMarkers(); // removes the marks in the editor.
		    }
		} else if(data instanceof LayerChangeEvent) {
			information.setLayerInformation(((LayerChangeEvent) data).layer);
		}
	}

	private void selectTestRequirement(TestRequirementSelected data) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Description.VIEW_GRAPH);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		if(data.selected != null) {
			List<GraphItem> aux = selectInGraph(data.selected);
			GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length, GraphItem[].class); // convert the aux into an array of GraphItems.
			setSelected(items); // the list of selected items.
			setVisualCoverage(data);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void selectTestPath(TestPathSelected data) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Description.VIEW_GRAPH);
		} catch(PartInitException e) {
			e.printStackTrace();
		}
		if(data.selected != null) {
			List<GraphItem> aux = null;
			if(data.selected instanceof Path<?>)
				aux = selectInGraph((Path<Integer>) data.selected);
			else if(data.selected instanceof String)
				aux = selectTotal((String) data.selected);
			GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length, GraphItem[].class); // convert the aux into an array of GraphItems.
			setSelected(items); // the list of selected items.
			setVisualCoverage(data);
		}
	}
	
	private List<GraphItem> selectInGraph(Path<Integer> selectedTestRequirement) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		// select the nodes in the graph.		
		for(int i = 0; i < selectedTestRequirement.getPathNodes().size(); i++) { // through all node in the path.
			adt.graph.Node<Integer> node = selectedTestRequirement.getPathNodes().get(i);
			for(GraphNode gnode : graphNodes)  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) { // if matches.
						aux.add(gnode); // add node item to the list.
						break;
					}
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages.COVERAGE_TITLE, Messages.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}

			// select the edges in the graph.
			for(adt.graph.Edge<Integer> edge : sourceGraph.getNodeEdges((adt.graph.Node<Integer>) node))  // through all edges of the node.
				if(i < selectedTestRequirement.getPathNodes().size() - 1)  // if is not the last node if the path.
					if(edge.getEndNode() == selectedTestRequirement.getPathNodes().get(i + 1))   // the end node of the edge (the next node in the path).
						for(GraphConnection gconnection : graphEdges)  // through all connections in the graph.
							if(gconnection.getData().equals(edge)) { // if matches.
								aux.add(gconnection); // add connection item to the list.
								break;
							}
		}
		return aux;
	}

	private List<GraphItem> selectTotal(String selectTotal) {
		List<GraphItem> total = new LinkedList<GraphItem>();
		List<GraphItem> aux = null;
		Iterator<Path<Integer>> iterator = Activator.getDefault().getTestPathController().iterator();
 		while(iterator.hasNext()) {
			Path<Integer> path = iterator.next();
			aux = selectInGraph(path);
			for(GraphItem item : aux)
				if(!total.contains(item))
					total.add(item);
			aux.clear();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	private void setVisualCoverage(Object data) {
		if(Activator.getDefault().getCFGController().getLinkState()) // if the link button is on.
			if(data instanceof TestPathSelected)
				if(((TestPathSelected) data).selected instanceof Path<?>)
					information.setVisualCoverageStatus(Activator.getDefault().getCoverageDataController().getCoverageData((Path<Integer>) ((TestPathSelected) data).selected)); // insert coverage status to the editor.
				else
					System.out.println("String");
			else 
				information.setLayerInformation(Layer.INSTRUCTIONS); // set the information to the instructions layer.
	}
	
	private void createSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.
				
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item != null && e.item instanceof GraphNode ) {
					getSelected();
					information.setLayerInformation(Layer.INSTRUCTIONS);
				} else if(e.item == null)
					editor.deleteALLMarkers(); // removes the marks in the editor.
			}
		};	
		graph.addSelectionListener(event); // add the SelectionAdapter to the graph. 
	}
	
	private void removeSelectionListener() {
		graph.removeSelectionListener(event); // remove the SelectionAdapter from the graph.
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
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(adt.graph.Node<Integer> node : sourceGraph.getNodes()) { // through all nodes.
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				List<ASTNode> info = getASTNodes(map);
				if(info != null && findNode(info, position)) { // verify if it is the node.
					for(GraphNode gnode : graphNodes) { // through all GraphNodes.
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
		setSelected(items); // the list of selected items.
		information.setLayerInformation(Layer.INSTRUCTIONS); // set the information to the instructions layer.
	}
	
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodes.add(entry.getKey());
		return nodes;
	}
	
	private boolean findNode(List<ASTNode> info, int position) {
		int startPosition = information.findStartPosition(info); // get the start position.
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