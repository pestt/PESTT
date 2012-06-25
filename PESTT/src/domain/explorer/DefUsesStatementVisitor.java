package domain.explorer;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

public class DefUsesStatementVisitor extends ASTVisitor {
	
	private static final String EMPTY = null; 
	private static final String THIS = "this.";
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
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(CharacterLiteral node) {
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(NumberLiteral node) {
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(BooleanLiteral node) {
		stored.push(EMPTY);
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		if(node.getName().resolveBinding().getKind() == IBinding.VARIABLE) {
			IJavaElement javaElement = node.getName().resolveBinding().getJavaElement();
			switch(javaElement.getElementType()) {
				case IJavaElement.FIELD:
					if(node.getQualifier().resolveBinding().getKind() != IBinding.TYPE) 
						uses.add(node.getQualifier().toString());
					stored.push(node.getQualifier().toString() + "." + node.getName().toString());
					break;
				default:
					stored.push(node.getName().toString());
					break;
			}
		}
		return false;
	}
	
	@Override
	public void endVisit(SimpleName node) {
		if(node.resolveBinding().getKind() == IBinding.VARIABLE) {
			IJavaElement javaElement = node.resolveBinding().getJavaElement();
			if(javaElement.getElementType() == IJavaElement.FIELD) 
				stored.push(THIS + node.toString());
			else
				stored.push(node.toString());
		}
	}	
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		if(stored.size() > 1)
			addToUses();
		addToDefs();
	}
	
	@Override
	public void endVisit(Assignment node) {
		if(stored.size() > 1)
			addToUses();
		if(!node.getOperator().toString().equals("="))
			uses.add(stored.peek());
		addToDefs();		
	}

	@Override
	public void endVisit(InfixExpression node) {
		if(node.hasExtendedOperands()) {
			int size = node.extendedOperands().size();
			while(size != 0) {
				addToUses();
				size--;
			}
		}
		addToUses();
		addToUses();
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(PostfixExpression node) {
		String top = stored.peek();
		addToUses();
		stored.push(top);
		addToDefs();
	}

	@Override
	public void endVisit(PrefixExpression node) {
		String top = stored.peek();
		addToUses();
		stored.push(top);
		addToDefs();
	}
	
	@Override
	public void endVisit(ExpressionStatement node) {
		while(!stored.isEmpty())
			addToUses();
	}	
	
	@Override
	public void endVisit(ArrayCreation node) {
		addToUses();
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(ArrayInitializer node) {
		int size = node.expressions().size();
		while(size != 0) {
			addToUses();
			size--;
		}
		stored.push(EMPTY);
	}
	
	@Override
	public void endVisit(ArrayAccess node) {
		addToUses();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
		List<Expression> args = node.arguments();
		if(node.getExpression() != null)
			node.getExpression().accept(this);
		int size = stored.size();
		for(Expression exp : args)
			exp.accept(this);
		while(stored.size() != size)
			addToUses();
		return false;
	}
	
	@Override
	public boolean visit(IfStatement node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DoStatement node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		node.getExpression().accept(this);
		addToUses();
		node.getParameter().accept(this);
		addToDefs();
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		node.getExpression().accept(this);
		addToUses();
		return false;
	}
	
	@Override
	public boolean visit(SwitchCase node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public void endVisit(ReturnStatement node) {
		while(!stored.isEmpty())
			addToUses();
	}
	
	private void addToDefs() {
		if(!stored.isEmpty())
			if(stored.peek() != EMPTY)
				defs.add(stored.pop());
			else
				stored.pop();
	}

	private void addToUses() {
		if(!stored.isEmpty())
			if(stored.peek() != EMPTY && !defs.contains(stored.peek()))
				uses.add(stored.pop());
			else
				stored.pop();	
	}
}