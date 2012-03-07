package ui.display.views.structural;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
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

import ui.display.views.StatusImages;
import adt.graph.Path;
import domain.TestPathChangedEvent;
import domain.TestPathSelected;
import domain.TestRequirementChangedEvent;
import domain.constants.Colors;
import domain.constants.Images;
import domain.constants.Messages;
import domain.constants.TableViewers;

public class TestRequirementsViewer extends AbstractTableViewer implements ITableViewer, Observer {

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
	}

	public TableViewer create() {
		testRequirementsViewer = createViewTable(parent, site, true);
		testRequirementsControl = testRequirementsViewer.getControl();
		createColumnsToTestRequirement();
		setSelections(); // associate path to the ViewGraph elements.
		return testRequirementsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof TestRequirementChangedEvent) {
			if(((TestRequirementChangedEvent) data).hasInfinitePath)
				MessageDialog.openInformation(parent.getShell(), Messages.TEST_REQUIREMENT_INPUT_TITLE, Messages.TEST_REQUIREMENT_INFINITE_MSG); // message displayed when the method contains cycles.
			testRequirementsViewer.setInput(((TestRequirementChangedEvent) data).testRequirementSet);
			cleanPathStatus();
		} else if(data instanceof TestPathSelected)
			setPathStatus(((TestPathSelected) data).selected);
		else if(data instanceof TestPathChangedEvent)
			cleanPathStatus();
	}
	
	public void dispose() {
		testRequirementsControl.dispose();
	}

	private void createColumnsToTestRequirement() {
		String[] columnNames = new String[] { TableViewers.INFEASIBLE, TableViewers.STATUS, TableViewers.TEST_REQUIREMENTS }; // the names of columns.
		int[] columnWidths = new int[] {80, 55, 50}; // the width of columns.

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
				Path<?> path = (Path<?>) cell.getElement();
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
			testRequirementsViewer.getTable().getItem(n).setText(0, Integer.toString(n));
			n++;
		}
	}

	@SuppressWarnings("unchecked")
	private void setPathStatus(Object selected) {
		int n = 0;
		StatusImages images = new StatusImages();
		List<Path<Integer>> coveredPaths = null;
		if(selected instanceof Path<?>) {
			Path<Integer> selectedTestPath = (Path<Integer>) Activator.getDefault().getTestPathController().getSelectedTestPath();
			coveredPaths = Activator.getDefault().getTestRequirementController().getTestPathCoverage(selectedTestPath);
		} else
			coveredPaths = Activator.getDefault().getTestRequirementController().getTotalTestPathCoverage();
		Iterator<Path<Integer>> iterator = Activator.getDefault().getTestRequirementController().iterator();
		for(TableItem item : testRequirementsViewer.getTable().getItems()) {
			Path<Integer> path = iterator.next();
			if(coveredPaths.contains(path)) {
				item.setText(0, Integer.toString(n));
				item.setImage(1, images.getImage().get(Images.PASS));
				item.setBackground(Colors.GREEN_COVERAGE);
			} else {
				item.setText(0, Integer.toString(n));
				item.setImage(1, images.getImage().get(Images.FAIL));
				item.setBackground(Colors.RED_COVERAGE);
			}
			n++;
		}
	}
	
	private void setSelections() {
		testRequirementsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@SuppressWarnings("unchecked")
			public void selectionChanged(final SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); // get the selection.
				Path<Integer> selected = (Path<Integer>) selection.getFirstElement(); // get the path selected.
				Activator.getDefault().getTestRequirementController().selectTestRequirement(selected);
		    }
		});
	}
}