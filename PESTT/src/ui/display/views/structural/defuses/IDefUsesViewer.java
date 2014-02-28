package ui.display.views.structural.defuses;

import org.eclipse.jface.viewers.TableViewer;

public interface IDefUsesViewer {

	public TableViewer create();

	public void dispose();

	public void addObservers();

	public void deleteObservers();

}
