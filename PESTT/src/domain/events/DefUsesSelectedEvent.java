package domain.events;

import java.util.List;
import java.util.Set;


public class DefUsesSelectedEvent {
	
	public final Set<List<Object>> selectedDefUse;

	public DefUsesSelectedEvent(Set<List<Object>> selected) {
		this.selectedDefUse = selected;
	}

}
