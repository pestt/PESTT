package domain;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import adt.graph.Graph;
import domain.constants.JavadocTagAnnotations;
import domain.events.CFGCreateEvent;
import domain.explorer.StatementsVisitor;
import domain.graph.visitors.IGraphVisitor;

public class SourceGraph extends Observable {
	
	private Graph<Integer> sourceGraph;
	private Map<JavadocTagAnnotations, List<String>> javadocAnnotations;
	
	public SourceGraph() {
		sourceGraph = new Graph<Integer>();
	}
	
	public void create(ICompilationUnit unit, String methodName) {
		// Now create the AST for the ICompilationUnits
		CompilationUnit parser = parse(unit);
		StatementsVisitor statementVisitor = new StatementsVisitor(methodName, parser);
		parser.accept(statementVisitor);
		sourceGraph = statementVisitor.getGraph();
		javadocAnnotations = statementVisitor.getJavadocTagAnnotations();
		setChanged();
		notifyObservers(new CFGCreateEvent(sourceGraph));
	}
	
	public Graph<Integer> getSourceGraph() {
		return sourceGraph;
	}
	
	public int numberOfNodes() {
		return sourceGraph.getNodes().size(); 
	}
	
	public Map<JavadocTagAnnotations, List<String>> getJavadocTagAnnotations() {
		return javadocAnnotations;
	}
	
	public CompilationUnit getCompilationUnit(ICompilationUnit unit) {
		return parse(unit);
	}
	
	public void applyVisitor(IGraphVisitor<Integer> visitor) {
		sourceGraph.accept(visitor);
	}
	
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); 
	}
}