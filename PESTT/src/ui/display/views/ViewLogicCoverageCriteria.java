package ui.display.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ui.display.views.logic.LogicCoverageCriteria;

public class ViewLogicCoverageCriteria extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		new LogicCoverageCriteria(parent);
	}

	@Override
	public void setFocus() {
	}
}