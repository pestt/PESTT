package ui.display.views.structural;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import ui.constants.TableViewers;

public enum TableViewerFactory {
	
	INSTANCE;

	public TableViewer createTebleViewer(Composite parent, IWorkbenchPartSite site, String name) {
		switch(TableViewers.valueOf(name)) {
			case TESTREQUIREMENTSVIEWER:
				return new TestRequirementsViewer(parent, site).create();
			case TESTPATHSVIEWER:
				return new TestPathsViewer(parent, site).create();
			case STATISTICSVIEWER:
				return new StatisticsViewer(parent, site).create();
			default:
				return null;
		}
	}
}

