package domain.coverage.instrument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import main.activator.Activator;
import ui.constants.BytemanLog;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;

public class ExecutedPaths {

	Graph<Integer> sourceGraph;
	String location;
	String mthd;
	List<Node<Integer>> pathNodes;
	List<Edge<Integer>> loop;
	
	public ExecutedPaths(String location, String mthd) {
		this.location = location;
		this.mthd = mthd;
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		pathNodes = new ArrayList<Node<Integer>>();
		loop = new ArrayList<Edge<Integer>>();
	}
	
	public List<Path<Integer>> getExecutedPaths() {
		List<Path<Integer>> paths = new ArrayList<Path<Integer>>();
		try {
			Scanner scanner = new Scanner(new File(location));
			while(scanner.hasNext()) {
				pathNodes.clear();
				loop.clear();
				paths.add(createPath(scanner));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paths;
	}

	private Path<Integer> createPath(Scanner scanner) {
		boolean entry = false;
		boolean exit = false;
		while(!entry || !exit) {
			String line = scanner.nextLine();
			if(line.compareTo(BytemanLog.ENTERING_METHOD + mthd) == 0)
				entry = true;
			else if(line.compareTo(BytemanLog.EXITING_METHOD + mthd) == 0)
				exit = true;
			else {
				List<Edge<Integer>> edges = getEdges(line);
				if(!edges.isEmpty())
					addNodeToPath(edges);
			}
		}
		String path = getPathString();
		return Activator.getDefault().getTestPathController().createTestPath(path);
	}

	private String getPathString() {
		if(!sourceGraph.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
			getFinalNode(pathNodes.get( pathNodes.size() - 1));
		String path = "";
		for(Node<Integer> node : pathNodes)
			path += node.getValue() + ", ";
		path = path.substring(0, path.length() - 2);
		return path;
	}

	private void getFinalNode(Node<Integer> node) {
		for(Edge<Integer> edge : sourceGraph.getNodeEdges(node))
			if(sourceGraph.isFinalNode(edge.getEndNode())) {
				pathNodes.add(edge.getEndNode());
				break;
			}
	}

	private void addNodeToPath(List<Edge<Integer>> edges) {
		if(pathNodes.isEmpty()) {
			pathNodes.add(edges.get(0).getBeginNode());
			pathNodes.add(edges.get(0).getEndNode());
		} else {
			if(edges.size() == 1) {
				if(pathNodes.get(pathNodes.size() - 1) == edges.get(0).getBeginNode())
					pathNodes.add(edges.get(0).getEndNode());
				else {
					boolean breaking = false;
					while(pathNodes.get(pathNodes.size() - 1) != edges.get(0).getBeginNode()) {
						Edge<Integer> e = getEdgeForBegin(pathNodes.get(pathNodes.size() - 1).getValue(), loop);
						if(e != null) 
							pathNodes.add(e.getEndNode());
						else {
							breaking = true;
							break;
						}
					}
					if(!breaking)
						pathNodes.add(edges.get(0).getEndNode());
				}
			} else { 
				Edge<Integer> e = getEdgeForBegin(pathNodes.get(pathNodes.size() - 1).getValue(), edges);
				if(e != null) {
					pathNodes.add(e.getEndNode());
					Edge<Integer> e1 = getEdgeForBegin(pathNodes.get(pathNodes.size() - 1).getValue(), edges);
					if(e1 != null) {
						pathNodes.add(e1.getEndNode());
						if(edges.size() > 2) {
							edges.remove(e);
							edges.remove(e1);
							addToLoop(edges);
						}
					}
				} else {
					addToLoop(edges);
				}
			}
		}
	}
	
	private void addToLoop(List<Edge<Integer>> edges) {
		for(Edge<Integer> edge : edges)
			if(!loop.contains(edge))
				loop.add(edge);
	}

	private List<Edge<Integer>> getEdges(String line) {
		List<Edge<Integer>> edges = new ArrayList<Edge<Integer>>();
		int begin = 0;
		int end = 0;
		StringTokenizer tokenizer = new StringTokenizer(line, "()");
		while(tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			if(str.length() > 1) {
				StringTokenizer tok = new StringTokenizer(str, ", ");
				begin = Integer.parseInt(tok.nextToken());
				end = Integer.parseInt(tok.nextToken());
				Edge<Integer> edge = getEdgeForNodes(begin, end);
				if(edge != null)
					edges.add(edge);
			}
		}
		return edges;
	}

	private Edge<Integer> getEdgeForNodes(int begin, int end) {
		for(Node<Integer> node : sourceGraph.getNodes()) 
			for(Edge<Integer> edge : sourceGraph.getNodeEdges(node))
				if(edge.getBeginNode().getValue() == begin && edge.getEndNode().getValue() == end)
					return edge;
		return null;
	}
	
	private Edge<Integer> getEdgeForBegin(int begin, List<Edge<Integer>> edges) {
		for(Edge<Integer> edge : edges)
			if(edge.getBeginNode().getValue() == begin)
				return edge;
		return null;
	}
}