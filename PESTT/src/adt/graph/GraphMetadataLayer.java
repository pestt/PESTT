package adt.graph;

import java.util.HashMap;
import java.util.Map;

public class GraphMetadataLayer {
	
	/**
	 * The Node information associated to the Graph meta-data. 
	 */
	private Map<Node<?>, Object> nodeMetadata;
	
	/**
	 * The Edge information associated to the Graph meta-data. 
	 */
	private Map<Edge<?>, Object> edgeMetadata;
	
	/**
	 * Create a new GraphMetadataLayer Object.
	 */
	public GraphMetadataLayer() {
		nodeMetadata = new HashMap<Node<?>, Object> ();
		edgeMetadata = new HashMap<Edge<?>, Object> ();
	}
	
	/**
	 * Adds new information to the given Node, to the current Graph meta-data.
	 *  
	 * @param node - The Node to add the information.
	 * @param data - The Information to associated to the Node.
	 */
	public void addMetadata(Node<?> node, Object data) {
		nodeMetadata.put(node, data);
	}
	
	/**
	 * Adds new information to the given EDge, to the current Graph meta-data.
	 *  
	 * @param edge - The Edge to add the information.
	 * @param data - The Information to associated to the Edge.
	 */
	public void addEdgeMetadata(Edge<?> edge, Object data) {
		edgeMetadata.put(edge, data);
	}
	
	/***
	 * Get the information to the given Node.
	 * 
	 * @param node - The Node to get the information.
	 * @return Object - The information associated to the give Node.
	 */
	public Object getMetadata(Node<?> node) {
		return nodeMetadata.get(node);
	}
	
	/***
	 * Get the information to the given Edge.
	 * 
	 * @param edge - The Edge to get the information.
	 * @return Object - The information associated to the give Edge.
	 */
	public Object getMetadata(Edge<?> edge) {
		return edgeMetadata.get(edge);
	}
}