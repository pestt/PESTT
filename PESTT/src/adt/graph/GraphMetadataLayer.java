package adt.graph;

import java.util.HashMap;
import java.util.Map;

public class GraphMetadataLayer {

	/**
	 * The Node information associated to the Graph meta-data.
	 */
	private Map<Node, Object> nodeMetadata;

	/**
	 * The Edge information associated to the Graph meta-data.
	 */
	private Map<Edge, Object> edgeMetadata;

	/**
	 * Creates a new GraphMetadataLayer Object.
	 */
	public GraphMetadataLayer() {
		nodeMetadata = new HashMap<Node, Object>();
		edgeMetadata = new HashMap<Edge, Object>();
	}

	/**
	 * Adds new information to the given Node in the current Graph's meta-data.
	 * 
	 * @param node
	 *            - The Node to add the information to.
	 * @param data
	 *            - The Information to associate to the Node.
	 */
	public void addMetadata(Node node, Object data) {
		nodeMetadata.put(node, data);
	}

	/**
	 * Adds new information to the given Edge in the current Graph's meta-data.
	 * 
	 * @param edge
	 *            - The Edge to add the information to.
	 * @param data
	 *            - The Information to associate to the Edge.
	 */
	public void addEdgeMetadata(Edge edge, Object data) {
		edgeMetadata.put(edge, data);
	}

	/***
	 * Gets the information of the given Node.
	 * 
	 * @param node
	 *            - The Node to get the information from.
	 * @return Object - The information associated to the given Node.
	 */
	public Object getMetadata(Node node) {
		return nodeMetadata.get(node);
	}

	/***
	 * Gets the information of the given Edge.
	 * 
	 * @param edge
	 *            - The Edge to get the information from.
	 * @return Object - The information associated to the given Edge.
	 */
	public Object getMetadata(Edge edge) {
		return edgeMetadata.get(edge);
	}
}