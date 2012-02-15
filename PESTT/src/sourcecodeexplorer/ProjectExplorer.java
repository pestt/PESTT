package sourcecodeexplorer;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import constants.CompilationUnits_ID;

import sourcegraph.Graph;

public class ProjectExplorer implements IProjectExplorer {

	private Graph<Integer> graph;
	

	public Graph<Integer> getSourceCodeGraph(ArrayList<String> path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for(IProject project : projects) {
			if(project.getName().equals(path.get(CompilationUnits_ID.PROJECT))) {
				try {
					if(project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
						IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
						for(IPackageFragment mypackage : packages) 
							if(mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) 
								if(mypackage.getElementName().equals(path.get(CompilationUnits_ID.PACKAGE))) 
									for(ICompilationUnit unit : mypackage.getCompilationUnits())
										if(unit.getElementName().equals(path.get(CompilationUnits_ID.CLASS))) {
											// Now create the AST for the ICompilationUnits
											CompilationUnit parse = parse(unit);
											StatementsVisitor visitor = new StatementsVisitor(path.get(CompilationUnits_ID.METHOD), parse);
											parse.accept(visitor);
											graph = visitor.getGraph();
										}								
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return graph;
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 */

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}