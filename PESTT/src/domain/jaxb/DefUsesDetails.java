package domain.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import main.activator.Activator;

@XmlRootElement(name = "defusesList")
public class DefUsesDetails {

	@XmlElement(name = "defuses")
	List<DefUsesEntry> defuses;

	public DefUsesDetails() {
		defuses = new ArrayList<DefUsesEntry>();
	}

	public void setMethodDefUses() {
		Map<Object, List<List<String>>> temp = Activator.getDefault()
				.getDefUsesController().getDefUsesByNodeEdge();
		for (Object key : temp.keySet())
			defuses.add(new DefUsesEntry(key.toString(), temp.get(key)));
	}

	@XmlRootElement(name = "defuses")
	public static class DefUsesEntry {

		@XmlElement(name = "node_edge")
		public String key;
		@XmlElementWrapper(name = "definitionsList")
		@XmlElement(name = "definitions")
		public List<String> defs;
		@XmlElementWrapper(name = "usesList")
		@XmlElement(name = "uses")
		public List<String> uses;

		public DefUsesEntry() {
		}

		public DefUsesEntry(String key, List<List<String>> values) {
			this.key = key;
			this.defs = values.get(0);
			this.uses = values.get(1);
		}

		public String getKey() {
			return this.key;
		}

		public List<String> getDefinitions() {
			return defs;
		}

		public List<String> getUses() {
			return uses;
		}
	}
}
