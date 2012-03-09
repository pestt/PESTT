package ui.display.views.structural;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import main.activator.Activator;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.constants.TableViewers;
import ui.events.StatisticsChangedEvent;
import domain.events.TestPathChangedEvent;
import domain.events.TestPathSelectedEvent;

public class StatisticsViewer extends AbstractTableViewer implements ITableViewer, Observer {
	
	private Composite parent;
	private TableViewer statisticsViewer;
	private Control statisticsControl; // control of statisticsViewer.
	private IWorkbenchPartSite site;

	public StatisticsViewer(Composite parent, IWorkbenchPartSite site) {
		this.parent = parent;
		this.site = site;
		Activator.getDefault().getStatisticsController().addObserverStatistics(this);
		Activator.getDefault().getTestPathController().addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
	}
	
	public TableViewer create() {
		statisticsViewer = createViewTable(parent, site, TableViewers.STATISTICSVIEWER);
		statisticsControl = statisticsViewer.getControl();
		createColumnsToStatisticsViewer();
		return statisticsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof StatisticsChangedEvent) {
			Set<String> statistics = new TreeSet<String>();
			Iterator<String> iterator = ((StatisticsChangedEvent) data).statisticsSet;
			while(iterator.hasNext())
				statistics.add(iterator.next());
			statisticsViewer.setInput(statistics);
		} else if(data instanceof TestPathSelectedEvent) {
			if(((TestPathSelectedEvent) data).selectedTestPaths != null)
				if(!((TestPathSelectedEvent) data).selectedTestPaths.isEmpty())
					Activator.getDefault().getTestPathController().getStatistics();
				else
					Activator.getDefault().getStatisticsController().cleanStatistics();
		} else if(data instanceof TestPathChangedEvent)
			Activator.getDefault().getStatisticsController().cleanStatistics();
	}

	public void dispose() {
		statisticsControl.dispose();
	}
	
	public void createColumnsToStatisticsViewer() {
		String columnNames = TableViewers.STATISTICS; // the names of column.
		int columnWidths = 200; // the width of column.
		TableViewerColumn col = createColumnsHeaders(statisticsViewer, columnNames, columnWidths, 0);
		col.setLabelProvider(new StyledCellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				String str = (String) cell.getElement();
				cell.setText(str);
			}
		});
	}
}
