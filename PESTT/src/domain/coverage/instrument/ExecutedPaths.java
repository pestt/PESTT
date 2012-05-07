package domain.coverage.instrument;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;

import domain.constants.Layer;

import ui.constants.BytemanLog;
import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;

public class ExecutedPaths {

	String location;
	String mthd;
	
	public ExecutedPaths(String location, String mthd) {
		this.location = location;
		this.mthd = mthd;
	}
	
	public List<Path<Integer>> getExecutedPaths() {
		List<Path<Integer>> paths = new ArrayList<Path<Integer>>();
		try {
			Scanner scanner = new Scanner(new File(location));
			while(scanner.hasNext()) 
				paths.add(createPath(scanner));
		} catch (Exception e) {
			e.printStackTrace();

		}
		return paths;
	}

	private Path<Integer> createPath(Scanner scanner) {
		boolean entry = false;
		boolean exit = false;
		List<Node<Integer>> nodes = new ArrayList<Node<Integer>>();
		while(!entry || !exit) {
			String line = scanner.nextLine();
			if(line.compareTo(BytemanLog.ENTERING_METHOD + mthd) == 0)
				entry = true;
			else if(line.compareTo(BytemanLog.EXITING_METHOD + mthd) == 0)
				exit = true;
			else {
				int l = Integer.parseInt(line);
				nodes.add(getNodeForLine(l));
			}
		}
		String path = verifyPath(nodes);
		return Activator.getDefault().getTestPathController().createTestPath(path);
	}

	@SuppressWarnings("unchecked")
	private Node<Integer> getNodeForLine(int line) {
		Graph<Integer> sourceGraph =  Activator.getDefault().getSourceGraphController().getSourceGraph();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) 
				if(line == map.values().iterator().next().getStartLine()) 
						return node;	
		}
		return null;
	}
	
	private String verifyPath(List<Node<Integer>> nodes) {
		String path = "";
	/*	for(Node<Integer> node : nodes) {
			if
		} */
		return path;
	}
}
