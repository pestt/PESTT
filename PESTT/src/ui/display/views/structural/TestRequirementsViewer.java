package ui.display.views.structural;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
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
import domain.events.TestRequirementSelectedCriteriaEvent;

public class TestRequirementsViewer extends AbstractTableViewer implements Observer {

	private Composite parent;
	private TableViewer testRequirementsViewer;
	private Control testRequirementsControl;
	private IWorkbenchPartSite site;

	public TestRequirementsViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getTestRequirementController().addObserverTestRequirement(this);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestPathController().addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getDefUsesController().addObserver(this);
	}

	/***
	 * Creates the table viewer for the Test Requirements.
	 * 
	 * @return TableViewer - The Table Viewer.
	 */
	public TableViewer create() {
		testRequirementsViewer = createViewTable(parent, site, TableViewers.TESTREQUIREMENTSVIEWER);
		testRequirementsControl = testRequirementsViewer.getControl();
		createColumnsToTestRequirement();
		setSelections(); // connect the view elements to the graph elements.
		return testRequirementsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof TestRequirementSelectedCriteriaEvent)
			Activator.getDefault().getTestRequirementController().cleanTestRequirementSet();
		else if(data instanceof TestRequirementChangedEvent) {
			cleanPathStatus();
			if(((TestRequirementChangedEvent) data).hasInfinitePath)
				MessageDialog.openInformation(parent.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_INFINITE_MSG); // message displayed when the method contains cycles.
			Set<AbstractPath<Integer>> testRequirements = new TreeSet<AbstractPath<Integer>>();
			for(AbstractPath<Integer> path : ((TestRequirementChangedEvent) data).testRequirementSet)
				testRequirements.add(path);
			testRequirementsViewer.setInput(testRequirements);
			setInfeasibles(((TestRequirementChangedEvent) data).infeasigles);
		} else if(data instanceof TestPathSelectedEvent || data instanceof TourChangeEvent) {
			if(Activator.getDefault().getEditorController().isEverythingMatching()) {
				Set<Path<Integer>> selectedTestPaths = Activator.getDefault().getTestPathController().getSelectedTestPaths();
				if(selectedTestPaths != null && !selectedTestPaths.isEmpty()) {
					testRequirementsViewer.setSelection(null);
					Activator.getDefault().getTestRequirementController().selectTestRequirement(null);	
					setPathStatus();
				} else
					cleanPathStatus();
			} else
				cleanPathStatus();
		} else if(data instanceof TestPathChangedEvent) 
			cleanPathStatus();
		else if(data instanceof DefUsesSelectedEvent) {
			Object selectedDefUses = ((DefUsesSelectedEvent) data).selectedDefUse;
			if(selectedDefUses != null) {
				cleanPathStatus();
				testRequirementsViewer.setSelection(null);
				Activator.getDefault().getTestRequirementController().selectTestRequirement(null);
				setDefUsesStatus();
			}
		}
	}

	public void dispose() {
		testRequirementsControl.dispose();
	}

	/***
	 * Create the table columns.
	 */
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

		// third column is for test requirement paths.
		col = createColumnsHeaders(testRequirementsViewer, columnNames[2], columnWidths[2], 2);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				AbstractPath<?> path = (AbstractPath<?>) cell.getElement();
				cell.setText(path.toString());
			}
		});
	}
	
	/***
	 * Clears all visual status (Colors and images).
	 */
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

	/***
	 * Sets the visual status for the test requirements according to the selected test path.
	 * The visual status to the test requirements are:
	 * - Green (Background and pass icon) if the test requirement is covered by the selected test path.
	 * - Red (Background and cross icon) if the test requirement is not covered by the selected test path.
	 * - Blue (Background and pass icon) if the test requirement is infeasible.
	 */
	private void setPathStatus() {
		int n = 0;
		StatusImages images = new StatusImages();
		Set<Path<Integer>> coveredPaths = Activator.getDefault().getTestPathController().getTestRequirementCoverage();
		Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			AbstractPath<Integer> path = iterator.next();
			if(coveredPaths.contains(path)) {
				if(item.getChecked()) { 
					MessageDialog.openInformation(parent.getShell(), Messages.TEST_REQUIREMENT_TITLE, "The path " + path.toString() + Messages.TEST_REQUIREMENT_NOT_INFEASIBLE_MSG);
					item.setChecked(false);
					Activator.getDefault().getTestRequirementController().disableInfeasible(path);
					Activator.getDefault().getTestPathController().getStatistics();
				}
				item.setText(0, Integer.toString(n + 1));
				item.setImage(1, images.getImage().get(Images.PASS));
				item.setBackground(Colors.GREEN_COVERAGE);
			} else if(item.getText(2).equals(path.toString()) && item.getChecked()) {
				item.setText(0, Integer.toString(n + 1));
				item.setImage(1, images.getImage().get(Images.PASS));
				item.setBackground(Colors.INFEASIBLE_COVERAGE);
			} else {
				item.setText(0, Integer.toString(n + 1));
				item.setImage(1, images.getImage().get(Images.FAIL));
				item.setBackground(Colors.RED_COVERAGE);
			}
			n++;
		}
	}
	
	/***
	 * Mark all infeasible path in the view.
	 * 
	 * @param infeasigles - The set of infeasible path.
	 */
	private void setInfeasibles(Iterable<AbstractPath<Integer>> infeasigles) {
		for(AbstractPath<Integer> path : infeasigles) 
			for(TableItem item : testRequirementsViewer.getTable().getItems()) {
				if(item.getText(2).equals(path.toString()) && !item.getChecked()) {
					item.setChecked(true);
					break;
				}
			}
	}

	/***
	 * Show all test requirements associated to the selected def-use (variable or node).
	 * The test requirements appear in yellow.
	 */
	private void setDefUsesStatus() {
		Set<AbstractPath<Integer>> testRequirementsOfSelected = Activator.getDefault().getDefUsesController().getTestRequirementsOfSelected();
		Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		if(testRequirementsOfSelected != null) {
			for(TableItem item : testRequirementsViewer.getTable().getItems()) {
				AbstractPath<Integer> path = iterator.next();
				if(testRequirementsOfSelected.contains(path))
					item.setBackground(Colors.YELLOW_COVERAGE);
			}
		} else 
			MessageDialog.openInformation(parent.getShell(), Messages.TEST_REQUIREMENT_TITLE, Messages.TEST_REQUIREMENT_NEED_UPDATE_MSG);
	}

	/***
	 * Sets the selection listener for this view.
	 *
	 * In this view exists two types of listener:
	 * - One for the row selection - selects one test requirement.
	 * - One for the checkbox selection - enable/disable a infeasible path.
	 *
	 * Note that when a user select the checkbox (to enable or disable one infeasible path) 
	 * the listener fired two evens:
	 * - one corresponding to the enable/disable of the checkbox.
	 * - and one for the row selection. 
	 */
	private void setSelections() {	
		testRequirementsViewer.getTable().addListener(SWT.Selection, new Listener() {
		    			
			public void handleEvent(Event event) {
				cleanPathStatus();
		        if(event.detail == SWT.CHECK) { // when user enable/disable an infeasible path.
		        	Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		    		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
		    			AbstractPath<Integer> selected = iterator.next();
		        		if(item == event.item) {
		        			Activator.getDefault().getEditorController().removeALLMarkers();
		        			if(item.getChecked())
		        				Activator.getDefault().getTestRequirementController().enableInfeasible(selected);
		        			else 
		        				Activator.getDefault().getTestRequirementController().disableInfeasible(selected);
		        			break;
		        		}
		    		}
		        } else if(event.detail == SWT.NONE) { // when user selects a table row.
		    		Iterator<AbstractPath<Integer>> iterator = Activator.getDefault().getTestRequirementController().getTestRequirements().iterator();
		    		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
		    			AbstractPath<Integer> selected = iterator.next();
		    			if(item == event.item) {
		    				Activator.getDefault().getTestRequirementController().selectTestRequirement(selected);
		    				break;
		    			}
		    		}
		        }
			}
		}); 
	}
}