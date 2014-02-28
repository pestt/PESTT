package domain.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import main.activator.Activator;
import adt.graph.Edge;
import adt.graph.Node;

@XmlRootElement(name = "graph")
public class GraphDetails {

	@XmlElementWrapper(name = "initialNodesList")
	@XmlElement(name = "initialNodes")
	private List<String> initialNodes;
	@XmlElementWrapper(name = "finalNodesList")
	@XmlElement(name = "finalNode")
	private List<String> finalNodes;
	@XmlElementWrapper(name = "nodesList")
	@XmlElement(name = "node")
	private List<String> nodes;
	@XmlElementWrapper(name = "esgesList")
	@XmlElement(name = "edge")
	private List<String> edges;

	public GraphDetails() {
		initialNodes = new ArrayList<String>();
		finalNodes = new ArrayList<String>();
		nodes = new ArrayList<String>();
		edges = new ArrayList<String>();
	}

	public void setGraphDetails() {
		setGraphInitialNodes();
		setGraphFinalNodes();
		setGraphNodes();
		setGraphEdges();
	}

	private void setGraphInitialNodes() {
		for (Node<Integer> node : Activator.getDefault()
				.getSourceGraphController().getSourceGraph().getInitialNodes())
			initialNodes.add(node.toString());
	}

	public List<String> getGraphInitialNodes() {
		return initialNodes;
	}

	private void setGraphFinalNodes() {
		for (Node<Integer> node : Activator.getDefault()
				.getSourceGraphController().getSourceGraph().getFinalNodes())
			finalNodes.add(node.toString());
	}

	public List<String> getGraphFinalNodes() {
		return finalNodes;
	}

	private void setGraphNodes() {
		for (Node<Integer> node : Activator.getDefault()
				.getSourceGraphController().getSourceGraph().getNodes())
			nodes.add(node.toString());
	}

	public List<String> getGraphNodes() {
		return nodes;
	}

	private void setGraphEdges() {
		for (Node<Integer> node : Activator.getDefault()
				.getSourceGraphController().getSourceGraph().getNodes())
			for (Edge<Integer> edge : Activator.getDefault()
					.getSourceGraphController().getSourceGraph()
					.getNodeEdges(node))
				edges.add(edge.toString());
	}

	public List<String> getGraphEdges() {
		return edges;
	}

}
