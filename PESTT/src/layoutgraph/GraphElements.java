package layoutgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import constants.Description_ID;

public class GraphElements {
	
	private double gWidth;
	private double gHeight;
	private LinkedHashMap<String, Node> nodes;
	private LinkedHashMap<String, Edge> connections;
	
	public GraphElements(Map<String, ArrayList<String>> elements) {
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
	
	public HashMap<String, Node> getNodesInfo() {
		return nodes;
	}
	
	public HashMap<String, Edge> getEdgesInfo() {
		return connections;
	}
	
	private void getElements(Map<String, ArrayList<String>> elements) {
		nodes = new LinkedHashMap<String, Node>();
		connections =  new LinkedHashMap<String, Edge>();
		Node node = null;
		Edge connection = null;
		Set<Entry<String, ArrayList<String>>> set = elements.entrySet();
		Iterator<Entry<String, ArrayList<String>>> iterator = set.iterator();
		while(iterator.hasNext()) {
			Entry<String, ArrayList<String>> entry = iterator.next();
			if(entry.getKey().equals(Description_ID.GRAPH)) {
				ArrayList<String> dimension = entry.getValue();
				gWidth = Double.parseDouble(dimension.get(0));
				gHeight = Double.parseDouble(dimension.get(1));
			} else if(Description_ID.NODE.equals(entry.getKey().substring(0, 4))) {
				node = new Node(entry.getValue());
				nodes.put(entry.getKey(), node);
			} else if(Description_ID.EDGE.equals(entry.getKey().substring(0, 4))) {
				connection = new Edge(entry.getValue());
				connections.put(entry.getKey(), connection);
			}
		}
	}
	
}