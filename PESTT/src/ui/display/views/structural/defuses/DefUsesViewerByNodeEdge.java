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

import ui.constants.TableViewers;
import ui.display.views.structural.AbstractTableViewer;
import adt.graph.Edge;
import adt.graph.Node;
import domain.events.DefUsesChangedEvent;

public class DefUsesViewerByNodeEdge extends AbstractTableViewer implements IDefUsesViewer, Observer {

	private Composite parent;
	private TableViewer defUsesViewer;
	private Control defUsesControl;
	private IWorkbenchPartSite site;

	public DefUsesViewerByNodeEdge(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getDefUsesController().addObserverDefUses(this);
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
		if(data instanceof DefUsesChangedEvent) 
			setDefUses(((DefUsesChangedEvent) data).defuses);
	}

	public void dispose() {
		defUsesControl.dispose();
		Activator.getDefault().getDefUsesController().deleteObserverDefUses(this);
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
	
	@SuppressWarnings("unchecked")
	private void setDefUses(Map<Object, List<String>> defuses) {
		int n = 0;
		defUsesViewer.setInput(defuses.keySet());
		Iterator<Object> keys = defuses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			Object obj = keys.next();
			if(obj instanceof Node<?>) {
				Node<Integer> node = (Node<Integer>) obj;
				String defs = defuses.get(node).get(0);
				String uses = defuses.get(node).get(1);
				item.setText(0, Integer.toString(n + 1));
				item.setText(1, node.toString());
				item.setText(2, defs);
				item.setText(3, uses);
			} else if(obj instanceof Edge<?>) {
				Edge<Integer> edge = (Edge<Integer>) obj;
				String defs = defuses.get(edge).get(0);
				String uses = defuses.get(edge).get(1);
				item.setText(0, Integer.toString(n + 1));
				item.setText(1, edge.toString());
				item.setText(2, defs);
				item.setText(3, uses);
			}
			n++;
		}	
	}
	
	private void setSelections() {
		defUsesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); // get the selection.
				Object selected = selection.getFirstElement();
				Activator.getDefault().getDefUsesController().selectDefUse(selected);
		    }
		});
	}

}