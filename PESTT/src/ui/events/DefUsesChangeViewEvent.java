package ui.events;

import domain.constants.DefUsesView;

public class DefUsesChangeViewEvent {
	
	public final DefUsesView selectedDefUseView;

	public DefUsesChangeViewEvent(DefUsesView selected) {
		this.selectedDefUseView = selected;
	}

}
