package ui.display.views.structural;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.StatusImages;
import ui.constants.Colors;
import ui.constants.Images;
import ui.constants.Messages;
import ui.constants.TableViewers;
import ui.events.TourChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.events.DefUsesSelectedEvent;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;
import domain.events.TestRequirementChangedEvent;

public class TestRequirementsViewer extends AbstractTableViewer implements Observer {

	private Composite parent;
	private TableViewer testRequirementsViewer;
	private Control testRequirementsControl;
	private IWorkbenchPartSite site;

	public TestRequirementsViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getTestRequirementController().addObserverTestRequirement(this);
		Activator.getDefault().getTestPathController().addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getDefUsesController().addObserver(this);
	}

	public TableViewer create() {
		testRequirementsViewer = createViewTable(parent, site, TableViewers.TESTREQUIREMENTSVIEWER);
		testRequirementsControl = testRequirementsViewer.getControl();
		createColumnsToTestRequirement();
		setSelections(); // connect the view elements to the graph elements.
		return testRequirementsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof TestRequirementChangedEvent) {
			if(((TestRequirementChangedEvent) data).hasInfinitePath)
				MessageDialog.openInformation(parent.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_INFINITE_MSG); // message displayed when the method contains cycles.
			Set<AbstractPath<Integer>> testRequirements = new TreeSet<AbstractPath<Integer>>();
			for(AbstractPath<Integer> path : ((TestRequirementChangedEvent) data).testRequirementSet)
				testRequirements.add(path);
			testRequirementsViewer.setInput(testRequirements);
			cleanPathStatus();
			setInfeasibles(((TestRequirementChangedEvent) data).infeasigles);
		} else if(data instanceof TestPathSelectedEvent || data instanceof TourChangeEvent) {
			Set<Path<Integer>> selectedTestPaths = Activator.getDefault().getTestPathController().getSelectedTestPaths();
			if(selectedTestPaths != null)
				if(!selectedTestPaths.isEmpty())
					setPathStatus();
				else
					cleanPathStatus();
		} else if(data instanceof TestPathChangedEvent) 
			cleanPathStatus();
		else if(data instanceof DefUsesSelectedEvent) {
			Object selectedDefUses = ((DefUsesSelectedEvent) data).selectedDefUse;
			if(selectedDefUses != null) {
				cleanPathStatus();
				setDefUsesStatus();
			}
		}
	}

	public void dispose() {
		testRequirementsControl.dispose();
	}

	private void createColumnsToTestRequirement() {
		String[] columnNames = new String[] {TableViewers.INFEASIBLE, TableViewers.STATUS, TableViewers.TEST_REQUIREMENTS}; // the names of columns.
		int[] columnWidths = new int[] {80, 55, 200}; // the width of columns.

		// first column is for the id.
		TableViewerColumn col = createColumnsHeaders(testRequirementsViewer, columnNames[0], columnWidths[0], 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
			}
		});

		// second column is for status.
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
				AbstractPath<?> path = (AbstractPath<?>) cell.getElement();
				cell.setText(path.toString());
			}
		});
	}
	
	private void cleanPathStatus() {
		int n = 0;
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			item.setImage(1, null);
			if(n % 2 == 0)
				item.setBackground(Colors.WHITE);
			else 
				item.setBackground(Colors.GREY);
			testRequirementsViewer.getTable().getItem(n).setText(0, Integer.toString(n + 1));
			n++;
		}
	}

	private void setPathStatus() {
		int n = 0;
		StatusImages images = new StatusImages();
		Set<Path<Integer>> coveredPaths = Activator.getDefault().getTestPathController().getTestRequirementCoverage();
		Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			AbstractPath<Integer> path = iterator.next();
			if(coveredPaths.contains(path)) {
				item.setText(0, Integer.toString(n + 1));
				item.setImage(1, images.getImage().get(Images.PASS));
				item.setBackground(Colors.GREEN_COVERAGE);
			} else {
				item.setText(0, Integer.toString(n + 1));
				item.setImage(1, images.getImage().get(Images.FAIL));
				item.setBackground(Colors.RED_COVERAGE);
			}
			n++;
		}
	}
	
	private void setInfeasibles(Iterable<AbstractPath<Integer>> infeasigles) {
		for(AbstractPath<Integer> path : infeasigles) 
			for(TableItem item : testRequirementsViewer.getTable().getItems()) {
				if(item.getText(2).equals(path.toString()) && !item.getChecked()) {
					item.setChecked(true);
					break;
				}
			}
	}

	private void setDefUsesStatus() {
		Set<AbstractPath<Integer>> testRequirementsOfSelected = Activator.getDefault().getDefUsesController().getTestRequirementsOfSelected();
		Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			AbstractPath<Integer> path = iterator.next();
			if(testRequirementsOfSelected.contains(path))
				item.setBackground(Colors.YELLOW_COVERAGE);
		}
	}

	private void setSelections() {
		testRequirementsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@SuppressWarnings("unchecked")
			public void selectionChanged(final SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); // get the selection.
				AbstractPath<Integer> selected = (AbstractPath<Integer>) selection.getFirstElement(); // get the path selected.
				Activator.getDefault().getTestRequirementController().selectTestRequirement(selected);
		    }
		});
		
		testRequirementsViewer.getTable().addListener(SWT.Selection, new Listener() {
		    
			public void handleEvent(Event event) {
		        if(event.detail == SWT.CHECK)
		        	for(TableItem item : testRequirementsViewer.getTable().getItems()) 
		        		if(item == event.item) {
		        			Activator.getDefault().getEditorController().removeALLMarkers();
		        			if(item.getChecked())
		        				Activator.getDefault().getTestRequirementController().enableInfeasible(Activator.getDefault().getTestRequirementController().getSelectedTestRequirement());
		        			else 
		        				Activator.getDefault().getTestRequirementController().disableInfeasible(Activator.getDefault().getTestRequirementController().getSelectedTestRequirement());
		        		}
		      }
		    });
	}
}