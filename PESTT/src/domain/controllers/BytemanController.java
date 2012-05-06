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
import domain.coverage.instrument.HelperContent;
import domain.coverage.instrument.HelperCreator;
import domain.coverage.instrument.Rules;
import domain.coverage.instrument.RulesFileCreator;

public class BytemanController {
	
	public String getExecutedPath() {
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		String scriptDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + "script";
		String sourceDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + unit.getParent().getParent().getElementName();
		String scriptFile = "rules.btm";
		String outputFile = "output.txt";
		String helperClass = "PESTTHelper.java";
		String methodName = Activator.getDefault().getEditorController().getSelectedMethod();
		String packageName = Activator.getDefault().getEditorController().getPackageName();
		String className = Activator.getDefault().getEditorController().getClassName();
		HelperCreator helper = setHelperClass(sourceDir, helperClass, packageName);
		createRulesFile(scriptDir, scriptFile, className, methodName, helper);
		createOutputFile(scriptDir, outputFile);
		return "";
	}

	private HelperCreator setHelperClass(String dir, String helperClass, String packageName) {
		HelperCreator helper = new HelperCreator(dir, packageName, helperClass);
		HelperContent helperContent = new HelperContent();
		helper.writeHelper(helperContent.getContent(packageName));
		helper.close();
		return helper;
	}

	private void createRulesFile(String dir, String file, String className, String methodName, HelperCreator helper) {
		RulesFileCreator rulesFile = new RulesFileCreator(dir, file);
		Rules rules = new Rules();
		rulesFile.writeRule(rules.createRuleForMethodEntry(className, methodName, helper.getName()));
		rulesFile.writeRule(rules.createRuleForMethodExit(className, methodName, helper.getName()));
		List<Integer> lines = getNodeLines();
		for(Integer line : lines)
			rulesFile.writeRule(rules.createRuleForLine(className, methodName, line, helper.getName()));
		rulesFile.close();
	}
	
	private void createOutputFile(String dir, String file) {
		new RulesFileCreator(dir, file);
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
