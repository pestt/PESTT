package domain.events;

import java.util.List;
import java.util.Map;

public class DefUsesChangedEvent {
	
	public Map<String, List<String>> defuses;
	
	public DefUsesChangedEvent(Map<String, List<String>> defuses) {
		this.defuses = defuses;
	}

}
