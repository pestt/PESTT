package ui.display.views.logic;

import static ui.constants.Tooltips.CACC;
import static ui.constants.Tooltips.CACC_NODE;
import static ui.constants.Tooltips.CC;
import static ui.constants.Tooltips.CC_NODE;
import static ui.constants.Tooltips.COC;
import static ui.constants.Tooltips.COC_NODE;
import static ui.constants.Tooltips.GACC;
import static ui.constants.Tooltips.GACC_NODE;
import static ui.constants.Tooltips.GICC;
import static ui.constants.Tooltips.GICC_NODE;
import static ui.constants.Tooltips.PC;
import static ui.constants.Tooltips.PC_NODE;
import static ui.constants.Tooltips.RACC;
import static ui.constants.Tooltips.RACC_NODE;
import static ui.constants.Tooltips.RICC;
import static ui.constants.Tooltips.RICC_NODE;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.draw2d.Label;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import ui.constants.Colors;
import ui.constants.Messages;
import ui.events.GraphChangeEvent;
import domain.constants.LogicCoverageCriteriaId;

public class LogicCoverageCriteria implements Observer {

	private Graph graph;
	private Map<LogicCoverageCriteriaId, GraphNode> nodes;
	private SelectionAdapter event;
	private Composite parent;

	public LogicCoverageCriteria(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		Activator.getDefault().getCFGController().addObserver(this);
		create();
		this.parent = parent;
	}

	private void create() {
		graph.clear();
		removeSelectionListener();
		setNodes();
		setEdges();
		setLayout();
		addSelectionListener();
		if (Activator.getDefault().getTestRequirementController()
				.isCoverageCriteriaSelected())
			setSelected(nodes.get(Activator.getDefault()
					.getTestRequirementController()
					.getSelectedCoverageCriteria()));
	}

	public void dispose() {
		Activator.getDefault().getCFGController().deleteObserver(this);
	}

	private void setNodes() {
		nodes = new HashMap<LogicCoverageCriteriaId, GraphNode>();
		GraphNode coc = new GraphNode(graph, SWT.SINGLE, COC_NODE
				+ insertTrace(16) + "\n            (CoC)");
		coc.setData(LogicCoverageCriteriaId.COMPLETE_CLAUSE);
		coc.setTooltip(new Label(COC));
		nodes.put(LogicCoverageCriteriaId.COMPLETE_CLAUSE, coc);

		GraphNode racc = new GraphNode(graph, SWT.SINGLE, RACC_NODE
				+ insertTrace(16) + "\n          (RACC)");
		racc.setData(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE);
		racc.setTooltip(new Label(RACC));
		nodes.put(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE, racc);

		GraphNode ricc = new GraphNode(graph, SWT.SINGLE, RICC_NODE
				+ insertTrace(18) + "\n           (RICC)");
		ricc.setData(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE);
		ricc.setTooltip(new Label(RICC));
		nodes.put(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE, ricc);

		GraphNode cacc = new GraphNode(graph, SWT.SINGLE, CACC_NODE
				+ insertTrace(17) + "\n           (CACC)");
		cacc.setData(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE);
		cacc.setTooltip(new Label(CACC

		));
		nodes.put(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE, cacc);

		GraphNode gacc = new GraphNode(graph, SWT.SINGLE, GACC_NODE
				+ insertTrace(16) + "\n          (GACC)");
		gacc.setData(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE);
		gacc.setTooltip(new Label(GACC));
		nodes.put(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE, gacc);

		GraphNode gicc = new GraphNode(graph, SWT.SINGLE, GICC_NODE
				+ insertTrace(16) + "\n          (GICC)");
		gicc.setData(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE);
		gicc.setTooltip(new Label(GICC));
		nodes.put(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE, gicc);

		GraphNode cc = new GraphNode(graph, SWT.SINGLE, CC_NODE
				+ insertTrace(9) + "\n     (CC)");
		cc.setData(LogicCoverageCriteriaId.CLAUSE);
		cc.setTooltip(new Label(CC));
		nodes.put(LogicCoverageCriteriaId.CLAUSE, cc);

		GraphNode pc = new GraphNode(graph, SWT.SINGLE, PC_NODE
				+ insertTrace(10) + "\n      (PC)");
		pc.setData(LogicCoverageCriteriaId.PREDICATE);
		pc.setTooltip(new Label(PC));
		nodes.put(LogicCoverageCriteriaId.PREDICATE, pc);

		for (GraphNode gnode : nodes.values()) {
			gnode.setBackgroundColor(Colors.WHITE);
			gnode.setForegroundColor(Colors.BLACK);
			gnode.setBorderColor(Colors.BLACK);
			gnode.setHighlightColor(Colors.YELLOW);
			gnode.setBorderHighlightColor(Colors.BLACK);
		}
	}

	private void setEdges() {
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.COMPLETE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.COMPLETE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.RESTRICTED_INACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.RESTRICTED_ACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.CORRELATED_ACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.PREDICATE));
		GraphConnection edge = new GraphConnection(graph,
				ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.GENERAL_ACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.CLAUSE));
		edge.setCurveDepth(50);
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.CLAUSE));
		new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
				nodes.get(LogicCoverageCriteriaId.GENERAL_INACTIVE_CLAUSE),
				nodes.get(LogicCoverageCriteriaId.PREDICATE));
	}

	private String insertTrace(int num) {
		String line = "";
		for (int i = 0; i < num; i++)
			line += (char) 0x2013 + "";
		return line;
	}

	private void setLayout() {
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				TreeLayoutAlgorithm.TOP_DOWN), true);
//				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
				
	}

	private void addSelectionListener() {
		event = new SelectionAdapter() { // create a new SelectionAdapter event.

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item != null && e.item instanceof GraphNode) {
					setSelected(null);
					setSelected((GraphItem) e.item);
					getSelected().getData();
				}
			}
		};
		graph.addSelectionListener(event);
	}

	private void removeSelectionListener() {
		if (event != null)
			graph.removeSelectionListener(event);
	}

	private GraphItem getSelected() {
		if (graph.getSelection().size() == 0)
			MessageDialog.openInformation(parent.getShell(),
					Messages.GRAPH_ITEM_TITLE, Messages.GRAPH_ITEM_NOT_FOUND);
		return (GraphItem) graph.getSelection().get(0); // return the list with the selected nodes.
	}

	private void setSelected(GraphItem item) {
		graph.setSelection(item == null ? null : new GraphItem[] { item }); // the items selected.
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof GraphChangeEvent)
			create();
	}
}