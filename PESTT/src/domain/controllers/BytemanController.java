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
import adt.graph.Path;
import domain.constants.Layer;
import domain.coverage.instrument.FileCreator;
import domain.coverage.instrument.HelperClass;
import domain.coverage.instrument.Rules;

public class BytemanController {
	
	Graph<Integer> sourceGraph =  Activator.getDefault().getSourceGraphController().getSourceGraph();
	FileCreator helper;
	FileCreator rules;
	FileCreator output;
	
	public String createScripts() {
		String scriptFile = "rules.btm";
		String outputFile = "output.txt";
		String helperClass = "PESTTHelper.java";
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		String sourceDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + unit.getParent().getParent().getElementName();
		String scriptDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + "script";
		String packageName = Activator.getDefault().getEditorController().getPackageName();
		String methodName = Activator.getDefault().getEditorController().getSelectedMethod();
		String className = Activator.getDefault().getEditorController().getClassName();
		output = createOutputFile(scriptDir, outputFile);
		helper = createHelperClass(sourceDir, packageName, helperClass, output.getAbsolutePath());
		rules = createRulesFile(scriptDir, scriptFile, helper.getLocation(), methodName, className);
		return "";
	}

	private FileCreator createOutputFile(String dir, String name) {
		FileCreator output = new FileCreator();
		output.createDirectory(dir);
		output.createFile(name);
		return output;
	}
	
	private FileCreator createHelperClass(String dir, String pckg, String cls, String output) {
		FileCreator helper = new FileCreator();
		HelperClass helperClass = new HelperClass();
		dir += IPath.SEPARATOR + pckg.replace('.', IPath.SEPARATOR);
		helper.createDirectory(dir);
		helper.createFile(cls);
		helper.writeFileContent(helperClass.getContent(pckg, output));
		helper.close();
		return helper;
	}

	private FileCreator createRulesFile(String dir, String name, String helper, String mthd, String cls) {
		FileCreator rulesFile = new FileCreator();
		Rules rules = new Rules();
		rulesFile.createDirectory(dir);
		rulesFile.createFile(name);
		rulesFile.writeFileContent(rules.createRuleForMethodEntry(helper, mthd, cls));
		rulesFile.writeFileContent(rules.createRuleForMethodExit(helper, mthd, cls));
		List<Integer> lines = getNodeFirstLineNumber();
		for(Integer line : lines)
			rulesFile.writeFileContent(rules.createRuleForLine(helper, mthd, cls, line));
		rulesFile.close();
		return rulesFile;
	}
	
	public void deleteScripts() {
		helper.deleteFile();
		output.deleteFile();
		rules.deleteFile();
		rules.deleteDirectory();
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getNodeFirstLineNumber() {
		List<Integer> lines = new ArrayList<Integer>();
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
	
	@SuppressWarnings("unchecked")
	private Node<Integer> getNodeForLine(int line) {
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.
		for(Node<Integer> node : sourceGraph.getNodes()) {
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node); // get the information in this layer to this node.
			if(map != null) 
				for(Line l : map.values()) 
					if(line == l.getStartLine()) 
						return node;	
		}
		return null;
	}
	
	public List<Path<Integer>> getExecutedPaths() {
		List<Path<Integer>> paths = new ArrayList<Path<Integer>>();
		return paths;
	}
	
	 
}
