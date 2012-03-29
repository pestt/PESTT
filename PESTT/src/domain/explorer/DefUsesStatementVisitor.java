package domain.explorer;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class DefUsesStatementVisitor extends ASTVisitor {
	
	private Set<String> defs;
	private Set<String> uses;
	
	public DefUsesStatementVisitor(Set<String> defs, Set<String> uses) {
		this.defs = defs;
		this.uses = uses;
	}

	public boolean visit(Assignment node) {
		defs.add(node.getLeftHandSide().toString());
		return false;
	}
	
	public boolean visit(PostfixExpression node) {
		defs.add(node.getOperand().toString());
		uses.add(node.getOperand().toString());
		return false;
	}
	
	public boolean visit(PrefixExpression node) {
		defs.add(node.getOperand().toString());
		uses.add(node.getOperand().toString());
		return false;
	}
		
	@Override  
	public boolean visit(AssertStatement node) {
		defs.add(node.getExpression().toString());
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override  
	public boolean visit(VariableDeclarationStatement node) {
		List<VariableDeclarationFragment> vars = (List<VariableDeclarationFragment>) node.fragments();
		for(VariableDeclarationFragment var : vars)
			defs.add(var.getName().toString());
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override  
	public boolean visit(VariableDeclarationExpression node) {
		List<VariableDeclarationFragment> vars = (List<VariableDeclarationFragment>) node.fragments();
		for(VariableDeclarationFragment var : vars)
			defs.add(var.getName().toString());
		return true;
	} 
}