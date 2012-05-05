package domain.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.activator.Activator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;

import ui.editor.Line;
import adt.graph.Graph;
import adt.graph.Node;
import domain.constants.Layer;
import domain.coverage.instrument.Rules;
import domain.coverage.instrument.RulesFileCreator;

public class BytemanController {
	
	public String getExecutedPath() {
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		String dir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + "script";
		String file = "rules.btm";
		String className = Activator.getDefault().getEditorController().getClassName();
		String methodName = Activator.getDefault().getEditorController().getSelectedMethod();
		RulesFileCreator fileCreator = new RulesFileCreator(dir, file);
		Rules rules = new Rules();
		fileCreator.writeRule(rules.createRuleForMethodEntry(className, methodName));
		fileCreator.writeRule(rules.createRuleForMethodExit(className, methodName));
		List<Integer> lines = getNodeLines();
		for(Integer line : lines)
			fileCreator.writeRule(rules.createRuleForLine(className, methodName, line));
				fileCreator.close();
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getNodeLines() {
		List<Integer> lines = new ArrayList<Integer>();
		Graph<Integer> sourceGraph =  Activator.getDefault().getSourceGraphController().getSourceGraph();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) {
				for(Line line : map.values()) 
					if(!lines.contains(line.getStartLine())) {
						lines.add(line.getStartLine());
						break;
					}
			}
		}
		return lines;
	}
}
