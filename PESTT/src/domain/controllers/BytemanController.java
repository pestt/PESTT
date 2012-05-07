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
import domain.coverage.instrument.ExecutedPaths;
import domain.coverage.instrument.HelperClass;
import domain.coverage.instrument.Rules;

public class BytemanController {
	
	Graph<Integer> sourceGraph;
	FileCreator helper;
	FileCreator rules;
	FileCreator output;
	
	public String createScripts() {
		sourceGraph =  Activator.getDefault().getSourceGraphController().getSourceGraph();
		String scriptFile = "rules.btm";
		String outputFile = "output.txt";
		String helperClass = "PESTTHelper.java";
		ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
		String sourceDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + unit.getParent().getParent().getElementName();
		String scriptDir = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getPath().toOSString() + IPath.SEPARATOR + "script";
		String pckg = Activator.getDefault().getEditorController().getPackageName();
		String mthd = Activator.getDefault().getEditorController().getSelectedMethod();
		String cls = Activator.getDefault().getEditorController().getClassName();
		output = createOutputFile(scriptDir, outputFile);
		helper = createHelperClass(sourceDir, pckg, helperClass, output.getAbsolutePath());
		rules = createRulesFile(scriptDir, scriptFile, helper.getLocation(), mthd, cls);
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
		helper.writeFileContent(helperClass.create(pckg, output));
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
	
	public void deleteScripts() {
		helper.deleteFile();
		output.deleteFile();
		rules.deleteFile();
		rules.deleteDirectory();
	}
	
	public List<Path<Integer>> getExecutedPaths() {
		ExecutedPaths reader = new ExecutedPaths(output.getAbsolutePath(), Activator.getDefault().getEditorController().getSelectedMethod());
		List<Path<Integer>> paths = reader.getExecutedPaths();
		output.cleanFileContent();
		return paths;
	}
	
	 
}
