package domain;

import java.util.Observer;

import org.eclipse.jdt.core.ICompilationUnit;

import adt.graph.Graph;
import domain.graph.visitors.IGraphVisitor;

public class SourceGraphController {

	private SourceGraph sourceGraph;

	public SourceGraphController(SourceGraph sourceGraph) {
		this.sourceGraph = sourceGraph;
	}
	
	public void addObserverSourceGraph(Observer o) {
		sourceGraph.addObserver(o);
	}
	
	public void deleteObserverSourceGraph(Observer o) {
		sourceGraph.deleteObserver(o);
	}
	
	public void create(ICompilationUnit unit, String methodName) {
		sourceGraph.create(unit, methodName);
	}
	
	public void applyVisitor(IGraphVisitor<Integer> visitor) {
		sourceGraph.applyVisitor(visitor);
	}

	public Graph<Integer> getSourceGraph() {
		return sourceGraph.getSourceGraph();
	}
	
	public boolean isGraphDisplayed() {
		return sourceGraph.isGraphDisplayed();
	}


}
