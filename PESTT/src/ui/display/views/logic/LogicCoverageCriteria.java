package ui.display.views.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import ui.constants.Colors;
import ui.events.RefreshLogicGraphEvent;
import domain.constants.LogicCoverageCriteriaId;

@SuppressWarnings("deprecation")
public class LogicCoverageCriteria implements Observer {
	
	private Graph graph;
	private Map<LogicCoverageCriteriaId, GraphNode> nodes;
	private SelectionAdapter event;	

	public LogicCoverageCriteria(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		Activator.getDefault().getCFGController().addObserver(this);
		create();
	}

	private void create() {
		graph.clear();
		setNodes();
		setEdges();
		setLayout();
		createSelectionListener();
		if(Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected()) 
			setSelected(nodes.get(Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria()));
	}
	
	public void dispose() {
		Activator.getDefault().getCFGController().deleteObserver(this);
	}
	
	private void setNodes() {
		nodes = new HashMap<LogicCoverageCriteriaId, GraphNode>();
		GraphNode coc = new GraphNode(graph, SWT.SINGLE, "Complete Clause\n        Coverage\n" + insertTrace(16) + "\n            (CoC)");
		coc.setData(LogicCoverageCriteriaId.COMPLETE_CLAUSE);
		coc.setTooltip(new Label("Combinatorial Coverage (or Complete Clause Coverage) (CoC):\nFor each p ∈ P, Test requirements has test requirements for the clauses in Cp to evaluate to each possible combination of truth values."));
		nodes.put(LogicCoverageCriteriaId.COMPLETE_CLAUSE, coc); 

		GraphNode racc = new GraphNode(graph, SWT.SINGLE, "Restricted Active\n Clause Coverage\n" + insertTrace(16) + "\n          (RACC)");
		racc.setData(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE);
		racc.setTooltip(new Label("Restricted Active Clause Coverage (RACC):\nFor each p ∈ P and each major clause ci ∈ Cp , choose minor clauses cj, j != i so that ci determines p.\n" + 
						"Test requirements has two requirements for each ci: ci evaluates to true and ci evaluates to false.\n" +
						"The values chosen for the minor clauses cj must be the same when ci is true as when ci is false."));
		nodes.put(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE, racc); 

		GraphNode ricc = new GraphNode(graph, SWT.SINGLE, "Restricted Inactive\n  Clause Coverage\n" + insertTrace(18) + "\n           (RICC)");
		ricc.setData(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE);
		ricc.setTooltip(new Label("Restricted Inactive Clause Coverage (RICC):\nFor each p ∈ P and each major clause ci ∈ Cp, choose minor clauses cj, j = i so that ci does not determine p.\n" +
				"Test requirements has four requirements for ci under these circumstances:\n" +
				"(1) ci evaluates to true with p true,\n" +
				"(2) ci evaluates to false with p true,\n" +
				"(3) ci evaluates to true with p false, and\n" +
				"(4) ci evaluates to false with p false.\n" +
				"The values chosen for the minor clauses cj must be the same in cases (1) and (2),\n" +
				"and the values chosen for the minor clauses cj must also be the same in cases (3) and (4)."));
		nodes.put(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE, ricc); 

		GraphNode cacc = new GraphNode(graph, SWT.SINGLE, "Correlated Active\n Clause Coverage\n" + insertTrace(17) + "\n           (CACC)");
		cacc.setData(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE);
		cacc.setTooltip(new Label("Correlated Active Clause Coverage (CACC):\nFor each p ∈ P and each major clause ci ∈ C p, choose minor clauses cj, j = i so that ci determines p.\n"+
								"Test requirements has two requirements for each ci: ci evaluates to true and ci evaluates to false.\n" +
								"The values chosen for the minor clauses cj must cause p to be true for one value of the major clause ci and false for the other."));
		nodes.put(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE, cacc); 

		GraphNode gacc = new GraphNode(graph, SWT.SINGLE, "  General Active\nClause Coverage\n" + insertTrace(16) + "\n          (GACC)");
		gacc.setData(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE);
		gacc.setTooltip(new Label("General Active Clause Coverage (GACC):\nFor each p ∈ P and each major clause ci ∈ Cp, choose minor clauses cj, j != i so that ci determines p.\n" +
				"Test requirements has two requirements for each ci: ci evaluates to true and ci evaluates to false.\n" +
				"The values chosen for the minor clauses cj do not need to be the same when ci is true as when ci is false.\n"));
		nodes.put(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE, gacc); 

		GraphNode gicc = new GraphNode(graph, SWT.SINGLE, "General Inactive\nClause Coverage\n" + insertTrace(16) + "\n          (GICC)");
		gicc.setData(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE);
		gicc.setTooltip(new Label("General Inactive Clause Coverage (GICC):\nFor each p ∈ P and each major clause ci ∈ C p, choose minor clauses cj, j = i so that ci does not determine p.\n" +
				"Test requirements has four requirements for ci under these circumstances:\n " +
				"(1) ci evaluates to true with p true,\n" +
				"(2) ci evaluates to false with p true,\n" +
				"(3) ci evaluates to true with p false, and\n " +
				"(4) ci evaluates to false with p false.\n" +
				"The values chosen for the minor clauses cj may vary amongst the four cases."));
		nodes.put(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE, gicc); 

		GraphNode cc = new GraphNode(graph, SWT.SINGLE, "   Clause\nCoverage\n" + insertTrace(9) + "\n     (CC)");
		cc.setData(LogicCoverageCriteriaId.CLAUSE);
		cc.setTooltip(new Label("Clause Coverage (CC):\nFor each c ∈ C, Test Requirements contains two requirements: c evaluates to true, and c evaluates to false."));
		nodes.put(LogicCoverageCriteriaId.CLAUSE, cc);

		GraphNode pc = new GraphNode(graph, SWT.SINGLE, "Predicate\nCoverage\n" + insertTrace(10) + "\n     (PC)");
		pc.setData(LogicCoverageCriteriaId.PREDICATE);
		pc.setTooltip(new Label("Predicate Coverage (PC):\nFor each p ∈ P, Test requirements contains two requirements: p evaluates to true, and p evaluates to false."));
		nodes.put(LogicCoverageCriteriaId.PREDICATE, pc);

		for(GraphNode gnode : nodes.values()) {
			gnode.setBackgroundColor(Colors.WHITE); 
			gnode.setForegroundColor(Colors.BLACK); 
			gnode.setBorderColor(Colors.BLACK); 
			gnode.setHighlightColor(Colors.YELLOW); 
			gnode.setBorderHighlightColor(Colors.BLACK); 
		}
	}
	
	private void setEdges() {
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.COMPLETE_CLAUSE), nodes.get(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.COMPLETE_CLAUSE), nodes.get(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.PREDICATE));
		GraphConnection edge = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.CLAUSE));
		edge.setCurveDepth(50);
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE), nodes.get(LogicCoverageCriteriaId.PREDICATE));
	}
	
	private String insertTrace(int num) {
		String line = "";
		for(int i = 0; i < num; i++)
			line += (char)0x2013 + "";
		return line;
	}
	
	private void setLayout() {
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	private void createSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.
				
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item != null && e.item instanceof GraphNode ) {
					setSelected(null);
					setSelected((GraphItem) e.item);
					getSelected().getData();
				}
			}
		};	
		graph.addSelectionListener(event);				
	}

	private GraphItem getSelected() {
		if(graph.getSelection().size() == 0)
			return null;
		return (GraphItem) graph.getSelection().get(0); // return the list with the selected nodes.
	}
	
	private void setSelected(GraphItem item) {
		graph.setSelection(item == null ? null : new GraphItem[] {item}); // the items selected.
	}
	
	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof RefreshLogicGraphEvent)
			create();
	}
}