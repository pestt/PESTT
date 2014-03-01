package domain.tests.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

import main.activator.Activator;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Byteman;

public class ExecutedPaths {

	private Graph sourceGraph;
	private File output;
	private String mthd;
	private List<Node> pathNodes;
	private Stack<List<Edge>> loop;

	public ExecutedPaths(File output, String mthd) {
		this.output = output;
		this.mthd = mthd;
		sourceGraph = Activator.getDefault().getSourceGraphController()
				.getSourceGraph();
		pathNodes = new ArrayList<Node>();
		loop = new Stack<List<Edge>>();
	}

	public List<Path> getExecutedPaths() {
		List<Path> paths = new ArrayList<Path>();
		try {
			Scanner scanner = new Scanner(output);
			while (scanner.hasNext()) {
				pathNodes.clear();
				loop.clear();
				paths.add(createPath(scanner));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paths;
	}

	private Path createPath(Scanner scanner) {
		boolean entry = false;
		boolean exit = false;
		while (!entry || !exit) {
			String line = scanner.nextLine();
			if (line.compareTo(Byteman.ENTERING_METHOD + mthd) == 0)
				entry = true;
			else if (line.compareTo(Byteman.EXITING_METHOD + mthd) == 0)
				exit = true;
			else {
				List<Edge> edges = getEdges(line);
				if (!edges.isEmpty())
					addNodeToPath(edges);
			}
		}
		String path = getPathString();
		return Activator.getDefault().getTestPathController()
				.createTestPath(path);
	}

	private String getPathString() {
		if (!sourceGraph.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
			getFinalNode();
		String path = "";
		for (Node node : pathNodes)
			path += node.getValue() + ", ";
		path = path.substring(0, path.length() - 2);
		return path;
	}

	private void getFinalNode() {
		if (!loop.isEmpty()) {
			Node last = pathNodes.get(pathNodes.size() - 1);
			while (!loop.isEmpty()) {
				Edge e = getEdgeForBegin(
						pathNodes.get(pathNodes.size() - 1), loop.peek());
				if (e != null)
					if (e.getEndNode() != last)
						pathNodes.add(e.getEndNode());
					else
						loop.pop();
				else
					loop.pop();
			}
		}
		for (Edge edge : sourceGraph.getNodeEdges(pathNodes
				.get(pathNodes.size() - 1)))
			if (sourceGraph.isFinalNode(edge.getEndNode())) {
				pathNodes.add(edge.getEndNode());
				break;
			}
	}

	private void addNodeToPath(List<Edge> edges) {
		List<Edge> toRemove = new ArrayList<Edge>();
		if (pathNodes.isEmpty())
			addFirstNodesToPath(edges, toRemove);
		else {
			if (edges.size() == 1) {
				if (pathNodes.get(pathNodes.size() - 1) == edges.get(0)
						.getBeginNode())
					pathNodes.add(edges.get(0).getEndNode());
				else
					addNodeToPathEndForOneEdge(edges);
			} else {
				if (pathNodes.get(pathNodes.size() - 1) != edges.get(0)
						.getBeginNode())
					addNodeToPathEndForManyEdge(edges);
				toRemove.add(getEdgeForBegin(
						pathNodes.get(pathNodes.size() - 1), edges));
				if (toRemove.get(toRemove.size() - 1) != null) {
					pathNodes.add(toRemove.get(toRemove.size() - 1)
							.getEndNode());
					toRemove.add(getEdgeForBegin(
							pathNodes.get(pathNodes.size() - 1), edges));
					if (toRemove.get(toRemove.size() - 1) != null) {
						pathNodes.add(toRemove.get(toRemove.size() - 1)
								.getEndNode());
						if (edges.size() > 2) {
							for (Edge e : edges)
								if (!toRemove.contains(e)
										&& e.getEndNode() == toRemove.get(
												toRemove.size() - 1)
												.getBeginNode()) {
									toRemove.remove(toRemove.size() - 1);
									break;
								}
							edges.removeAll(toRemove);
							loop.push(edges);
						}
					}
				} else
					loop.push(edges);
			}
		}
	}

	private void addFirstNodesToPath(List<Edge> edges,
			List<Edge> toRemove) {
		for (Edge edge : edges)
			if (sourceGraph.isInitialNode(edge.getBeginNode())) {
				pathNodes.add(edges.get(0).getBeginNode());
				pathNodes.add(edges.get(0).getEndNode());
				toRemove.add(edge);
			}
		edges.removeAll(toRemove);
		if (!edges.isEmpty())
			loop.push(edges);
	}

	private void addNodeToPathEndForOneEdge(List<Edge> edges) {
		boolean breaking = false;
		while (pathNodes.get(pathNodes.size() - 1) != edges.get(0)
				.getBeginNode() && !loop.isEmpty()) {
			Edge e = getEdgeForBegin(
					pathNodes.get(pathNodes.size() - 1), loop.peek());
			if (e != null)
				pathNodes.add(e.getEndNode());
			else {
				breaking = true;
				break;
			}
		}
		if (!breaking
				&& !sourceGraph
						.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
			pathNodes.add(edges.get(0).getEndNode());
	}

	private void addNodeToPathEndForManyEdge(List<Edge> edges) {
		int i = 0;
		List<Node> tmp = new ArrayList<Node>();
		tmp.add(pathNodes.get(pathNodes.size() - 1));
		while (tmp.get(tmp.size() - 1) != edges.get(0).getBeginNode()
				&& !loop.isEmpty()) {
			Edge e = getEdgeForBegin(tmp.get(tmp.size() - 1),
					loop.peek());
			if (i == 3) {
				loop.pop();
				i = 0;
			} else if (e != null)
				tmp.add(e.getEndNode());
			if (!loop.isEmpty() && edges.containsAll(loop.peek()))
				loop.pop();
			i++;
		}
		tmp.remove(0);
		if (!tmp.isEmpty()
				&& pathNodes.get(pathNodes.size() - 1) == edges.get(0)
						.getBeginNode())
			pathNodes.addAll(tmp);
		else if (!tmp.isEmpty()
				&& getEdgeForBegin(tmp.get(tmp.size() - 1), edges) != null)
			pathNodes.addAll(tmp);
	}

	private List<Edge> getEdges(String line) {
		List<Edge> edges = new ArrayList<Edge>();
		int begin = 0;
		int end = 0;
		StringTokenizer tokenizer = new StringTokenizer(line, "()");
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			if (str.length() > 1) {
				StringTokenizer tok = new StringTokenizer(str, ", ");
				begin = Integer.parseInt(tok.nextToken());
				end = Integer.parseInt(tok.nextToken());
				Edge edge = getEdgeForNodes(begin, end);
				if (edge != null)
					edges.add(edge);
			}
		}
		return edges;
	}

	private Edge getEdgeForNodes(int begin, int end) {
		for (Node node : sourceGraph.getNodes())
			for (Edge edge : sourceGraph.getNodeEdges(node))
				if (edge.getBeginNode().getValue() == begin
						&& edge.getEndNode().getValue() == end)
					return edge;
		return null;
	}

	private Edge getEdgeForBegin(Node begin,
			List<Edge> edges) {
		for (Edge edge : edges)
			if (edge.getBeginNode() == begin)
				return edge;
		return null;
	}
}