package domain.graph.visitors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import domain.constants.Colors;
import domain.constants.Layer;
import domain.coverage.instrument.ICoverageData;


public class LoopBreakGraphVisitor<V extends Comparable<V>> extends ASTVisitor {

	private ICoverageData data;
	private Graph<V> graph;
	private boolean breaks;
	
	public LoopBreakGraphVisitor(ICoverageData data, Graph<V> graph) {
		this.data = data;
		this.graph = graph;
		breaks = false;
	}
	
	@Override  
	public boolean visit(WhileStatement node) {
		return false;
	}
	
	@Override  
	public boolean visit(DoStatement node) {
		return false;
	}
	
	@Override  
	public boolean visit(ForStatement node) {
		return false;
	}
	
	@Override  
	public boolean visit(EnhancedForStatement node) {
		return false;
	}
	
	@Override  
	public boolean visit(SwitchStatement node) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override  
	public boolean visit(BreakStatement node) {
		Node<V> breakNode = graph.getInitialNodes().iterator().next();	
		graph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		Map<ASTNode, Line> map = (LinkedHashMap<ASTNode, Line>) graph.getMetadata(breakNode); // get the information in this layer to this node.
		int line = map.entrySet().iterator().next().getValue().getStartLine();
		if(data.getLineStatus(line).equals(Colors.GRENN_ID)) 
			breaks = true;
		return true;
	}
		
	public boolean hasBreaks() {
		return breaks;
	}
}