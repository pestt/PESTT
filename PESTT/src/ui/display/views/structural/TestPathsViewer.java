package ui.display.views.structural;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.constants.Colors;
import ui.constants.Description;
import ui.constants.TableViewers;
import ui.events.TourChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Graph;
import adt.graph.Path;
import domain.events.DefUsesChangedEvent;
import domain.events.DefUsesSelectedEvent;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;
import domain.events.TestRequirementChangedEvent;
import domain.events.TestRequirementSelectedCriteriaEvent;
import domain.events.TestRequirementSelectedEvent;

public class TestPathsViewer extends AbstractTableViewer implements Observer {

	private Composite parent;
	private TableViewer testPathhsViewer;
	private Control testPathsControl; // control of executedGraphViewer.
	private IWorkbenchPartSite site;
	private Listener listener;

	public TestPathsViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getTestPathController().addObserver(this);
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getDefUsesController().addObserverDefUses(this);
		Activator.getDefault().getDefUsesController().addObserver(this);
	}

	public TableViewer create() {
		testPathhsViewer = createViewTable(parent, site,
				TableViewers.TESTPATHSVIEWER);
		testPathsControl = testPathhsViewer.getControl();
		createColumnsToExecutedGraphViewer();
		setSelections(); // associate path to the ViewGraph elements.
		return testPathhsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof TestRequirementSelectedCriteriaEvent)
			Activator.getDefault().getTestPathController().unSelectTestPaths();
		else if (data instanceof TestRequirementChangedEvent
				|| data instanceof DefUsesChangedEvent)
			Activator.getDefault().getTestPathController().unSelectTestPaths();
		else if (data instanceof DefUsesSelectedEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching()) {
				if (Activator.getDefault().getDefUsesController()
						.isDefUseSelected())
					Activator.getDefault().getTestPathController()
							.unSelectTestPaths();
			} else
				testPathhsViewer.setSelection(null);
		} else if (data instanceof TestRequirementSelectedEvent
				|| data instanceof TourChangeEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching()) {
				if (Activator.getDefault().getTestRequirementController()
						.isTestRequirementSelected()) {
					Activator.getDefault().getTestPathController()
							.unSelectTestPaths();
					setPathStatus(Activator.getDefault()
							.getTestRequirementController()
							.getSelectedTestRequirement());
				}
			} else
				testPathhsViewer.setSelection(null);
		} else if (data instanceof TestPathChangedEvent) {
			List<Object> testPaths = new ArrayList<Object>();
			Set<Path> paths = getPathSet(
					((TestPathChangedEvent) data).testPathSet,
					((TestPathChangedEvent) data).manuallyAdded);
			for (Path path : paths)
				testPaths.add(path);
			if (testPaths.size() > 1)
				testPaths.add(Description.TOTAL);
			testPathhsViewer.setInput(testPaths);
			if (listener != null)
				testPathhsViewer.getTable().removeListener(SWT.MouseHover,
						listener);
			addTooltips();
		} else if (data instanceof TestPathSelectedEvent) {
			cleanPathStatus();
			if (!Activator.getDefault().getTestPathController()
					.isTestPathSelected())
				testPathhsViewer.setSelection(null);
		}
	}

	private Set<Path> getPathSet(Iterable<Path> automatic,
			Iterable<Path> manually) {
		Set<Path> set = new TreeSet<Path>();
		for (Path path : automatic)
			set.add(path);
		for (Path path : manually)
			set.add(path);
		return set;
	}

	public void dispose() {
		testPathsControl.dispose();
	}

	private void createColumnsToExecutedGraphViewer() {
		String columnNames = TableViewers.TEST_PATTHS; // the names of column.
		int columnWidths = 500; // the width of column.
		TableViewerColumn col = createColumnsHeaders(testPathhsViewer,
				columnNames, columnWidths, 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() instanceof Graph) {
					Graph graph = (Graph) cell.getElement();
					cell.setText(graph.toString());
				} else if (cell.getElement() instanceof Path) {
					Path path = (Path) cell.getElement();
					cell.setText(path.toString());
				} else {
					String str = (String) cell.getElement();
					cell.setText(str);
				}
			}
		});
	}

	/***
	 * Clears all visual status (Colors).
	 */
	private void cleanPathStatus() {
		int n = 0;
		for (TableItem item : testPathhsViewer.getTable().getItems()) {
			if (n % 2 == 0)
				item.setBackground(Colors.WHITE);
			else
				item.setBackground(Colors.GREY);
			n++;
		}
	}

	/***
	 * Sets the visual status for the test path according to the selected test
	 * requirement. The visual status to the test path are:
	 * <ul>
	 * <li>Green (Background) if the selected test requirement is covered by the
	 * test path.</li>
	 * <li>Red (Background) if the selected test requirement is not covered by
	 * the test path.</li>
	 * </ul>
	 */
	private void setPathStatus(AbstractPath testRequirement) {
		Set<Path> paths = getPathSet(Activator.getDefault()
				.getTestPathController().getTestPaths(), Activator.getDefault()
				.getTestPathController().getManuallyAddedTestPaths());
		Iterator<Path> iterator = paths.iterator();
		for (TableItem item : testPathhsViewer.getTable().getItems()) {
			if (iterator.hasNext()) {
				Path path = iterator.next();
				Set<Path> coveredPaths = Activator.getDefault()
						.getTestRequirementController()
						.getTestPathCoverage(path);
				if (coveredPaths.contains(testRequirement))
					item.setBackground(Colors.YELLOW_COVERAGE);
			}
		}
	}

	private void setSelections() {
		testPathhsViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@SuppressWarnings("unchecked")
					public void selectionChanged(
							final SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection(); // get the selection.
						if (selection.getFirstElement() != null) {
							boolean hasTotal = false;
							Set<Path> testPaths = new TreeSet<Path>();
							for (Object obj : selection.toList())
								if (obj instanceof String) {
									hasTotal = true;
									break;
								} else
									testPaths.add((Path) obj);
							if (hasTotal) {
								testPaths.clear();
								for (Path path : Activator
										.getDefault().getTestPathController()
										.getManuallyAddedTestPaths())
									testPaths.add(path);
								for (Path path : Activator
										.getDefault().getTestPathController()
										.getTestPaths())
									testPaths.add(path);
							}
							Activator.getDefault().getTestPathController()
									.selectTestPath(testPaths);
						}
					}
				});
	}

	private void addTooltips() {
		listener = new Listener() {

			public void handleEvent(Event event) {
				Point coords = new Point(event.x, event.y);
				TableItem item = testPathhsViewer.getTable().getItem(coords);
				Set<Path> set = getPathSet(Activator.getDefault()
						.getTestPathController().getTestPaths(), Activator
						.getDefault().getTestPathController()
						.getManuallyAddedTestPaths());
				Iterator<Path> iterator = set.iterator();
				Path path = null;
				for (TableItem i : testPathhsViewer.getTable().getItems()) {
					path = null;
					if (iterator.hasNext()) {
						path = iterator.next();
						if (i == item)
							break;
					}
				}
				if (path != null)
					testPathhsViewer.getTable().setToolTipText(
							Activator.getDefault().getTestPathController()
									.getExecutionTip(path));
				else
					testPathhsViewer.getTable().setToolTipText("");
			}
		};
		testPathhsViewer.getTable().addListener(SWT.MouseHover, listener);
	}
}