package ui.display.views.structural.defuses;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
import domain.events.DefUsesChangedEvent;

public class DefUsesViewerByVariable extends AbstractTableViewer implements IDefUsesViewer, Observer {
	
	private Composite parent;
	private TableViewer defUsesViewer;
	private Control defUsesControl;
	private IWorkbenchPartSite site;

	public DefUsesViewerByVariable(Composite parent, IWorkbenchPartSite site) {
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
	
	private void setDefUses(Map<Object, List<String>> defuses) {
		int n = 0;
		Map<String, List<List<Object>>> variableDefUses = getdefUsesByVariables(defuses);
		defUsesViewer.setInput(variableDefUses.keySet());
		Iterator<String> keys = variableDefUses.keySet().iterator();
		for(TableItem item : defUsesViewer.getTable().getItems()) {
			String key = keys.next();
			String defs = "{" + getdefUsesRepresentation(variableDefUses.get(key).get(0)) + " }";
			String uses = "{" + getdefUsesRepresentation(variableDefUses.get(key).get(1)) + " }";
			item.setText(0, Integer.toString(n + 1));
			item.setText(1, key);
			item.setText(2, defs);
			item.setText(3, uses);
			n++;
		}
	}
	
	private String getdefUsesRepresentation(List<Object> list) {
		String str = "";
		for(Object obj : list)
			str += " " + obj.toString() + ",";
		if(str.length() > 2)
			str = str.substring(0, str.length() - 1);
		return str;
	}

	private Map<String, List<List<Object>>> getdefUsesByVariables(Map<Object, List<String>> defuses) {
		Map<String, List<List<Object>>> variablesDefUses = new LinkedHashMap<String, List<List<Object>>>();
		Set<String> vars = getVariables(defuses);
		for(String var : vars) {
			List<List<Object>> nodeedges = getNodesEdgesDefUses(defuses, var);
			variablesDefUses.put(var, nodeedges);
		}
		return variablesDefUses;
	}

	private List<List<Object>> getNodesEdgesDefUses(Map<Object, List<String>> defuses, String var) {
		List<List<Object>> nodeedges = new LinkedList<List<Object>>();
		List<Object> defs = new LinkedList<Object>();
		List<Object> uses = new LinkedList<Object>();
		for(Object obj : defuses.keySet()) {
			List<String> vars = defuses.get(obj);
			if(vars.get(0).contains(var))
				defs.add(obj);
			if(vars.get(1).contains(var))
				uses.add(obj);
		}
		nodeedges.add(defs);
		nodeedges.add(uses);
		return nodeedges;
	}

	private Set<String> getVariables(Map<Object, List<String>> defuses) {
		Set<String> vars = new TreeSet<String>();
		for(Object obj : defuses.keySet()) {
			List<String> variables = defuses.get(obj);
			vars.addAll(parseVariables(variables.get(0)));
			vars.addAll(parseVariables(variables.get(1)));
		}
		return vars;
	}

	private List<String> parseVariables(String input) {
		List<String> vars = new LinkedList<String>();
		StringTokenizer strtok = new StringTokenizer(input, ", ");
		while(strtok.hasMoreTokens())
			vars.add(strtok.nextToken());
		return vars;
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
