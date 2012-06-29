package ui.display.views.structural.defuses;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ui.constants.Colors;
import ui.constants.Description;
import ui.constants.TableViewers;
import ui.display.views.structural.AbstractTableViewer;
import adt.graph.AbstractPath;
import adt.graph.Edge;
import adt.graph.Node;
import domain.events.DefUsesChangedEvent;
import domain.events.TestPathChangedEvent;
import domain.events.TestRequirementSelectedEvent;

public class DefUsesViewerByNodeEdge extends AbstractTableViewer implements IDefUsesViewer, Observer {

	private Composite parent;
	private TableViewer defUsesViewer;
	private Control defUsesControl;
	private IWorkbenchPartSite site;

	public DefUsesViewerByNodeEdge(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getDefUsesController().addObserverDefUses(this);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestPathController().addObserver(this);
	}

	public TableViewer create() {
		defUsesViewer = createViewTable(parent, site, TableViewers.DEFUSESVIEWER);
		defUsesControl = defUsesViewer.getControl();
		createColumnsToDefUses();
		setSelections(); // connect the view elements to the graph elements.
		return defUsesViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof DefUsesChangedEvent) {
			cleanDefUsesStatus();
			setDefUses(((DefUsesChangedEvent) data).nodeedgeDefUses);
		} else if(data instanceof TestRequirementSelectedEvent) {
			if(((TestRequirementSelectedEvent) data).selectedTestRequirement != null) {
				bringViewToTop();
				cleanDefUsesStatus();
				setDefUsesStatus(((TestRequirementSelectedEvent) data).selectedTestRequirement);
			}
		} else if(data instanceof TestPathChangedEvent) 
			cleanDefUsesStatus();
	}

	public void dispose() {
		defUsesControl.dispose();
		Activator.getDefault().getDefUsesController().deleteObserverDefUses(this);
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
	}

	private void createColumnsToDefUses() {
		String[] columnNames = new String[] {"", TableViewers.NODES_EDGES, TableViewers.DEFS, TableViewers.USES }; // the names of columns.
		int[] columnWidths = new int[] {50, 405, 405, 400}; // the width of columns.

		// first column is for id.
		TableViewerColumn col = createColumnsHeaders(defUsesViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
			}
		});
		
		// second column is for nodes and edges.
		col = createColumnsHeaders(defUsesViewer, columnNames[1], columnWidths[1], 1);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Object obj = cell.getElement();
				if(obj instanceof Node<?>) {
					Node<Integer> node = (Node<Integer>) obj;
					cell.setText(node.toString()); 
				}
				else if(obj instanceof Edge<?>) {
					Edge<Integer> edge = (Edge<Integer>) obj;
					cell.setText(edge.toString());
				}
			}
		});

		// third column is for definitions.
		col = createColumnsHeaders(defUsesViewer, columnNames[2], columnWidths[2], 2);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
			}
		});
		
		// third column is for uses.
		col = createColumnsHeaders(defUsesViewer, columnNames[3], columnWidths[3], 3);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
			}
		});
	}
	
	private void cleanDefUsesStatus() {
		int n = 0;
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			if(n % 2 == 0)
				item.setBackground(Colors.WHITE);
			else 
				item.setBackground(Colors.GREY);
			n++;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setDefUses(Map<Object, List<List<String>>> nodeedgeDefUses) {
		int n = 0;
		defUsesViewer.setInput(nodeedgeDefUses.keySet());
		Iterator<Object> keys = nodeedgeDefUses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			Object key = keys.next();
			if(key instanceof Node<?>) {
				Node<Integer> node = (Node<Integer>) key;
				String defs = "{ " + getDefUsesRepresentation(nodeedgeDefUses.get(node).get(0)) + " }";
				String uses = "{ " + getDefUsesRepresentation(nodeedgeDefUses.get(node).get(1)) + " }";
				item.setText(0, Integer.toString(n + 1));
				item.setText(1, node.toString());
				item.setText(2, defs);
				item.setText(3, uses);
			} else if(key instanceof Edge<?>) {
				Edge<Integer> edge = (Edge<Integer>) key;
				String defs = "{ " + getDefUsesRepresentation(nodeedgeDefUses.get(edge).get(0)) + " }";
				String uses = "{ " + getDefUsesRepresentation(nodeedgeDefUses.get(edge).get(1)) + " }";
				item.setText(0, Integer.toString(n + 1));
				item.setText(1, edge.toString());
				item.setText(2, defs);
				item.setText(3, uses);
			}
			n++;
		}	
	}
	
	private void setDefUsesStatus(AbstractPath<Integer> selectedTestRequirement) {
		Map<Object, List<List<String>>> defuses = Activator.getDefault().getDefUsesController().getDefUsesByNodeEdge();
		Iterator<Object> iterator = defuses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) 
			if(Activator.getDefault().getDefUsesController().getTestRequirementsToNode(iterator.next()).contains(selectedTestRequirement))
				item.setBackground(Colors.YELLOW_COVERAGE);
	}

	private String getDefUsesRepresentation(List<String> list) {
		String str = "";
		for(Object obj : list)
			str += obj.toString() + ", ";
		if(str.length() > 1)
			str = str.substring(0, str.length() - 2);
		return str;
	}
	
	private void bringViewToTop() {
		defUsesViewer.setSelection(null);
		Activator.getDefault().getDefUsesController().selectDefUse(null);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Description.VIEW_DATA_FLOW_COVERAGE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}		
	}

	private void setSelections() {
		defUsesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {
				cleanDefUsesStatus();
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); // get the selection.
				Object selected = selection.getFirstElement();
				Activator.getDefault().getDefUsesController().selectDefUse(selected);
		    }
		});
	}

}