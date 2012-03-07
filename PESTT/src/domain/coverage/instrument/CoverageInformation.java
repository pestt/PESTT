package domain.coverage.instrument;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.activator.Activator;
import ui.editor.ActiveEditor;
import adt.graph.Graph;
import domain.graph.visitors.ExecutedGraphVisitor;

public class CoverageInformation {
	
	private Graph<Integer> sourceGraph;
	private CodeCoverage codeCoverage;
	private List<List<ICoverageData>> data;
	private List<Object> executedPaths;
	
	public CoverageInformation(ActiveEditor editor) {
		this.sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		codeCoverage = new CodeCoverage(editor);
		data = codeCoverage.getCodeCoverageStatus();
		executedPaths = new LinkedList<Object>();
	}
	
	public List<Object> getExecutedPaths() {
		executedPaths.clear();
		List<Integer> toRemove = new ArrayList<Integer>();
		for(int i = 0; i < data.size(); i++) {
			ExecutedGraphVisitor<Integer> executedGraphVisitor = new ExecutedGraphVisitor<Integer>(data.get(i).get(0));
			sourceGraph.accept(executedGraphVisitor);
			Graph<Integer> executedGraph = executedGraphVisitor.getExecutedGraph();
			if(executedGraph.getNodes().size() > 0)
				executedPaths.add(executedGraph);
			else
				toRemove.add(i);
		}
		for(int i : toRemove)
			data.remove(i);
		return executedPaths;
	}
	
}
