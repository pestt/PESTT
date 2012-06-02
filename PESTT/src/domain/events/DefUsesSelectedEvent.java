package domain.events;

public class DefUsesSelectedEvent {
	
	public final Object selectedDefUse;

	public DefUsesSelectedEvent(Object selected) {
		this.selectedDefUse = selected;
	}

}
