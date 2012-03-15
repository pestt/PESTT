package domain.explorer;


import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class DefUsesVisitor extends ASTVisitor {
	
	private String methodName;
	private CompilationUnit unit;

	public DefUsesVisitor(String methodName, CompilationUnit unit) {
		this.methodName = methodName; // name of the method to be analyzed.
		this.unit = unit;
	}

	
	public boolean visit(Assignment node) {
		System.out.println("Assignment " + node.getLeftHandSide());
		return false;
	}
	
	public boolean visit(PostfixExpression node) {
		System.out.println("PostfixExpression " + node.getOperand());
		return false;
	}
	
	public boolean visit(PrefixExpression node) {
		System.out.println("PrefixExpression " + node.getOperand());
		return false;
	}
	
	
	
	@Override  
	public boolean visit(ExpressionStatement node) {
		System.out.println("ExpressionStatement " + node.getExpression().toString());
		return true;
	}

	@Override  
	public boolean visit(AssertStatement node) {
		System.out.println("AssertStatement " + node.getExpression().toString());
		return true;
	}

	@Override  
	public boolean visit(VariableDeclarationStatement node) {
		System.out.println("VariableDeclarationStatement " + node.toString() );
		return true;
	}
	
	@Override  
	public boolean visit(VariableDeclarationExpression node) {
		System.out.println("VariableDeclarationExpression " + node.fragments() );
		List<VariableDeclarationFragment> vars = node.fragments();
		for(VariableDeclarationFragment var : vars)
			System.out.println("Name " + var.getName());
		return true;
	}
	
}