package domain.explorer;

import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

public class DefUsesStatementVisitor extends ASTVisitor {
	
	private static final String EMPTY = null; 
	private Set<String> defs;
	private Set<String> uses;
	private Stack<String> stored;
	
	public DefUsesStatementVisitor(Set<String> defs, Set<String> uses) {
		this.defs = defs;
		this.uses = uses;
		stored = new Stack<String>();
	}
	
	@Override
	public void endVisit(StringLiteral node) {
		System.out.println("StringLiteral " + node);
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(CharacterLiteral node) {
		System.out.println("CharacterLiteral " + node);
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(NumberLiteral node) {
		System.out.println("NumberLiteral " + node);
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(BooleanLiteral node) {
		System.out.println("BooleanLiteral " + node);
		stored.push(EMPTY);
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		System.out.println("QualifiedName " + node);
		return false;
	}
	
	@Override
	public void endVisit(SimpleName node) {
		System.out.println("SimpleName " + node);
		if(node.resolveBinding().getKind() == IBinding.VARIABLE) 
			stored.push(node.toString());
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		System.out.println("VariableDeclarationFragment " + node);
		if(stored.size() > 1)
			addUses();
		addDefs();
	}
	
	@Override
	public void endVisit(Assignment node) {
		System.out.println("Assignment " + node);
		if(stored.size() > 1)
			addUses();
		addDefs();
	}

	@Override
	public void endVisit(InfixExpression node) {
		System.out.println("InfixExpression " + node);
		if(node.hasExtendedOperands()) {
			int size = node.extendedOperands().size();
			while(size != 0) {
				addUses();
				size--;
			}
		}
		addUses();
		addUses();
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(PostfixExpression node) {
		System.out.println("PostfixExpression " + node);
		String top = stored.peek();
		addUses();
		stored.push(top);
		addDefs();
	}

	@Override
	public void endVisit(PrefixExpression node) {
		System.out.println("PrefixExpression " + node);
		String top = stored.peek();
		addUses();
		stored.push(top);
		addDefs();
	}
	
	@Override
	public void endVisit(ExpressionStatement node) {
		System.out.println("ExpressionStatement " + node);
		while(!stored.isEmpty())
			addUses();
	}	
	
	@Override
	public void endVisit(ArrayCreation node) {
		System.out.println("ArrayCreation " + node);
		addUses();
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(ArrayInitializer node) {
		System.out.println("ArrayInitializer " + node);
		int size = node.expressions().size();
		while(size != 0) {
			addUses();
			size--;
		}
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(ArrayAccess node) {
		System.out.println("ArrayAccess " + node);
		addUses();
	}
	
	@Override
	public boolean visit(IfStatement node) {
		System.out.println("IFStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		System.out.println("WhileStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DoStatement node) {
		System.out.println("DoStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		System.out.println("ForStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		System.out.println("EnhancedForStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		System.out.println("SwitchStatement " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(SwitchCase node) {
		System.out.println("SwitchCase " + node);
		node.getExpression().accept(this);
		return false;
	}
	
	private void addDefs() {
		if(!stored.isEmpty())
			if(stored.peek() != EMPTY)
				defs.add(stored.pop());
			else
				stored.pop();
	}

	private void addUses() {
		if(!stored.isEmpty())
			if(stored.peek() != EMPTY)
				uses.add(stored.pop());
			else
				stored.pop();	
	}
}