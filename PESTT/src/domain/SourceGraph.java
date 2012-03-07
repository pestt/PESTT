package domain;

import java.util.Observable;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import adt.graph.Graph;
import domain.explorer.StatementsVisitor;
import domain.graph.visitors.IGraphVisitor;

public class SourceGraph extends Observable {
	
	private Graph<Integer> sourceGraph;
	
	public SourceGraph() {
		sourceGraph = new Graph<Integer>();
	}
	
	public void create(ICompilationUnit unit, String methodName) {
		// Now create the AST for the ICompilationUnits
		CompilationUnit parse = parse(unit);
		StatementsVisitor visitor = new StatementsVisitor(methodName, parse);
		parse.accept(visitor);
		sourceGraph = visitor.getGraph();
		setChanged();
		notifyObservers(new ControlFlowGraphGeneratedEvent(sourceGraph));
	}
	
	public Graph<Integer> getSourceGraph() {
		return sourceGraph;
	}
	
	public boolean isGraphDisplayed() {
		return sourceGraph.getNodes().size() > 0 ? true : false; 
	}
	
	public void applyVisitor(IGraphVisitor<Integer> visitor) {
		sourceGraph.accept(visitor);
	}
	
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}