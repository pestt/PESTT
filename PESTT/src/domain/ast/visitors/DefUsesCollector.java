package domain.ast.visitors;

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

public class DefUsesCollector extends ASTVisitor {
	
	private static final String EMPTY = null; 
	private static final String THIS = "this.";
	private Set<String> defs;
	private Set<String> uses;
	private Stack<String> stack;
	
	public DefUsesCollector(Set<String> defs, Set<String> uses) {
		this.defs = defs;
		this.uses = uses;
		stack = new Stack<String>();
	}
	
	@Override
	public void endVisit(StringLiteral node) {
		stack.push(EMPTY);
	}
	
	@Override
	public void endVisit(CharacterLiteral node) {
		stack.push(EMPTY);
	}
	
	@Override
	public void endVisit(NumberLiteral node) {
		stack.push(EMPTY);
	}
	
	@Override
	public void endVisit(BooleanLiteral node) {
		stack.push(EMPTY);
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		if(node.getName().resolveBinding().getKind() == IBinding.VARIABLE) {
			IJavaElement javaElement = node.getName().resolveBinding().getJavaElement();
			if(javaElement != null) {
				switch(javaElement.getElementType()) {
					case IJavaElement.FIELD:
						if(node.getQualifier().resolveBinding().getKind() != IBinding.TYPE)
							if(node.getQualifier().resolveBinding().getJavaElement().getElementType() == IJavaElement.FIELD)
								uses.add(THIS + node.toString());
							else {
								uses.add(node.getQualifier().toString());
								stack.push(node.getQualifier().toString() + "." + node.getName().toString());
							}
						break;
					default:
						stack.push(node.getName().toString());
						break;
				}
			} else {
				uses.add(node.getQualifier().toString());
				stack.push(node.getQualifier().toString() + "." + node.getName().toString());
			}
		}
		return false;
	}
	
	@Override
	public void endVisit(SimpleName node) {
		if(node.resolveBinding().getKind() == IBinding.VARIABLE) {
			IJavaElement javaElement = node.resolveBinding().getJavaElement();
			if(javaElement.getElementType() == IJavaElement.FIELD) 
				stack.push(THIS + node.toString());
			else
				stack.push(node.toString());
		}
	}	
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		if(stack.size() > 1)
			addToUses();
		addToDefs();
	}
	
	@Override
	public void endVisit(Assignment node) {
		addToUses();
		if(!node.getOperator().toString().equals("="))
			uses.add(stack.peek());
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
		stack.push(EMPTY);
	}
	
	@Override
	public void endVisit(PostfixExpression node) {
		String top = stack.peek();
		addToUses();
		defs.add(top);
	}

	@Override
	public void endVisit(PrefixExpression node) {
		String top = stack.peek();
		addToUses();
		defs.add(top);
	}
	
	@Override
	public void endVisit(ExpressionStatement node) {
		while(!stack.isEmpty())
			addToUses();
	}	
	
	@Override
	public void endVisit(ArrayCreation node) {
		addToUses();
		stack.push(EMPTY);
	}
	
	@Override
	public void endVisit(ArrayInitializer node) {
		int size = node.expressions().size();
		while(size != 0) {
			addToUses();
			size--;
		}
		stack.push(EMPTY);
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
		int size = stack.size();
		for(Expression exp : args)
			exp.accept(this);
		while(stack.size() != size)
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
		Expression exp = node.getExpression();
		if(exp != null)
			node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public void endVisit(ReturnStatement node) {
		while(!stack.isEmpty())
			addToUses();
	}
	
	private void addToDefs() {
		if(!stack.isEmpty())
			if(stack.peek() != EMPTY)
				defs.add(stack.pop());
			else
				stack.pop();
	}

	private void addToUses() {
		if(!stack.isEmpty())
			if(stack.peek() != EMPTY && !defs.contains(stack.peek()))
				uses.add(stack.pop());
			else
				stack.pop();	
	}
}