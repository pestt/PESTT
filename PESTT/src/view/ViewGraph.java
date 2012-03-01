package view;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

public class ViewGraph extends ViewPart  {

	private Composite parent;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
	}
	
	@Override
	public void setFocus() {		
	}

	public void create(ICompilationUnit unit, String methodName) {
		for(Control control : parent.getChildren()) // dispose all active controls.
			control.dispose();
		GraphsCreator.INSTANCE.createGraphs(parent, unit, methodName); // create the new viewGraph.
	}
}