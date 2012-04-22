package domain.events;

import java.util.List;
import java.util.Map;

public class DefUsesChangedEvent {
	
	public Map<Object, List<List<String>>> nodeedgeDefUses;
	public Map<String, List<List<Object>>> variableDefUses;
	
	
	public DefUsesChangedEvent(Map<Object, List<List<String>>> nodeedgeDefUses, Map<String, List<List<Object>>> variableDefUses) {
		this.nodeedgeDefUses = nodeedgeDefUses;
		this.variableDefUses = variableDefUses;
	}

}
