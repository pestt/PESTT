package view;

import java.util.Arrays;
import java.util.LinkedList;
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

	private Composite parent;
	private sourcegraph.Graph<Integer> sourceGraph;
	private layoutgraph.Graph layoutGraph;
	private TableViewer testRequirementsViewer;
	private TableViewer executedGraphViewer;
	private TableViewer statisticsViewer;
	private Control executedGraphControl; // control of statisticsViewer
	private Control statisticsControl; // control of statisticsViewer
	private ICoverageAlgorithms<Integer> requirementSet;
	private ICoverage coverageInformation;
	private ActiveEditor editor;
	private GraphInformation information;
	private List<Path<Integer>> testRequirements;
	private List<Object> executedGraphs;
	private Graph<Integer> selectedExecutedGraph;
	private Path<Integer> selectedExecutedPath;
	private Path<Integer> selectedTestRequirement;
	private String selectTotal;
	private String tour;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
	}

	@Override
	public void setFocus() {
		// does nothing.
	}

	public void showTestRequirements(String criteria) {
		disposeControl(1);
		information = new GraphInformation();
		setGraphs();
		testRequirementsViewer = createViewTable(); // create the new view of requirements set.
		createColumnsToTestRequirement(); // create columns.
		setTestRequirements(criteria); // insert values to the view.
		setSelections(testRequirementsViewer); // associate path to the ViewGraph elements.
		cleanPathStatus();
		update(); // show in layout.
	}
	
	public void showCoverage(String criteria) {
		disposeControl(1);
		executedGraphs = null;
		testRequirements = null;
		information = new GraphInformation();
		setGraphs();
		testRequirementsViewer = createViewTable(); // create the new view of requirements set.
		createColumnsToTestRequirement(); // create columns.
		setTestRequirements(criteria); // insert values to the view.
		setSelections(testRequirementsViewer); // associate path to the ViewGraph elements.
		executedGraphViewer = createViewTable();
		executedGraphControl = executedGraphViewer.getControl();
		statisticsViewer = createViewTable();
		statisticsControl = statisticsViewer.getControl();
		createColumnsToExecutedGraphViewer();
		createColumnsToStatisticsViewer();
		setSelections(executedGraphViewer); // associate executed graph to the ViewGraph elements.
		cleanPathStatus();
		update(); // show in layout.
	}

	@SuppressWarnings("unchecked")
	private void setGraphs() {
		sourceGraph = (Graph<Integer>) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.SOURCE_GRAPH_NUM); // the sourceGraph;.
		layoutGraph = (layoutgraph.Graph) GraphsCreator.INSTANCE.getGraphs().get(Graph_ID.LAYOUT_GRAPH_NUM); // the layoutGraph.
	}
	
	public void setTour(String tour) {
		this.tour = tour;
		if(testRequirementsViewer != null) 
			if(selectedExecutedGraph != null)
				setPathStatus(selectedExecutedGraph);
			else if(selectedExecutedPath != null) 
				setPathStatus(selectedExecutedPath);
			else if(selectTotal != null)
				setPathStatus(selectTotal);		
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
		int[] columnWidths = new int[] {50, 50, 50 }; // the width of columns.

		// first column is for the id.
		TableViewerColumn col = createColumnsHeaders(testRequirementsViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
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
		coverageInformation = new CoverageInformation(editor);
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
				} else if(cell.getElement() instanceof Path<?>) {
					Path<?> path = (Path<?>) cell.getElement();
					cell.setText(path.toString());
				} else {
					String str = (String) cell.getElement();
					cell.setText(str);
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
				item.setBackground(Colors_ID.WHITE);
			else 
				item.setBackground(Colors_ID.GREY);
			testRequirementsViewer.getTable().getItem(n).setText(0, Integer.toString(n));
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

	private void setPathStatus(Object selectedExecutedGraph) {
		StatusImages images = new StatusImages();
		int n = 0;		
		List<Path<Integer>> coveredPaths = coverageInformation.getCoveredTestRequirements(selectedExecutedGraph, getTestRequirement(), tour);
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			if(coveredPaths.contains(getTestRequirement().get(n))) {
				item.setText(0, Integer.toString(n));
				item.setImage(1, images.getImage().get(Images_ID.PASS));
				item.setBackground(Colors_ID.GREEN_COVERAGE);
			} else {
				item.setText(0, Integer.toString(n));
				item.setImage(1, images.getImage().get(Images_ID.FAIL));
				item.setBackground(Colors_ID.RED_COVERAGE);
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
		if(option != null) {
			requirementSet = new CoverageAlgorithmsFactory<Integer>().getCoverageAlgorithm(option);
			requirementSet.visitGraph(sourceGraph);
			testRequirementsViewer.setInput(getTestRequirement()); 
		} else
			MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.SELECT_VALID_COVERAGE); // message displayed when the coverage criteria is not valid.
	}
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
	
	public ActiveEditor getEditor() {
		return editor;
	}
	
	public List<Object> getExecutedGraphs() {
		if(executedGraphs == null) 
			executedGraphs = coverageInformation.getExecutedPaths();
		
		if(!executedGraphs.contains(Description_ID.TOTAL) && executedGraphs.size() > 1)
			executedGraphs.add(Description_ID.TOTAL);
		return executedGraphs;
	}
	
	public List<List<ICoverageData>> getCoverageData() {
		return coverageInformation.getCoverageData();
	}
	
	public Object getSelectedGraph() {
		if(selectedExecutedGraph != null) 
			return selectedExecutedGraph;
		else if(selectedExecutedPath != null) 
			return selectedExecutedPath;
		else
			return selectTotal;
	}
	
	public List<Path<Integer>> getTestRequirement() {
		if(testRequirements == null)
			testRequirements = requirementSet.getTestRequirements();
		return testRequirements;
	}
	
	public void setTestRequirement(List<Path<Integer>> requirementList) {
		testRequirements = requirementList;
	}
		
	public Path<Integer> getSelectedTestRequirement() {
		return selectedTestRequirement;
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
				List<GraphItem> aux = null; // auxiliary list to store selected items.
				Object selected = null;
				selectedExecutedPath = null;
				selectedExecutedGraph = null;
				selectedTestRequirement = null;
				selectTotal = null;
				if(selection.getFirstElement() instanceof Path<?> && viewer == testRequirementsViewer) {
					selectedTestRequirement = (Path<Integer>) selection.getFirstElement(); // get the path selected.
					if(selectedTestRequirement != null)
						aux = selectTestRequirement(selectedTestRequirement);
					selected = selectedExecutedPath;
				} else if(selection.getFirstElement() instanceof Path<?>) { 
					selectedExecutedPath = (Path<Integer>) selection.getFirstElement(); // get the path selected.
					if(selectedExecutedPath != null)
						aux = selectExecutedPath(selectedExecutedPath);
					selected = selectedExecutedPath;
				} else if(selection.getFirstElement() instanceof Graph<?>){
					selectedExecutedGraph = (Graph<Integer>) selection.getFirstElement(); // get the path selected.
					if(selectedExecutedGraph != null)
						aux = selectExecutedGraph(selectedExecutedGraph);
					selected = selectedExecutedGraph;
				} else {
					selectTotal = (String) selection.getFirstElement(); // get the path selected.
					if(selectTotal != null)
						aux = selectTotal(selectTotal);
					selected = selectTotal;
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
						int index = coverageInformation.getStatusOfRun(selected);
						information.setEditor(editor);
						information.setVisualCoverageStatus(coverageInformation.getCoverageStatus(index).get(0)); // insert coverage status to the editor.
					} else {
						information.setLayerInformation(Layer_ID.INSTRUCTIONS); // set the information to the instructions layer.
					}
		    }
		});
	}
		
	private List<GraphItem> selectExecutedGraph(Graph<Integer> selectedExecutedGraph) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		setPathStatus(selectedExecutedGraph);
		int index = coverageInformation.getStatusOfRun(selectedExecutedGraph);
		statisticsViewer.setInput(coverageInformation.getCoverageStatistics(index, selectedExecutedGraph, getTestRequirement(), tour));
		for(Node<Integer> node : selectedExecutedGraph.getNodes()) { // through all node in the path.
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) { // if matches.
						aux.add(gnode); // add node item to the list.
						break;
					}
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}
					
			for(Edge<Integer> edge : selectedExecutedGraph.getNodeEdges(node)) // through all edges of the node.
				for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
					if(gconnection.getData().equals(edge)) { // if matches.
						aux.add(gconnection); // add connection item to the list.
						break;
					}
		}
		return aux;
	}
	
	private List<GraphItem> selectExecutedPath(Path<Integer> selectedExecutedPath) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		setPathStatus(selectedExecutedPath);
		int index = coverageInformation.getStatusOfRun(selectedExecutedPath);
		statisticsViewer.setInput(coverageInformation.getCoverageStatistics(index, selectedExecutedPath, getTestRequirement(), tour));
		for(int i = 0; i < selectedExecutedPath.getPathNodes().size(); i++) { // through all node in the path.
			Node<Integer> node = selectedExecutedPath.getPathNodes().get(i);
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) { // if matches.
						aux.add(gnode); // add node item to the list.
						break;
					}
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}

			for(Edge<Integer> edge : sourceGraph.getNodeEdges((Node<Integer>) node))  // through all edges of the node.
				if(i < selectedExecutedPath.getPathNodes().size() - 1)  // if is not the last node if the path.
					if(edge.getEndNode() == selectedExecutedPath.getPathNodes().get(i + 1))   // the end node of the edge (the next node in the path).
						for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
							if(gconnection.getData().equals(edge)) { // if matches.
								aux.add(gconnection); // add connection item to the list.
								break;
							}
		}
		return aux;
	}
	
	@SuppressWarnings("unchecked")
	private List<GraphItem> selectTotal(String selectTotal) {
		List<GraphItem> total = new LinkedList<GraphItem>();
		List<GraphItem> aux = null;
		for(Object obj : getExecutedGraphs()) {
			if(obj instanceof Graph<?>)
				aux = selectExecutedGraph((Graph<Integer>) obj);
			else if(obj instanceof Path<?>)
				aux = selectExecutedPath((Path<Integer>) obj);
			
			for(GraphItem item : aux)
				if(!total.contains(item))
					total.add(item);
			aux.clear();
		}
		setPathStatus(selectTotal);
		int index = coverageInformation.getStatusOfRun(selectTotal);
		statisticsViewer.setInput(coverageInformation.getCoverageStatistics(index, selectTotal, getTestRequirement(), tour));
		return total;
	}
	
	private List<GraphItem> selectTestRequirement(Path<Integer> selectedTestRequirement) {
		List<GraphItem> aux = new LinkedList<GraphItem>();
		// select the nodes in the graph.		
		for(int i = 0; i < selectedTestRequirement.getPathNodes().size(); i++) { // through all node in the path.
			Node<Integer> node = selectedTestRequirement.getPathNodes().get(i);
			for(GraphNode gnode : layoutGraph.getGraphNodes())  // through all nodes in the graph.
				if(!gnode.isDisposed()) {
					if(gnode.getData().equals(node)) { // if matches.
						aux.add(gnode); // add node item to the list.
						break;
					}
				} else {
					MessageDialog.openInformation(parent.getShell(), Messages_ID.COVERAGE_TITLE, Messages_ID.NEED_UPDATE); // message displayed when the graph is not designed.
					return null;
				}
			
			// select the edges in the graph.
			for(Edge<Integer> edge : sourceGraph.getNodeEdges((Node<Integer>) node))  // through all edges of the node.
				if(i < selectedTestRequirement.getPathNodes().size() - 1)  // if is not the last node if the path.
					if(edge.getEndNode() == selectedTestRequirement.getPathNodes().get(i + 1))   // the end node of the edge (the next node in the path).
						for(GraphConnection gconnection : layoutGraph.getGraphEdges())  // through all connections in the graph.
							if(gconnection.getData().equals(edge)) { // if matches.
								aux.add(gconnection); // add connection item to the list.
								break;
							}
		}
		return aux;
	}
}