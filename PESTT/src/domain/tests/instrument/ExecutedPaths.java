package domain.tests.instrument;

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

	private Graph<Integer> sourceGraph;
	private File output;
	private String mthd;
	private List<Node<Integer>> pathNodes;
	private Stack<List<Edge<Integer>>> loop;

	public ExecutedPaths(File output, String mthd) {
		this.output = output;
		this.mthd = mthd;
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		pathNodes = new ArrayList<Node<Integer>>();
		loop = new Stack<List<Edge<Integer>>>();
	}

	public List<Path<Integer>> getExecutedPaths() {
		List<Path<Integer>> paths = new ArrayList<Path<Integer>>();
		try {
			Scanner scanner = new Scanner(output);
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
			if(line.compareTo(Byteman.ENTERING_METHOD + mthd) == 0)
				entry = true;
			else if(line.compareTo(Byteman.EXITING_METHOD + mthd) == 0)
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
			getFinalNode();
		String path = "";
		for(Node<Integer> node : pathNodes)
			path += node.getValue() + ", ";
		path = path.substring(0, path.length() - 2);
		return path;
	}

	private void getFinalNode() {
		if(!loop.isEmpty()) {
			Node<Integer> last = pathNodes.get(pathNodes.size() - 1);
			while(!loop.isEmpty()) {
				Edge<Integer> e = getEdgeForBegin(pathNodes.get(pathNodes.size() - 1), loop.peek());
				if(e != null) 
					if(e.getEndNode() != last)
						pathNodes.add(e.getEndNode());
					else
						loop.pop();
				else
					loop.pop();
			}
		}
		for(Edge<Integer> edge : sourceGraph.getNodeEdges(pathNodes.get(pathNodes.size() - 1)))
			if(sourceGraph.isFinalNode(edge.getEndNode())) {
				pathNodes.add(edge.getEndNode());
				break;
			}
	}

	private void addNodeToPath(List<Edge<Integer>> edges) {
		List<Edge<Integer>> toRemove = new ArrayList<Edge<Integer>>();
		if(pathNodes.isEmpty()) 
			addFirstNodesToPath(edges, toRemove);
		else {
			if(edges.size() == 1) {
				if(pathNodes.get(pathNodes.size() - 1) == edges.get(0).getBeginNode())
					pathNodes.add(edges.get(0).getEndNode());
				else 
					addNodeToPathEndForOneEdge(edges);
			} else { 
				if(pathNodes.get(pathNodes.size() - 1) != edges.get(0).getBeginNode()) 
					addNodeToPathEndForManyEdge(edges);
				toRemove.add(getEdgeForBegin(pathNodes.get(pathNodes.size() - 1), edges));
				if(toRemove.get(toRemove.size() - 1) != null) {
					pathNodes.add(toRemove.get(toRemove.size() - 1).getEndNode());
					toRemove.add(getEdgeForBegin(pathNodes.get(pathNodes.size() - 1), edges));
					if(toRemove.get(toRemove.size() - 1) != null) {
						pathNodes.add(toRemove.get(toRemove.size() - 1).getEndNode());
						if(edges.size() > 2) {
							for(Edge<Integer> e : edges) 
								if(!toRemove.contains(e) && e.getEndNode() == toRemove.get(toRemove.size() - 1).getBeginNode()) {
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

	private void addFirstNodesToPath(List<Edge<Integer>> edges,
			List<Edge<Integer>> toRemove) {
		for(Edge<Integer> edge : edges)
			if(sourceGraph.isInitialNode(edge.getBeginNode())) {
				pathNodes.add(edges.get(0).getBeginNode());
				pathNodes.add(edges.get(0).getEndNode());
				toRemove.add(edge);
			}
		edges.removeAll(toRemove);
		if(!edges.isEmpty())
			loop.push(edges);
	}
	
	private void addNodeToPathEndForOneEdge(List<Edge<Integer>> edges) {
		boolean breaking = false;
		while(pathNodes.get(pathNodes.size() - 1) != edges.get(0).getBeginNode() && !loop.isEmpty()) {
			Edge<Integer> e = getEdgeForBegin(pathNodes.get(pathNodes.size() - 1), loop.peek());
			if(e != null) 
				pathNodes.add(e.getEndNode());
			else {
				breaking = true;
				break;
			}
		}
		if(!breaking && !sourceGraph.isFinalNode(pathNodes.get(pathNodes.size() - 1)))
			pathNodes.add(edges.get(0).getEndNode());
	}
	
	private void addNodeToPathEndForManyEdge(List<Edge<Integer>> edges) {
		int i = 0;
		List<Node<Integer>> tmp = new ArrayList<Node<Integer>>();
		tmp.add(pathNodes.get(pathNodes.size() - 1));
		while(tmp.get(tmp.size() - 1) != edges.get(0).getBeginNode() && !loop.isEmpty()) {
			Edge<Integer> e = getEdgeForBegin(tmp.get(tmp.size() - 1), loop.peek());
			if(i == 3) {
				loop.pop();
				i = 0;
			} else if(e != null)
				tmp.add(e.getEndNode());
			if(!loop.isEmpty() && edges.containsAll(loop.peek()))
				loop.pop();
			i++;
		}
		tmp.remove(0);
		if(!tmp.isEmpty() && pathNodes.get(pathNodes.size() - 1) == edges.get(0).getBeginNode()) 
			pathNodes.addAll(tmp);
		else if(!tmp.isEmpty() && getEdgeForBegin(tmp.get(tmp.size() - 1), edges) != null)
			pathNodes.addAll(tmp);
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

	private Edge<Integer> getEdgeForBegin(Node<Integer> begin, List<Edge<Integer>> edges) {
		for(Edge<Integer> edge : edges)
			if(edge.getBeginNode() == begin)
				return edge;
		return null;
	}
}