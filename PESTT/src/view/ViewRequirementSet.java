package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import layoutgraph.GraphInformation;

import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

import sourcegraph.Edge;
import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;
import constants.Colors_ID;
import constants.Description_ID;
import constants.Graph_ID;
import constants.Images_ID;
import constants.Layer_ID;
import constants.Messages_ID;
import constants.TableViewers_ID;
import coverage.CoverageInformation;
import coverage.ICoverage;
import coverage.ICoverageData;
import coveragealgorithms.CoverageAlgorithmsFactory;
import coveragealgorithms.ICoverageAlgorithms;
import editor.ActiveEditor;

public class ViewRequirementSet extends ViewPart {

	private TableViewer testRequirementsViewer;
	private TableViewer statisticsViewer;
	private TableViewer executedGraphViewer;
	private sourcegraph.Graph<Integer> sourceGraph;
	private layoutgraph.Graph layoutGraph;
	private Composite parent;
	private Control statisticsControl; // control of statisticsViewer
	private Control executedGraphControl; // control of statisticsViewer
	private ICoverageAlgorithms<Integer> requirementSet;
	private ICoverage coverageInformation;
	private ActiveEditor editor;
	private GraphInformation information;
	private ArrayList<Object> executedĜraphs;
	private Graph<Integer> selectedExecutedGraph;
	private Path<Integer> selectedTestRequirement;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
	}

	@Override
	public void setFocus() {
		// does nothing.
	}

	public void showTestRequirements(String option) {
		information = new GraphInformation();
		setGraphs();
		disposeControl(1);
		testRequirementsViewer = createViewTable(); // create the new view of requirements set.
		createColumnsToTestRequirement(); // create columns.
		setTestRequirements(option); // insert values to the view.
		setSelections(testRequirementsViewer); // associate path to the ViewGraph elements.
		update(); // show in layout.
	}

	public void showCoverageResults() {
		if(testRequirementsViewer != null) {
			executedĜraphs = null;
			cleanPathStatus();
			disposeControl(2);
			executedGraphViewer = createViewTable();
			executedGraphControl = executedGraphViewer.getControl();
			statisticsViewer = createViewTable();
			statisticsControl = statisticsViewer.getControl();
			createColumnsToExecutedGraphViewer();
			createColumnsToStatisticsViewer();
			setSelections(executedGraphViewer); // associate executed graph to the ViewGraph elements.
			update(); // show in layout.
		} else {
			MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_TEST_REQUIREMENTS); 
			MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.SELECT_COVERAGE); // message displayed to select test requirements.
		}
	}

	@SuppressWarnings("unchecked")
	private void setGraphs() {
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM); // the sourceGraph;.
		layoutGraph = (layoutgraph.Graph) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.LAYOUT_GRAPH_NUM); // the layoutGraph.
	}

	public void disposeControl(int controls) {
		switch(controls) {
		case 1:
			for(Control control : parent.getChildren())
				control.dispose();
			break;
		case 2:
			if(executedGraphControl != null)
				executedGraphControl.dispose();
		case 3:
			if(statisticsControl != null)
				statisticsControl.dispose();
			break;
		}

		if(layoutGraph != null) {
			GraphItem[] items = {}; // clean the selected elements.
			layoutGraph.setSelected(items); // the list of selected items.
			information.setEditor(editor);
			information.setLayerInformation(Layer_ID.INSTRUCTIONS);
		}
	}

	private TableViewer createViewTable() {
		TableViewer viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = viewer.getTable(); // create the table.
		table.setHeaderVisible(true); // show header.
		table.setLinesVisible(true); // show table lines.
		viewer.setContentProvider(new ArrayContentProvider()); // set the content provider.
		getSite().setSelectionProvider(viewer); // Make the selection available to other views.
		return viewer;
	}

	private void createColumnsToTestRequirement() {
		String[] columnNames = new String[] { TableViewers_ID.EMPTY, TableViewers_ID.STATUS, TableViewers_ID.TESTREQUIREMENTS }; // the names of columns.
		int[] columnWidths = new int[] { 50, 50, 50 }; // the width of columns.

		// first column is for the id.
		TableViewerColumn col = createColumnsHeaders(testRequirementsViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Path<?> path = (Path<?>) cell.getElement();
				cell.setText(Integer.toString(path.getPathId()));
			}
		});

		// second column is for test paths.
		col = createColumnsHeaders(testRequirementsViewer, columnNames[1], columnWidths[1], 1);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
			}
		});

		// third column is for test paths.
		col = createColumnsHeaders(testRequirementsViewer, columnNames[2], columnWidths[2], 2);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Path<?> path = (Path<?>) cell.getElement();
				cell.setText(path.toString());
			}
		});
	}

	public void createColumnsToExecutedGraphViewer() {
		coverageInformation = new CoverageInformation(requirementSet, editor);
		executedGraphViewer.setInput(getExecutedGraphs());
		String columnNames = TableViewers_ID.EXECUTED_GRAPH; // the names of column.
		int columnWidths = 100; // the width of column.
		TableViewerColumn col = createColumnsHeaders(executedGraphViewer, columnNames, columnWidths, 0);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				if(cell.getElement() instanceof Graph<?>) {
					Graph<?> graph = (Graph<?>) cell.getElement();
					cell.setText(graph.toString());
				} else {
					Path<?> path = (Path<?>) cell.getElement();
					cell.setText(path.toString());
				}
			}
		});
	}

	private void createColumnsToStatisticsViewer() {
		String columnNames = TableViewers_ID.STATISTICS; // the names of column.
		int columnWidths = 200; // the width of column.
		TableViewerColumn col = createColumnsHeaders(statisticsViewer, columnNames, columnWidths, 0);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				String str = (String) cell.getElement();
				cell.setText(str);
			}
		});
	}
	
	public void cleanPathStatus() {
		int n = 0;
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			item.setImage(1, null);
			if(n % 2 == 0)
				testRequirementsViewer.getTable().getItem(n).setBackground(Colors_ID.WHITE);
			else 
				testRequirementsViewer.getTable().getItem(n).setBackground(Colors_ID.GREY);
			n++;
		}
		if(statisticsViewer != null) {
			disposeControl(3);
			statisticsViewer = createViewTable();
			statisticsControl = statisticsViewer.getControl();
			createColumnsToStatisticsViewer();
			update();
		}
	}

	@SuppressWarnings("unchecked")
	private void setPathStatus(Object selectedExecutedGraph) {
		StatusImages images = new StatusImages();
		ArrayList<Path<Integer>> coveredPaths = null;
		int n = 0;
		if(selectedExecutedGraph instanceof Graph<?>)
			coveredPaths = coverageInformation.getCoveredPaths((Graph<Integer>) selectedExecutedGraph);
		else 
			coveredPaths = coverageInformation.getCoveredPaths((Path<Integer>) selectedExecutedGraph);		
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			if(coveredPaths.contains(requirementSet.getTestRequirements().get(n))) {
				item.setImage(1, images.getImage().get(Images_ID.PASS));
				testRequirementsViewer.getTable().getItem(n).setBackground(Colors_ID.GREEN_COVERAGE);
			} else {
				item.setImage(1, images.getImage().get(Images_ID.FAIL));
				testRequirementsViewer.getTable().getItem(n).setBackground(Colors_ID.RED_COVERAGE);
			}
			n++;
		}
	}

	private TableViewerColumn createColumnsHeaders(TableViewer viewer, String columnName, int columnWidth, int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE); // the columns style.
		final TableColumn column = viewerColumn.getColumn(); // get the column.
		column.setText(columnName); // set the column title.
		column.setWidth(columnWidth); // set the column width.
		column.setAlignment(SWT.CENTER); // set the column alignment.
		column.setResizable(true); // set the column to be resizable.
		column.setMoveable(true); // set the column to be moveable.
		return viewerColumn;
	}

	private void setTestRequirements(String option) {
		requirementSet = new CoverageAlgorithmsFactory<Integer>().getCoverageAlgorithm(option);
		requirementSet.visitGraph(sourceGraph);
		testRequirementsViewer.setInput(requirementSet.getTestRequirements()); 
	}
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
	
	public ActiveEditor getEditor() {
		return editor;
	}
	
	public ArrayList<Object> getExecutedGraphs() {
		if(executedĜraphs == null) 
			executedĜraphs = coverageInformation.getExecutedGraphs();
		return executedĜraphs;
	}
	
	public List<ArrayList<ICoverageData>> getCoverageData() {
		return coverageInformation.getCoverageData();
	}
	
	public Graph<Integer> getSelectedGraph() {
		return selectedExecutedGraph;
	}
	
	public ArrayList<Path<Integer>> getTestRequirement() {
		return requirementSet.getTestRequirements();
	}
	
	public Path<Integer> getSelectedTestRequirement() {
		Path<Integer> temp = selectedTestRequirement;
		selectedTestRequirement = null;
		return temp;
	}
	
	public TableViewer getTableViewer(String viewer) {
		switch(TableViewers_ID.valueOf(viewer)) {
			case TESTREQUIREMENTSETVIEWER:
				return testRequirementsViewer;
			case EXECUTESGRAPHVIEWER:
				return executedGraphViewer;
			case STATISTICSVIEWER:
				return statisticsViewer;
		}
		return null;
	}
	
	public void update() {
		parent.layout();
	}

	@SuppressWarnings("unchecked")
	private void setSelections(final TableViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
		    
			public void selectionChanged(final SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); // get the selection.
				ArrayList<GraphItem> aux = null; // auxiliary list to store selected items.
				if(selection.getFirstElement() instanceof Path<?>) {
					selectedTestRequirement = (Path<Integer>) selection.getFirstElement(); // get the path selected.
					if(selectedTestRequirement != null)
						if(viewer == testRequirementsViewer)
							aux = selectTestRequirement(selectedTestRequirement);
						else
							aux = selectExecutedPath(selectedTestRequirement);
				} else {
					selectedExecutedGraph = (Graph<Integer>) selection.getFirstElement(); // get the path selected.
					if(selectedExecutedGraph != null)
						aux = selectExecutedGraph(selectedExecutedGraph);
				}
				
				if(aux == null)
					return;
				
				GraphItem[] items = Arrays.copyOf(aux.toArray(), aux.toArray().length, GraphItem[].class); // convert the aux into an array of GraphItems.
				layoutGraph.setSelected(items); // the list of selected items.
				ICommandService cmdService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class); // get the ICommandService.
				State state = cmdService.getCommand(Description_ID.LINK_BUTTON).getState("org.eclipse.ui.commands.toggleState"); // the current state of the link command.
				boolean value = (Boolean) state.getValue(); // the value of the state.
				if(value) // if the link button is on.
					if(viewer.equals(executedGraphViewer)) {
						if(selectedExecutedGraph != null) {
							int index = coverageInformation.getStatusOfRun(selectedExecutedGraph);
							information.setEditor(editor);
							information.setVisualCoverageStatus(coverageInformation.getCoverageStatus(index).get(0)); // insert coverage status to the editor.
						}
					} else {
						information.setLayerInformation(Layer_ID.INSTRUCTIONS); // set the information to the instructions layer.
					}
		    }
		});
	}
		
	private ArrayList<GraphItem> selectExecutedGraph(Graph<Integer> selectedExecutedGraph) {
		ArrayList<GraphItem> aux = new ArrayList<GraphItem>();
		setPathStatus(selectedExecutedGraph);
		int index = coverageInformation.getStatusOfRun(selectedExecutedGraph);
		statisticsViewer.setInput(coverageInformation.getCoverageStatistics(index, selectedExecutedGraph));
		for(Node<Integer> node : selectedExecutedGraph.getNodes()) { // through all node in the path.
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) // if matches.
						aux.add(gnode); // add node item to the list.
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}

			for(Edge<Integer> edge : selectedExecutedGraph.getNodeEdges(node)) // through all edges of the node.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
					if(gconnection.getData().equals(edge)) // if matches.
						aux.add(gconnection); // add connection item to the list.
		}
		return aux;
	}
	
	private ArrayList<GraphItem> selectExecutedPath(Path<Integer> selectedExecutedPath) {
		ArrayList<GraphItem> aux = new ArrayList<GraphItem>();
		setPathStatus(selectedExecutedPath);
		int index = coverageInformation.getStatusOfRun(selectedExecutedPath);
		statisticsViewer.setInput(coverageInformation.getCoverageStatistics(index, selectedExecutedPath));
		for(int i = 0; i < selectedTestRequirement.getPathNodes().size(); i++) { // through all node in the path.
			Node<Integer> node = selectedTestRequirement.getPathNodes().get(i);
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) // if matches.
						aux.add(gnode); // add node item to the list.
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}

			for(Edge<Integer> edge : sourceGraph.getNodeEdges((Node<Integer>) node))  // through all edges of the node.
				if(i < selectedTestRequirement.getPathNodes().size() - 1)  // if is not the last node if the path.
					if(edge.getEndNode() == selectedTestRequirement.getPathNodes().get(i + 1))   // the end node of the edge (the next node in the path).
						for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
							if(gconnection.getData().equals(edge)) // if matches.
								aux.add(gconnection); // add connection item to the list.
		}
		return aux;
	}
	
	private ArrayList<GraphItem> selectTestRequirement(Path<Integer> selectedTestRequirement) {
		ArrayList<GraphItem> aux = new ArrayList<GraphItem>();
		// select the nodes in the graph.		
		for(int i = 0; i < selectedTestRequirement.getPathNodes().size(); i++) { // through all node in the path.
			Node<Integer> node = selectedTestRequirement.getPathNodes().get(i);
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) // if matches.
						aux.add(gnode); // add node item to the list.
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}
			
			// select the edges in the graph.
			for(Edge<Integer> edge : sourceGraph.getNodeEdges((Node<Integer>) node))  // through all edges of the node.
				if(i < selectedTestRequirement.getPathNodes().size() - 1)  // if is not the last node if the path.
					if(edge.getEndNode() == selectedTestRequirement.getPathNodes().get(i + 1))   // the end node of the edge (the next node in the path).
						for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
							if(gconnection.getData().equals(edge)) // if matches.
								aux.add(gconnection); // add connection item to the list.
		}
		return aux;
	}
}