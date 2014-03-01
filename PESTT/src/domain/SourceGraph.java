package domain;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ui.constants.JavadocTagAnnotations;
import adt.graph.Graph;
import adt.graph.IGraphVisitor;
import adt.graph.Node;
import domain.ast.visitors.GraphBuilder;
import domain.constants.Layer;
import domain.events.CFGCreateEvent;
import domain.events.CFGUpdateEvent;

public class SourceGraph extends Observable {

	private Graph sourceGraph;
	private Map<JavadocTagAnnotations, List<String>> javadocAnnotations;
	private byte[] hash;
	private List<SingleVariableDeclaration> params;
	private List<VariableDeclarationFragment> attributes;
	private List<EnumDeclaration> enumFields;

	public SourceGraph() {
		sourceGraph = new Graph();
	}

	public void create(ICompilationUnit unit, String methodName) {
		// Now create the AST for the ICompilationUnits
		CompilationUnit parser = parse(unit);
		GraphBuilder visitor = new GraphBuilder(methodName, parser);
		parser.accept(visitor);
		sourceGraph = visitor.getGraph();
		javadocAnnotations = visitor.getJavadocAnnotations();
		hash = visitor.getMethodHash();
		params = visitor.getMethodParameters();
		attributes = visitor.getClassAttributes();
		enumFields = visitor.getEnumClassAttributes();
		setChanged();
		notifyObservers(new CFGCreateEvent(sourceGraph));
	}

	public Graph getSourceGraph() {
		return sourceGraph;
	}

	public int numberOfNodes() {
		return sourceGraph.size();
	}

	public List<SingleVariableDeclaration> getMethodParameters() {
		return params;
	}

	public List<VariableDeclarationFragment> getClassAttributes() {
		return attributes;
	}

	public List<EnumDeclaration> getEnumClassAttributes() {
		return enumFields;
	}

	public CompilationUnit getCompilationUnit(ICompilationUnit unit) {
		return parse(unit);
	}

	public Map<JavadocTagAnnotations, List<String>> getJavadocAnnotations() {
		return javadocAnnotations;
	}

	public byte[] getMethodHash() {
		return hash;
	}

	public void applyVisitor(IGraphVisitor visitor) {
		sourceGraph.accept(visitor);
	}

	@SuppressWarnings("deprecation")
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

	public void updateMetadataInformation(Graph graph) {
		graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer());
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer());
		Iterator<Node> sourceGraphIt = sourceGraph.getNodes()
				.iterator();
		Iterator<Node> graphIt = graph.getNodes().iterator();
		while (sourceGraphIt.hasNext() && graphIt.hasNext()) {
			Node gNode = graphIt.next();
			Node sgNode = sourceGraphIt.next();
			sourceGraph.addMetadata(sgNode, null);
			sourceGraph.addMetadata(sgNode, graph.getMetadata(gNode));
		}
		setChanged();
		notifyObservers(new CFGUpdateEvent(sourceGraph));
	}
}