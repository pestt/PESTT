package domain.events;

import java.util.List;
import java.util.Map;

public class DefUsesChangedEvent {
	
	public Map<Object, List<String>> defuses;
	
	public DefUsesChangedEvent(Map<Object, List<String>> defuses) {
		this.defuses = defuses;
	}

}
