package domain.controllers;

import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ui.constants.JavadocTagAnnotations;
import adt.graph.Graph;
import adt.graph.IGraphVisitor;
import domain.SourceGraph;

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

	public void applyVisitor(IGraphVisitor visitor) {
		sourceGraph.applyVisitor(visitor);
	}

	public List<SingleVariableDeclaration> getMethodParameters() {
		return sourceGraph.getMethodParameters();
	}

	public List<VariableDeclarationFragment> getClassAttributes() {
		return sourceGraph.getClassAttributes();
	}

	public List<EnumDeclaration> getEnumClassAttributes() {
		return sourceGraph.getEnumClassAttributes();
	}

	public Graph getSourceGraph() {
		return sourceGraph.getSourceGraph();
	}

	public int numberOfNodes() {
		return sourceGraph.numberOfNodes();
	}

	public CompilationUnit getCompilationUnit(ICompilationUnit unit) {
		return sourceGraph.getCompilationUnit(unit);
	}

	public void updateMetadataInformation(Graph graph) {
		sourceGraph.updateMetadataInformation(graph);
	}

	public Map<JavadocTagAnnotations, List<String>> getJavadocAnnotations() {
		return sourceGraph.getJavadocAnnotations();
	}

	public byte[] getMethodHash() {
		return sourceGraph.getMethodHash();
	}
}
