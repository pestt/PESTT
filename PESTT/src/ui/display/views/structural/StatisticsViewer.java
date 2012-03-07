package ui.display.views.structural;

import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;

import adt.graph.Path;

import domain.StatisticsChangedEvent;
import domain.TestPathChangedEvent;
import domain.TestPathSelected;
import domain.constants.TableViewers;

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
		statisticsViewer = createViewTable(parent, site, false);
		statisticsControl = statisticsViewer.getControl();
		createColumnsToStatisticsViewer();
		return statisticsViewer;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof StatisticsChangedEvent) {
			statisticsViewer.setInput(((StatisticsChangedEvent) data).statisticsSet);
		} else if(data instanceof TestPathSelected) {
			Object selected = ((TestPathSelected) data).selected;
			if(selected != null)
				if(selected instanceof Path<?>) 
					Activator.getDefault().getStatisticsController().getIndividualStatistics();
				else
					Activator.getDefault().getStatisticsController().getTotalStatistics();
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
