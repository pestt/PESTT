package domain.explorer;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class DefUsesStatementVisitor extends ASTVisitor {
	
	private static final String EMPTY = "[]"; 
	private Set<String> defs;
	private Set<String> uses;
	private Stack<String> stored;
	
	public DefUsesStatementVisitor(Set<String> defs, Set<String> uses) {
		this.defs = defs;
		this.uses = uses;
		stored = new Stack<String>();
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		System.out.println("VariableDeclarationStatement " + node);
		List<VariableDeclarationFragment> vars = (List<VariableDeclarationFragment>) node.fragments();
		for(VariableDeclarationFragment var : vars) 
			var.accept(this);
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationExpression node) {
		System.out.println("VariableDeclarationExpression " + node);
		List<VariableDeclarationFragment> vars = (List<VariableDeclarationFragment>) node.fragments();
		for(VariableDeclarationFragment var : vars) 
			var.accept(this);
		return false;
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		System.out.println("VariableDeclarationFragment " + node);
		stored.push(node.getName().toString());
		if(node.getInitializer() != null) {
			stored.push(node.getInitializer().toString());
			node.getInitializer().accept(this);
		}
		else
			stored.push(EMPTY);
		addUses();
		addDefs();
		return false;
	}
	
	public boolean visit(Assignment node) {
		System.out.println("Assignment " + node);
		stored.push(node.getLeftHandSide().toString());
		stored.push(node.getRightHandSide().toString());
		node.getRightHandSide().accept(this);
		addUses();
		node.getLeftHandSide().accept(this);
		addDefs();
		return false;
	}
	
	public boolean visit(InfixExpression node) {
		System.out.println("InfixExpression " + node);
		if(!stored.isEmpty()) 
			stored.pop();
		stored.push(node.getLeftOperand().toString());
		stored.push(node.getRightOperand().toString());
		node.getRightOperand().accept(this);
		addUses();
		node.getLeftOperand().accept(this);
		addUses();
		stored.push(EMPTY);
		return false;
	}
	
	public boolean visit(PostfixExpression node) {
		System.out.println("PostfixExpression " + node);
		stored.push(node.getOperand().toString());
		node.getOperand().accept(this);
		return false;
	}

	public boolean visit(PrefixExpression node) {
		System.out.println("PrefixExpression " + node);
		stored.push(node.getOperand().toString());
		node.getOperand().accept(this);
		return false;
	}
	
	public boolean visit(StringLiteral node) {
		System.out.println("StringLiteral " + node);
		if(!stored.isEmpty()) {
			stored.pop();
			stored.push(EMPTY);
		}
		return false;
	}
	
	public boolean visit(CharacterLiteral node) {
		System.out.println("CharacterLiteral " + node);
		if(!stored.isEmpty()) {
			stored.pop();
			stored.push(EMPTY);
		}
		return false;
	}
	
	public boolean visit(NumberLiteral node) {
		System.out.println("NumberLiteral " + node);
		if(!stored.isEmpty()) {
			stored.pop();
			stored.push(EMPTY);
		}
		return false;
	}
	
	public boolean visit(BooleanLiteral node) {
		System.out.println("BooleanLiteral " + node);
		if(!stored.isEmpty()) {
			stored.pop();
			stored.push(EMPTY);
		}
		return false;
	}
	
	public boolean visit(ExpressionStatement node) {
		System.out.println("ExpressionStatement " + node);
		System.out.println("Expression " + node.getExpression());
		return false;
	}
	
	public boolean visit(IfStatement node) {
		System.out.println("IFStatement " + node);
		stored.push(node.getExpression().toString());
		node.getExpression().accept(this);
		addUses();
		addDefs();
		return false;
	}
	
	public boolean visit(WhileStatement node) {
		System.out.println("WhileStatement " + node);
		return false;
	}
	
	public boolean visit(DoStatement node) {
		System.out.println("DoStatement " + node);
		return false;
	}
	
	public boolean visit(ForStatement node) {
		System.out.println("ForStatement " + node);
		return false;
	}
	
	public boolean visit(EnhancedForStatement node) {
		System.out.println("EnhancedForStatement " + node);
		return false;
	}
	
	public boolean visit(SwitchStatement node) {
		System.out.println("SwitchStatement " + node);
		return false;
	}
	
	public boolean visit(SwitchCase node) {
		System.out.println("SwitchCase " + node);
		return false;
	}
	
	private void addDefs() {
		if(!stored.isEmpty())
			if(!stored.peek().equals(EMPTY))
				defs.add(stored.pop());
			else
				stored.pop();
	}

	private void addUses() {
		if(!stored.isEmpty())
			if(!stored.peek().equals(EMPTY))
				uses.add(stored.pop());
			else
				stored.pop();			
	}

	/*
	public boolean visit(QualifiedName node) {
		System.out.println("QualifiedName " + node);
		return false;
	} */
	
	
}