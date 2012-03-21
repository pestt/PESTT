package domain.explorer;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ui.constants.Description;
import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import domain.GraphInformation;
import domain.constants.JavadocTagAnnotations;
import domain.constants.Layer;
import domain.graph.visitors.RenumNodesGraphVisitor;

public class StatementsVisitor extends ASTVisitor {

	private Graph<Integer> sourceGraph;
	private String methodName;
	private int nodeNum;
	private Stack<Node<Integer>> prevNode;
	private Stack<Node<Integer>> continueNode;
	private Stack<Node<Integer>> breakNode;
	private boolean controlFlag;
	private boolean returnFlag;
	private boolean caseFlag;
	private Node<Integer> finalnode;
	private GraphInformation infos;
	private CompilationUnit unit;
	private Map<JavadocTagAnnotations, List<String>> javadocAnnotations;

	public StatementsVisitor(String methodName, CompilationUnit unit) {
		this.methodName = methodName; // name of the method to be analyzed.
		this.unit = unit;
		nodeNum = 0; // number of the node.
		sourceGraph = new Graph<Integer>(); // the graph.
		sourceGraph.addMetadataLayer(); // the layer that associate the sourceGraph elements to the layoutGraph elements.
		sourceGraph.addMetadataLayer(); // the layer that contains the code cycles.
		sourceGraph.addMetadataLayer(); // the layer that contains the code instructions.
		prevNode = new Stack<Node<Integer>>(); // stack that contain the predecessor nodes.
		continueNode = new Stack<Node<Integer>>(); // stack that contains the node to be linked if a continue occurs.
		breakNode = new Stack<Node<Integer>>(); // stack that contains the node to be linked if a break occurs.
		javadocAnnotations = new HashMap<JavadocTagAnnotations, List<String>>();
		controlFlag = false; // flag that control if a continue or a break occur.
		returnFlag = false; // flag that control if a return occur.
		caseFlag = false; // flag that control the occurrence of a break in the previous case;  
		Node<Integer> initial = new Node<Integer>(nodeNum); // the initial node.
		sourceGraph.addInitialNode(initial); // add first node to the graph.
		prevNode.push(initial); // add first node to the previous node stack.
		finalnode = null; // The final node.
		infos = new GraphInformation(); // the graph informations.
	}

