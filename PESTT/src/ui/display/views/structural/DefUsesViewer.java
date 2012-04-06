package ui.display.views.structural;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.constants.TableViewers;
import domain.events.DefUsesChangedEvent;

public class DefUsesViewer extends AbstractTableViewer implements ITableViewer, Observer {

	private Composite parent;
	private TableViewer defUsesViewer;
	private Control defUsesControl;
	private IWorkbenchPartSite site;

	public DefUsesViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getDefUsesController().addObserverDefUses(this);
	}

	public TableViewer create() {
		defUsesViewer = createViewTable(parent, site, TableViewers.DEFUSESVIEWER);
		defUsesControl = defUsesViewer.getControl();
		createColumnsToDefUses();
		return defUsesViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof DefUsesChangedEvent) 
			setDefUses(((DefUsesChangedEvent) data).defuses);
	}

	public void dispose() {
		defUsesControl.dispose();
	}

	private void createColumnsToDefUses() {
		String[] columnNames = new String[] { "", TableViewers.NODES_EDGES, TableViewers.DEFS, TableViewers.USES }; // the names of columns.
		int[] columnWidths = new int[] {50, 410, 410, 410}; // the width of columns.

		// first column is for id.
		TableViewerColumn col = createColumnsHeaders(defUsesViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String str = (String) cell.getElement();
				cell.setText(str);
			}
		});
		
		// second column is for nodes.
		col = createColumnsHeaders(defUsesViewer, columnNames[1], columnWidths[1], 1);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
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
	
	private void setDefUses(Map<String, List<String>> defuses) {
		setId(defuses);
		Iterator<String> keys = defuses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			String node = keys.next();
			String defs = defuses.get(node).get(0);
			String uses = defuses.get(node).get(1);
			item.setText(1, node);
			item.setText(2, defs);
			item.setText(3, uses);
		} 
	}

	private void setId(Map<String, List<String>> defuses) {
		List<String> empty = new ArrayList<String>();
		int i = 0;
		while(i < defuses.size()) {
			empty.add(Integer.toString(i));
			i++;
		}
		defUsesViewer.setInput(empty);
	}

}
