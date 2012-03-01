package sourcecodeexplorer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sourcegraph.Graph;

public class ProjectExplorer implements IProjectExplorer {
	
	public Graph<Integer> getSourceCodeGraph(ICompilationUnit unit, String methodName) {
		// Now create the AST for the ICompilationUnits
		CompilationUnit parse = parse(unit);
		StatementsVisitor visitor = new StatementsVisitor(methodName, parse);
		parse.accept(visitor);
		return visitor.getGraph();
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