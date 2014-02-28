package ui.source;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ui.constants.Description;

public class GraphElements {

	private double gWidth;
	private double gHeight;
	private Map<String, Node> nodes;
	private Map<String, Edge> connections;

	public GraphElements(Map<String, List<String>> elements) {
		gWidth = 0.0;
		gHeight = 0.0;
		nodes = null;
		connections = null;
		getElements(elements);
	}

	public double getGraphWidth() {
		return gWidth;
	}

	public double getGraphHeight() {
		return gHeight;
	}

	public Map<String, Node> getNodesInfo() {
		return nodes;
	}

	public Map<String, Edge> getEdgesInfo() {
		return connections;
	}

	private void getElements(Map<String, List<String>> elements) {
		nodes = new LinkedHashMap<String, Node>();
		connections = new LinkedHashMap<String, Edge>();
		Node node = null;
		Edge connection = null;
		Set<Entry<String, List<String>>> set = elements.entrySet();
		Iterator<Entry<String, List<String>>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> entry = iterator.next();
			if (entry.getKey().equals(Description.GRAPH)) {
				List<String> dimension = entry.getValue();
				gWidth = Double.parseDouble(dimension.get(0));
				gHeight = Double.parseDouble(dimension.get(1));
			} else if (Description.NODE.equals(entry.getKey().substring(0, 4))) {
				node = new Node(entry.getValue());
				nodes.put(entry.getKey(), node);
			} else if (Description.EDGE.equals(entry.getKey().substring(0, 4))) {
				connection = new Edge(entry.getValue());
				connections.put(entry.getKey(), connection);
			}
		}
	}

}