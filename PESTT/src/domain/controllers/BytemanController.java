package domain.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import main.activator.Activator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import ui.editor.Line;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import adt.graph.Path;
import domain.constants.Byteman;
import domain.constants.Layer;
import domain.coverage.instrument.ExecutedPaths;
import domain.coverage.instrument.FileCreator;
import domain.coverage.instrument.JUnitTestRunListener;
import domain.coverage.instrument.Rules;
import domain.events.EndTestsExecutionEvent;
import domain.events.TestStartEvent;

public class BytemanController implements Observer {
	
	private Graph<Integer> sourceGraph;
	private FileCreator output;
	private JUnitTestRunListener listener;
	private ArrayList<String> tests;
	
	@Override
	public void update(Observable obs, Object data) {
		if(data instanceof EndTestsExecutionEvent) {
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					getExecutedtestPaths();
					deleteScripts();
				} 
			});
		} else if(data instanceof TestStartEvent) {
			tests.add(((TestStartEvent) data).test);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void addListener() {
		tests = new ArrayList<String>();
		listener = new JUnitTestRunListener();
		listener.addObserver(this);
		JUnitCore.addTestRunListener(listener);
	}
	
	public void removeListener() {
		listener.deleteObserver(this);
	}
	
	public void createScripts() {
		sourceGraph = Activator.getDefault().getSourceGraphController().getSourceGraph();
		String mthd = Activator.getDefault().getEditorController().getSelectedMethod();
		String cls = Activator.getDefault().getEditorController().getClassName();		
		output = createOutputFile(Byteman.SCRIPT_DIR, Byteman.OUTPUT_FILE);
		createRulesFile(Byteman.SCRIPT_DIR, Byteman.SCRIPT_FILE, Byteman.HELPER_LOCATIO, mthd, cls);
	}

	private FileCreator createOutputFile(String dir, String name) {
		FileCreator output = new FileCreator();
		output.createDirectory(dir);
		output.createFile(name);
		return output;
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
	
	
	private void deleteScripts() {
		removeListener();
	/*	output.deleteFile();
		rules.deleteFile();
		rules.deleteDirectory();
	*/
	}
	
	
	private void getExecutedtestPaths() {
		List<Path<Integer>> paths = getExecutedPaths();
		for(int i = 0; i < paths.size(); i++) {
			Path<Integer> newTestPath = paths.get(i);
			String tooltip = tests.get(i);
			if(newTestPath != null) 
				Activator.getDefault().getTestPathController().addAutomaticTestPath(newTestPath, tooltip);
		}
	}
	
	public List<Path<Integer>> getExecutedPaths() { 
		ExecutedPaths reader = new ExecutedPaths(output.getFile(), Activator.getDefault().getEditorController().getSelectedMethod());
		List<Path<Integer>> paths = reader.getExecutedPaths();
		return paths;
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