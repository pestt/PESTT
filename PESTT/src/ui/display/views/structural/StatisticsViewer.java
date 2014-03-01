package ui.display.views.structural;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ui.constants.Messages;
import ui.constants.TableViewers;
import ui.events.StatisticsChangedEvent;
import ui.events.TourChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Path;
import adt.graph.SequencePath;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;
import domain.events.TestRequirementChangedEvent;

public class StatisticsViewer extends AbstractTableViewer implements Observer {

	private Composite parent;
	private TableViewer statisticsViewer;
	private Control statisticsControl; // control of statisticsViewer.
	private IWorkbenchPartSite site;

	public StatisticsViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getStatisticsController()
				.addObserverStatistics(this);
		Activator.getDefault().getTestRequirementController()
				.addObserver(this);
		Activator.getDefault().getTestPathController()
				.addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
	}

	public TableViewer create() {
		statisticsViewer = createViewTable(parent, site,
				TableViewers.STATISTICSVIEWER);
		statisticsControl = statisticsViewer.getControl();
		createColumnsToStatisticsViewer();
		return statisticsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if (data instanceof StatisticsChangedEvent) {
			Set<String> statistics = new TreeSet<String>();
			Iterator<String> iterator = ((StatisticsChangedEvent) data).statisticsSet;
			while (iterator.hasNext())
				statistics.add(iterator.next());
			statisticsViewer.setInput(statistics);
		} else if (data instanceof TestPathSelectedEvent
				|| data instanceof TourChangeEvent) {
			if (Activator.getDefault().getEditorController()
					.isEverythingMatching()) {
				Set<Path> selectedTestPaths = Activator.getDefault()
						.getTestPathController().getSelectedTestPaths();
				if (selectedTestPaths != null)
					if (!selectedTestPaths.isEmpty())
						if (!containsSequencePaths())
							Activator.getDefault().getTestPathController()
									.getStatistics();
						else {
							IWorkbenchWindow window = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow();
							MessageDialog.openInformation(window.getShell(),
									Messages.STATISTICS_TITLE,
									Messages.STATISTICS_MSG + "\n"
											+ Messages.STATISTICS_REASON_MSG);
							List<String> msg = new ArrayList<String>();
							msg.add(Messages.STATISTICS_MSG);
							msg.add(Messages.STATISTICS_REASON_MSG);
							statisticsViewer.setInput(msg);
						}
					else
						Activator.getDefault().getStatisticsController()
								.cleanStatisticsSet();
			} else
				Activator.getDefault().getStatisticsController()
						.cleanStatisticsSet();
		} else if (data instanceof TestPathChangedEvent)
			Activator.getDefault().getStatisticsController()
					.cleanStatisticsSet();
		else if (data instanceof TestRequirementChangedEvent)
			Activator.getDefault().getStatisticsController()
					.cleanStatisticsSet();
	}

	public void dispose() {
		statisticsControl.dispose();
	}

	public void createColumnsToStatisticsViewer() {
		String columnNames = TableViewers.STATISTICS; // the names of column.
		int columnWidths = 700; // the width of column.
		TableViewerColumn col = createColumnsHeaders(statisticsViewer,
				columnNames, columnWidths, 0);
		col.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String str = (String) cell.getElement();
				cell.setText(str);
			}
		});
	}

	private boolean containsSequencePaths() {
		for (AbstractPath path : Activator.getDefault()
				.getTestRequirementController().getTestRequirements())
			if (path instanceof SequencePath)
				return true;
		return false;
	}
}
