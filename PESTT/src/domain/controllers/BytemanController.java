package domain.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Layer;
import domain.coverage.instrument.ExecutedPaths;
import domain.coverage.instrument.FileCreator;
import domain.coverage.instrument.HelperClass;
import domain.coverage.instrument.Rules;

public class BytemanController {
	
	Graph<Integer> sourceGraph;
	FileCreator helper;
	FileCreator rules;
	FileCreator output;
	
	public void createScripts() {
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
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.FOLDER, null);
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.FOLDER, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
		List<EdgeLine> lines = getNodeFirstLineNumber();
		for(EdgeLine el : lines)
			rulesFile.writeFileContent(rules.createRuleForLine(helper, mthd, cls, el.edges, el.line));
		rulesFile.close();
		return rulesFile;
	}
	
	@SuppressWarnings("unchecked")
	private List<EdgeLine> getNodeFirstLineNumber() {
		List<EdgeLine> lines = new ArrayList<EdgeLine>();
		sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); 
		for(Node<Integer> node : sourceGraph.getNodes()) {
			HashMap<ASTNode, Line> map = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node);
			if(map != null) {
				int nodeLine = map.values().iterator().next().getStartLine(); 
				Set<Edge<Integer>> fromNode = sourceGraph.getNodeEdges(node);
				if(isCondition(node)) 
					for(Edge<Integer> edge : fromNode) {
						HashMap<ASTNode, Line> instr = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(edge.getEndNode());
						if(instr != null) {
							int ruleLine = instr.values().iterator().next().getStartLine();
							addToLines(lines, edge, ruleLine);
						}
					}	
				else {
					for(Edge<Integer> edge : fromNode)
						if(isCondition(edge.getEndNode()))
							addToLines(lines, edge, nodeLine);
						else {
							HashMap<ASTNode, Line> instr = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(edge.getEndNode());
							if(instr != null) {
								int ruleLine = instr.values().iterator().next().getStartLine();
								addToLines(lines, edge, ruleLine);
							}
						}								
				}
			}
		}
		return lines;
	}
	
	private void addToLines(List<EdgeLine> lines, Edge<Integer> edge, int line) {
		boolean hasLine = false;
		for(EdgeLine eg : lines)
			if(eg.line == line) {
				eg.edit(eg.edges.concat(" " + edge.toString()));
				hasLine = true;
				break;
			}
		if(!hasLine) {
			lines.add(new EdgeLine(edge.toString(), line));
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isCondition(Node<Integer> node) {
		HashMap<ASTNode, Line> nodeInstructions = (HashMap<ASTNode, Line>) sourceGraph.getMetadata(node);
		if(nodeInstructions != null) {
			List<ASTNode> astNodes = getASTNodes(nodeInstructions);
			switch(astNodes.get(0).getNodeType()) {
				case ASTNode.IF_STATEMENT:
				case ASTNode.DO_STATEMENT:
				case ASTNode.FOR_STATEMENT:
				case ASTNode.ENHANCED_FOR_STATEMENT:
				case ASTNode.SWITCH_STATEMENT:
				case ASTNode.WHILE_STATEMENT:
					return true;
			}
		}
		return false;
	}
		
	private List<ASTNode> getASTNodes(HashMap<ASTNode, Line> map) {
		List<ASTNode> nodes = new LinkedList<ASTNode>();
		for(Entry<ASTNode, Line> entry : map.entrySet()) 
	         nodes.add(entry.getKey());
		return nodes;
	}
	
	public void deleteScripts() {
		helper.deleteFile();
		output.deleteFile();
		rules.deleteFile();
		rules.deleteDirectory();
		updateFiles();
	}
	
	public List<Path<Integer>> getExecutedPaths() {
		updateFiles();
		ExecutedPaths reader = new ExecutedPaths(output.getAbsolutePath(), Activator.getDefault().getEditorController().getSelectedMethod());
		List<Path<Integer>> paths = reader.getExecutedPaths();
		output.cleanFileContent();
		return paths;
	}
	
	private void updateFiles() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.FOLDER, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private class EdgeLine {
		private String edges;
		private int line;
		
		public EdgeLine(String edges, int line) {
			this.edges = edges;
			this.line = line;
		}
		
		public void edit(String edges) {
			this.edges= edges;
		}
	}
}
