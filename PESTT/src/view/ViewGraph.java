package view;

import java.util.ArrayList;
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

	public void create(ArrayList<String> location) {
		for(Control control : parent.getChildren()) // dispose all active controls.
			control.dispose();
		GraphsCreator.INSTANCE.createGraphs(parent, location); // create the new viewGraph.
	}
}