	//only visit the method indicated by the user.
	@Override  
	public boolean visit(MethodDeclaration node) {
		if(node.getName().getIdentifier().equals(methodName)) {
			if(node.getJavadoc() != null) {
				javadocAnnotations.put(JavadocTagAnnotations.COVERAGE_CRITERIA, getProperty(node.getJavadoc(), JavadocTagAnnotations.COVERAGE_CRITERIA.getTag()));
				javadocAnnotations.put(JavadocTagAnnotations.INFEASIBLE_PATH, getProperty(node.getJavadoc(), JavadocTagAnnotations.INFEASIBLE_PATH.getTag()));
				javadocAnnotations.put(JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH, getProperty(node.getJavadoc(), JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH.getTag()));
				javadocAnnotations.put(JavadocTagAnnotations.ADDITIONAL_TEST_PATH, getProperty(node.getJavadoc(), JavadocTagAnnotations.ADDITIONAL_TEST_PATH.getTag()));
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<String> getProperty(Javadoc javadoc, String propertyName) {
		List<String> result = new LinkedList<String>();
		List<TagElement> tags = (List<TagElement>) javadoc.tags();
		for(TagElement tag : tags) 
			if(tag.getTagName() != null)
				if(tag.getTagName().equals(propertyName) && !tag.fragments().isEmpty()) {
					String str = tag.fragments().get(0).toString(); 
					str = str.substring(1, str.length());
					result.add(str);
				}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endVisit(MethodDeclaration node) {
		if(node.getName().getIdentifier().equals(methodName)) {
			List<Node<Integer>> nodesToRemove = new LinkedList<Node<Integer>>();
			List<Edge<Integer>> edgesToRemove = new LinkedList<Edge<Integer>>();
			for(Node<Integer> graphNode : sourceGraph.getNodes()) {
				sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.	
				if(sourceGraph.getMetadata(graphNode) == null && 
					sourceGraph.getNodeEdges(graphNode).size() == 1) {
						Set<Edge<Integer>> edgeToRemove = sourceGraph.getNodeEndEdges(graphNode);
						Edge<Integer> edgeToFinalNode = (Edge<Integer>) sourceGraph.getNodeEdges(graphNode).toArray()[0];
						for(Edge<Integer> edge : edgeToRemove) {
							Edge<Integer> newEdge = sourceGraph.addEdge(edge.getBeginNode(), edgeToFinalNode.getEndNode());
							sourceGraph.selectMetadataLayer(Layer.GUARDS.getLayer()); // select the layer to get the information.
							infos.addInformationToLayer1(sourceGraph, newEdge, (String) sourceGraph.getMetadata(edge)); // add information newEdge.
							sourceGraph.removeEdge(edge);
						}
						nodesToRemove.add(graphNode);
						edgesToRemove.add(edgeToFinalNode);
				} 
			}
			for(Node<Integer> n : nodesToRemove)
				sourceGraph.removeNode(n);

			if(!sourceGraph.getInitialNodes().iterator().hasNext()) 
				sourceGraph.addInitialNode(sourceGraph.getNodes().iterator().next());

			RenumNodesGraphVisitor visitor = new RenumNodesGraphVisitor();
				sourceGraph.accept(visitor);
		} 	
	}
	
	public Map<JavadocTagAnnotations, List<String>> getJavadocTagAnnotations() {
		return javadocAnnotations;		
	}

	@Override  
	public boolean visit(ExpressionStatement node) {
		infos.addInformationToLayer2(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override  
	public boolean visit(AssertStatement node) {
		infos.addInformationToLayer2(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override  
	public boolean visit(EmptyStatement node) {
		infos.addInformationToLayer2(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override  
	public boolean visit(VariableDeclarationStatement node) {
		infos.addInformationToLayer2(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override  
	public boolean visit(TypeDeclarationStatement node) {
		infos.addInformationToLayer2(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override  
	public boolean visit(IfStatement node) {
		Edge<Integer> edge = createConnection(); // connect the previous node to this node.
		Node<Integer> noIf = edge.getEndNode(); // the initial node of the IFStatement.
		prevNode.push(noIf); // the graph continues from the initial node of the IFStatement.
		infos.addInformationToLayer2(sourceGraph, noIf, node, unit); // add informationrmation to noIF node.
    	Edge<Integer> edgeThen = createConnection(); // visit the Then block.
    	infos.addInformationToLayer1(sourceGraph, edgeThen, node.getExpression().toString()); // add information to noIF - noIFThen edge.
    	Node<Integer> noIfThen = edgeThen.getEndNode(); // create the IFThen node.
    	prevNode.push(noIfThen); // the graph continues from the IFThen node.
    	node.getThenStatement().accept(this);
    	boolean breakThenFlag = controlFlag; // verify if a break or a continue occur in the IFThen.
    	boolean returnThenFlag = returnFlag; // verify if a return occur in the IFThen.
    	controlFlag = false;
    	returnFlag = false;
    	prevNode.push(noIf); // the graph continues from the initial node of the IFStatement.
    	Statement elseStatement = node.getElseStatement(); // get the Else block.
    	if(elseStatement != null) { // if exists visit the Else block.
    		Edge<Integer> edgeElse = createConnection();
    		infos.addInformationToLayer1(sourceGraph, edgeElse, "¬(" + node.getExpression().toString() + ")"); // add information to noIF - noIFElse edge.
        	Node<Integer> noIfElse = edgeElse.getEndNode(); // create the IFElse node. 
        	prevNode.push(noIfElse); // the graph continues from the IFElse node.
        	elseStatement.accept(this);
    	}
    	boolean breakElseFlag = controlFlag; // verify if a break or a continue occur in the IFElse.
    	boolean returnElseFlag = returnFlag; // verify if a return occur in the IFElse.
    	if(!returnThenFlag || !returnElseFlag) { // if exist in maximum one return.
    		if(!prevNode.isEmpty()) {
	    		edge = createConnection(); // create the final node of the IFStatement.
	    		if(elseStatement == null) 
	    			infos.addInformationToLayer1(sourceGraph, edge, "¬(" + node.getExpression().toString() + ")"); // add information to noIF - noIFElse edge.
	    		finalnode = edge.getEndNode(); // update the final node.
	    		returnFlag = (returnThenFlag || returnElseFlag);
	    		if(!returnFlag) { // if there are no returns.
	    		    if(!breakThenFlag || !breakElseFlag) { // if exist in maximum one break or continue.
	    		    	controlFlag = (breakThenFlag || breakElseFlag);
	    		    	if(!controlFlag) // if there are no breaks or continues.
	    		    		sourceGraph.addEdge(prevNode.pop(), edge.getEndNode()); // the connection from previous node to the final node of the IFStatement.
	    		    	else
	    		    		controlFlag = false;
	    		    }
	    		} else
	    			returnFlag = false;
	    		prevNode.push(edge.getEndNode());
    		}
    	}
    	return false;
	}

	@Override  
	public boolean visit(WhileStatement node) {
		Edge<Integer> edge = createConnection(); // connect the previous node to this node.
		Node<Integer> noWhile = edge.getEndNode(); // the initial node of the WhileStatement.
		prevNode.push(noWhile); // the graph continues from the initial node of the WhileStatement.
		infos.addInformationToLayer2(sourceGraph, noWhile, node, unit); // add information to noWhile node.
		Node<Integer> noEndWhile = sourceGraph.addNode(++nodeNum); // the final node of the WhileStatement.
		breakNode.push(noEndWhile); // if a break occur goes to the final node of the WhileStatement.
		continueNode.push(noWhile); // if a continue occur goes to the initial node of the WhileStatement.
    	Edge<Integer> edgeBody = createConnection(); // visit the while body block.
    	infos.addInformationToLayer1(sourceGraph, edgeBody, node.getExpression().toString()); // add information to noWhile - noWhileBody edge.
    	Node<Integer> noWhileBody = edgeBody.getEndNode(); // create the WhileBody node. 
    	prevNode.push(noWhileBody); // the graph continues from the WhileBody node.
    	node.getBody().accept(this);
    	continueNode.pop(); // when ends clean the stack.
    	breakNode.pop(); // when ends clean the stack.
    	if(!returnFlag) { // verify if a return occur in the WhileBody.
    		if (!controlFlag) // verify if a break or a continue occur in the WhileBody. 
	    		sourceGraph.addEdge(prevNode.pop(), noWhile); // the loop connection.
	    	else
				controlFlag = false;
    	} else
    		returnFlag = false;
    	edge = sourceGraph.addEdge(noWhile, noEndWhile); // the connection from the initial node to the final node of the WhileStatement.
    	infos.addInformationToLayer1(sourceGraph, edge, "¬(" + node.getExpression().toString() + ")"); // add information to noWhile - noEndWhile edge.
    	prevNode.push(noEndWhile); // the graph continues from the final node of the WhileStatement.
    	finalnode = noEndWhile; // update the final node.
    	return false;
	}

	@Override  
	public boolean visit(DoStatement node) {
		Edge<Integer> edge = createConnection(); // connect the previous node to this node.
		Node<Integer> noDoWhileBody = edge.getEndNode(); // create the DoWhileBody node. 
    	prevNode.push(noDoWhileBody); // the graph continues from the DoWhileBody node.
		Node<Integer> noWhile = sourceGraph.addNode(++nodeNum); // the node of the WhileStatement.
    	infos.addInformationToLayer2(sourceGraph, noWhile, node.getExpression(), unit);
    	Node<Integer> noEndDoWhile = sourceGraph.addNode(++nodeNum); // the final node of the DoStatement.
    	breakNode.push(noEndDoWhile); // if a break occur goes to the final node of the DoStatement.
    	continueNode.push(noWhile); // if a continue occur goes to the WhileStatement node.
    	node.getBody().accept(this);
    	continueNode.pop(); // when ends clean the stack.
    	breakNode.pop(); // when ends clean the stack.
    	if(!returnFlag) { // verify if a return occur in the DoWhileBody.
	    	if (!controlFlag) // verify if a break or a continue occur in the DoWhileBody.
	    		sourceGraph.addEdge(prevNode.pop(), noWhile); // the connection from the DoWhileBody node to the WhileStatement node.
	    	else 
				controlFlag = false;
    	} else
    		returnFlag = false;
    	edge = sourceGraph.addEdge(noWhile, noDoWhileBody); // the loop connection.
    	infos.addInformationToLayer1(sourceGraph, edge, node.getExpression().toString()); // add information to noWhile - noDoWhile edge.
    	edge = sourceGraph.addEdge(noWhile, noEndDoWhile); // the connection from the WhileStatement node to the final node of the DoWhileStatement.
    	infos.addInformationToLayer1(sourceGraph, edge, "¬(" + node.getExpression().toString() + ")"); // add information to noWhile - noEndDoWhile edge.
    	prevNode.push(noEndDoWhile); // the graph continues from the final node of the DoWhileStatement.
    	finalnode = noEndDoWhile; // update the final node.
    	return false;
	}

	@Override  
	public boolean visit(ForStatement node) {
		Edge<Integer> edge = createConnection(); // initialization of the ForStatement.
		String initializers = Description.EMPTY;
		for(Object initNode : node.initializers()) {
			initializers += initNode.toString() + ", ";
			infos.addInformationToLayer2(sourceGraph, edge.getBeginNode(), (ASTNode) initNode, unit); // add information to noInitFor node.
		}
		initializers = initializers.substring(0, initializers.length() - 2);
		infos.addInformationToLayer1(sourceGraph, edge, initializers); // add information to previous node - noFor edge.
		Node<Integer> noFor = edge.getEndNode(); // the initial node of the ForStatement.
		infos.addInformationToLayer2(sourceGraph, noFor, node, unit);
    	Node<Integer> incFor = sourceGraph.addNode(++nodeNum); // the node of the incFor.
    	String updaters = Description.EMPTY;
    	for(Object incNode : node.updaters()) {
    		updaters += incNode.toString() + ", ";
    		infos.addInformationToLayer2(sourceGraph, incFor, (ASTNode) incNode, unit);
    	}
    	updaters = updaters.substring(0, updaters.length() - 2);
    	Node<Integer> noEndFor = sourceGraph.addNode(++nodeNum); // the final node of the ForStatement.
    	breakNode.push(noEndFor); // if a break occur goes to the final node of the ForStatement.
    	continueNode.push(incFor); // if a continue occur goes to the incFor node.
    	prevNode.push(noFor); // the graph continues from the initial node of the ForStatement.
    	Edge<Integer> edgeBody = createConnection(); // visit the ForStatement body block.
    	infos.addInformationToLayer1(sourceGraph, edgeBody, node.getExpression().toString()); // add information to noFor - ForBody edge.
    	Node<Integer> noForBody = edgeBody.getEndNode(); // create the ForBody node. 
    	prevNode.push(noForBody); // the graph continues from the ForBody node.
    	node.getBody().accept(this);
    	continueNode.pop(); // when ends clean the stack.
    	breakNode.pop(); // when ends clean the stack.
    	if(!returnFlag) { // verify if a return occur in the ForBody.
	    	if(!controlFlag) // verify if a break or a continue occur in the ForBody.
	    		sourceGraph.addEdge(prevNode.pop(), incFor); // connect the previous node to the increment node.
	    	else 
				controlFlag = false;
		} else
			returnFlag = false;
    	edge = sourceGraph.addEdge(incFor, noFor); // the loop connection.
    	infos.addInformationToLayer1(sourceGraph, edge, updaters); // add information to incFor - noFor edge.
    	edge = sourceGraph.addEdge(noFor, noEndFor); // the connection from the initial node to the final node of the ForStatement.
    	infos.addInformationToLayer1(sourceGraph, edge, "¬(" + node.getExpression().toString() + ")"); // add information to noFor - noEndFor edge.
    	prevNode.push(noEndFor); // the graph continues from the final node of the DoWhileStatement.
    	finalnode = noEndFor; // update the final node.
    	return false;
	}

	@Override  
	public boolean visit(EnhancedForStatement node) {
		Edge<Integer> edge = createConnection(); // connect the previous node to this node.
		Node<Integer> noForEach = edge.getEndNode(); // the initial node of the EnhancedForStatement.
		prevNode.push(noForEach);
		infos.addInformationToLayer2(sourceGraph, noForEach, node, unit); // add information to noForBody node.
		Node<Integer> noEndForEach = sourceGraph.addNode(++nodeNum); // the final node of the EnhancedForStatement.
		breakNode.push(noEndForEach); // if a break occur goes to the final node of the EnhancedForStatement.
		continueNode.push(noForEach); // if a break occur goes to the initial node of the EnhancedForStatement.
		Edge<Integer> edgeBody = createConnection(); // visit the forEach body block.
		infos.addInformationToLayer1(sourceGraph, edgeBody, node.getExpression().toString() + ".hasNext()"); // add information to noForEach - noForEachBody edge.
    	Node<Integer> noForEachBody = edgeBody.getEndNode(); // create the ForEachBody node.
    	prevNode.push(noForEachBody); // the graph continues from the ForEachBody node.
		node.getBody().accept(this);
		continueNode.pop(); // when ends clean the stack.
		breakNode.pop(); // when ends clean the stack.
		if (!returnFlag) { // verify if a return occur in the ForEachBody.
			if (!controlFlag) // verify if a break or a continue occur in the ForEachBody.
				sourceGraph.addEdge(prevNode.pop(), noForEach); // the loop connection.
			else
				controlFlag = false;
		} else
			returnFlag = false;
		edge = sourceGraph.addEdge(noForEach, noEndForEach); // the loop connection.
		infos.addInformationToLayer1(sourceGraph, edge, "¬(" + node.getExpression().toString() + ".hasNext()" + ")"); // add information to noForEach - noEndForEach edge.
		prevNode.push(noEndForEach); // the graph continues from the final node of the EnhancedForStatement.
		finalnode = noEndForEach; // update the final node.
		return false;
	}

	@Override  
	public boolean visit(SwitchStatement node) {
		Edge<Integer> edge = createConnection(); // connect the previous node to this node.
		Node<Integer> noSwitch = edge.getEndNode(); // the initial node of the SwitchStatement.
		infos.addInformationToLayer2(sourceGraph, noSwitch, node, unit); // add information to noSwitch node.
		Node<Integer> noEndSwitch = sourceGraph.addNode(++nodeNum); // the final node of the SwitchStatement.
    	breakNode.push(noEndSwitch); // if a break occur goes to the final node of the ForStatement.
    	continueNode.push(noEndSwitch); // if a continue occur goes to the incFor node.
    	prevNode.push(noEndSwitch); 
		prevNode.push(noSwitch); // the graph continues from the initial node of the SwitchStatement.
		return true;
	}

	@Override
	public void endVisit(SwitchStatement node) {
		// if the default case doesn't have a break.
		// the number 2 represents the initial and the final nodes of the SwitchStatement.
		if(prevNode.size() > 2) {
			sourceGraph.addEdge(prevNode.pop(), breakNode.peek());
			while(prevNode.size() != 2) // if one or more cases doesn't have a break.
				prevNode.pop();
		}
		breakNode.pop(); // when ends clean the stack.
		continueNode.pop(); // when ends clean the stack.
		prevNode.pop(); // the graph continues from the final node of the SwitchStatement.
		finalnode = prevNode.peek(); // update the final node.
		controlFlag = false;
		returnFlag = false;
	}

	@Override  
	public boolean visit(SwitchCase node) {
		if(!returnFlag) { // verify if a return occur in the SwitchBody.
			if(controlFlag) { // if there is a case with no break.
				while(prevNode.size() != 2) // the number 2 represents the initial and the final nodes of the SwitchStatement.
					prevNode.pop();
				controlFlag = false;
				caseFlag = true;
			} else
				caseFlag = false;
		} else
			returnFlag = false;
		nodeNum++;
		Node<Integer> n = sourceGraph.addNode(nodeNum); // create the node of the case.
		infos.addInformationToLayer2(sourceGraph, n, node, unit);
		Edge<Integer> edge = null;
		if(!caseFlag && prevNode.size() > 2)
			edge = sourceGraph.addEdge(prevNode.pop(), n); // create a edge from the previous node to this node.
		edge = sourceGraph.addEdge(prevNode.peek(), n); // create a edge from the begin of switch to this node.
		if(!node.isDefault()) // if the node is the default of the switch. 	
			infos.addInformationToLayer1(sourceGraph, edge, "case " + node.getExpression().toString() + ":"); // add information to noSwitch - case edge.
		else 
			infos.addInformationToLayer1(sourceGraph, edge, "default:"); // add information to noSwitch - default edge.
		prevNode.push(n); // the graph continues from the case node of the SwitchStatement.
		return false;
	}

	@Override  
	public boolean visit(BreakStatement node) {
		if(!prevNode.isEmpty()) {
			Edge<Integer> edge = sourceGraph.addEdge(prevNode.pop(), breakNode.peek()); // create the edge from the previous node to the break node.
			infos.addInformationToLayer1(sourceGraph, edge, "break;"); // add information to previous node - break.
			infos.addInformationToLayer2(sourceGraph, edge.getBeginNode(), node, unit);
			controlFlag = true;
		}
		return false;
	}	

	@Override  
	public boolean visit(ContinueStatement node) {
		if(!prevNode.isEmpty()) {
			Edge<Integer> edge = sourceGraph.addEdge(prevNode.pop(), continueNode.peek()); // create the edge from the previous node to the continue node.
			infos.addInformationToLayer1(sourceGraph, edge, "continue;"); // add information to previous node - continue.
			infos.addInformationToLayer2(sourceGraph, edge.getBeginNode(), node, unit);
			controlFlag = true;
		}
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		if(!prevNode.isEmpty()) {
			Edge<Integer> edge = createConnection(); // create the edge from the previous node to the return node.
			infos.addInformationToLayer1(sourceGraph, edge, "return;"); // add information to previous node - return.
			sourceGraph.addFinalNode(edge.getEndNode()); // add the return node to the final nodes.
			infos.addInformationToLayer2(sourceGraph, edge.getEndNode(), node, unit); // add information to return node.
			returnFlag = true;
			finalnode = null;
		}
		return false;
	}

	private Edge<Integer> createConnection() {
    	nodeNum++; // increase the node number.
    	Node<Integer> node = sourceGraph.addNode(nodeNum); // create a new node.
    	return sourceGraph.addEdge(prevNode.pop(), node); // create a edge to the previous node to the new one.
	}

	public Graph<Integer> getGraph() {
		if(finalnode != null) // if exist final node.
			sourceGraph.addFinalNode(finalnode); // add final node to the final nodes of the graph.
		return sourceGraph;
	}
}