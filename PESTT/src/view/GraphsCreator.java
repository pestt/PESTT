package view;

import graphvisitors.DotGraphVisitor;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import sourcecodeexplorer.IProjectExplorer;
import sourcecodeexplorer.ProjectExplorer;
import sourcegraph.Graph;
import constants.Graph_ID;
import coveragealgorithms.GraphCoverageCriteria;
import dotprocessor.DotProcess;
import dotprocessor.IDotProcess;

public enum GraphsCreator {
	
	INSTANCE;
	
	private ArrayList<Object> graphs;
	
	public void createGraphs(Composite parent, ArrayList<String> location) {
		IProjectExplorer explorer = new ProjectExplorer(); // creates a project to be explorer.
		DotGraphVisitor<Integer> visitor = new DotGraphVisitor<Integer>(); // creates the visitor to the graph.
		Graph<Integer> sourceGraph = explorer.getSourceCodeGraph(location); // get the graph of the project explored.
		graphs.add(Graph_ID.SOURCE_GRAPH_NUM, sourceGraph); // add the sourceGraph.
		visitor.visitGraph(sourceGraph); // apply the visitor to the graph.
		String dotGraph = "digraph grafo {\nrankdir=TD\nsize=\"10,10\"\n" + visitor.getDotString()  + "}\n"; // creates the string to be passed to Graphviz.
		IDotProcess dotProcess = new DotProcess(); // the object that parse the information to build the layoutGraph.
		Map<String, ArrayList<String>> map = dotProcess.DotToPlain(dotGraph); // the information to build the layoutGraph.				
		if(map != null) {
			layoutgraph.Graph layoutGraph = new layoutgraph.Graph(parent, map);
			graphs.add(Graph_ID.LAYOUT_GRAPH_NUM, layoutGraph); // add the layoutGraph.
			parent.layout();
		}
	}
	
	public void createCoverageCriteriaGraph(Composite parent) {
		graphs = new ArrayList<Object>(); // the list to store the sourceGraph and the layoutGraph;
		GraphCoverageCriteria coverageGraph = new GraphCoverageCriteria(parent);;
		graphs.add(Graph_ID.COVERAGE_GRAPH_NUM, coverageGraph); // add the coverageGraph.
		parent.layout();
	}
	
	public ArrayList<Object> getGraphs() {
		return graphs;
	}
		
	public boolean isDisplayed() {
		if(graphs != null)
			return true;
		return false;
	}
}