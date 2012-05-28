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

import ui.constants.Colors;
import ui.constants.TableViewers;
import ui.display.views.structural.AbstractTableViewer;
import adt.graph.AbstractPath;
import domain.events.DefUsesChangedEvent;
import domain.events.TestPathChangedEvent;
import domain.events.TestRequirementSelectedEvent;

public class DefUsesViewerByVariable extends AbstractTableViewer implements IDefUsesViewer, Observer {
	
	private Composite parent;
	private TableViewer defUsesViewer;
	private Control defUsesControl;
	private IWorkbenchPartSite site;

	public DefUsesViewerByVariable(Composite parent, IWorkbenchPartSite site) {
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
			setDefUses(((DefUsesChangedEvent) data).variableDefUses);
		} else if(data instanceof TestRequirementSelectedEvent) {
			cleanDefUsesStatus();
			setDefUsesStatus(((TestRequirementSelectedEvent) data).selectedTestRequirement);
		} else if(data instanceof TestPathChangedEvent) 
			cleanDefUsesStatus();
	}

	public void dispose() {
		defUsesControl.dispose();
		Activator.getDefault().getDefUsesController().deleteObserverDefUses(this);
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
	}

	private void createColumnsToDefUses() {
		String[] columnNames = new String[] {"", TableViewers.VARIABLES, TableViewers.DEFS, TableViewers.USES }; // the names of columns.
		int[] columnWidths = new int[] {50, 405, 405, 400}; // the width of columns.

		// first column is for id.
		TableViewerColumn col = createColumnsHeaders(defUsesViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
			}
		});
		
		// second column is for variables.
		col = createColumnsHeaders(defUsesViewer, columnNames[1], columnWidths[1], 1);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String str = (String) cell.getElement();;
				cell.setText(str); 
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
	
	private void setDefUses(Map<String, List<List<Object>>> variableDefUses) {
		int n = 0;
		defUsesViewer.setInput(variableDefUses.keySet());
		Iterator<String> keys = variableDefUses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			String key = keys.next();
			String defs = "{ " + getDefUsesRepresentation(variableDefUses.get(key).get(0)) + " }";
			String uses = "{ " + getDefUsesRepresentation(variableDefUses.get(key).get(1)) + " }";
			item.setText(0, Integer.toString(n + 1));
			item.setText(1, key);
			item.setText(2, defs);
			item.setText(3, uses);
			n++;
		}
	}
	
	private void setDefUsesStatus(AbstractPath<Integer> selectedTestRequirement) {
		Map<String, List<List<Object>>> defuses = Activator.getDefault().getDefUsesController().getDefUsesByVariable();
		Iterator<String> iterator = defuses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) 
			if(Activator.getDefault().getDefUsesController().getTestRequirementsToVariable(iterator.next()).contains(selectedTestRequirement))
				item.setBackground(Colors.YELLOW_COVERAGE);
	}
	
	private String getDefUsesRepresentation(List<Object> list) {
		String str = "";
		for(Object obj : list)
			str += obj.toString() + ", ";
		if(str.length() > 1)
			str = str.substring(0, str.length() - 2);
		return str;
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
