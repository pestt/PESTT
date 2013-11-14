package ui.display.views.structural;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.constants.TableViewers;
import ui.display.views.structural.defuses.DefUsesViewerFactory;

public enum TableViewerFactory {

	INSTANCE;

	public TableViewer createTableViewer(Composite parent,
			IWorkbenchPartSite site, String name) {
		switch (TableViewers.valueOf(name)) {
		case TESTREQUIREMENTSVIEWER:
			return new TestRequirementsViewer(parent, site).create();
		case TESTPATHSVIEWER:
			return new TestPathsViewer(parent, site).create();
		case STATISTICSVIEWER:
			return new StatisticsViewer(parent, site).create();
		case DEFUSESVIEWER:
			return new DefUsesViewerFactory().createTableViewer(parent, site);
		default:
			return null;//TODO
		}
	}
}
