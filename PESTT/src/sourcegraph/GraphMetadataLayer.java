package sourcegraph;

import java.util.HashMap;


public class GraphMetadataLayer {
	private HashMap<Node<?>, Object> nodeMetadata;
	private HashMap<Edge<?>, Object> edgeMetadata;
	
	public GraphMetadataLayer() {
		nodeMetadata = new HashMap<Node<?>, Object> ();
		edgeMetadata = new HashMap<Edge<?>, Object> ();
	}
	
	public void addMetadata(Node<?> node, Object data) {
		nodeMetadata.put(node, data);
	}
	
	public void addEdgeMetadata(Edge<?> edge, Object data) {
		edgeMetadata.put(edge, data);
	}
	
	public Object getMetadata(Node<?> node) {
		return nodeMetadata.get(node);
	}
	
	public Object getMetadata(Edge<?> edge) {
		return edgeMetadata.get(edge);
	}
}