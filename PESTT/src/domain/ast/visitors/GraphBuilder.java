package domain.ast.visitors;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import adt.graph.Edge;
import adt.graph.Graph;
import adt.graph.Node;
import domain.GraphInformation;
import domain.RenumNodesGraphVisitor;
import domain.constants.Layer;
import domain.exceptions.HashCreationException;

public class GraphBuilder extends ASTVisitor {

	private static final String NEG = "¬";
	private static final boolean DEBUG = true;
	private Graph sourceGraph;
	private String methodName;
	private int nodeNum;
	private Stack<Node> prevNode;
	private Stack<Node> continueNode;
	private Stack<Node> breakNode;
	private boolean controlFlag;
	private boolean returnFlag;
	private boolean caseFlag;
	private Node finalnode;
	private GraphInformation infos;
	private CompilationUnit unit;
	private byte[] hash;
	private List<SingleVariableDeclaration> params;
	private List<VariableDeclarationFragment> attributes;
	private List<EnumDeclaration> enumFields;

	public GraphBuilder(String methodName, CompilationUnit unit) {
		this.methodName = methodName; // name of the method to be analyzed.
		this.unit = unit;
		nodeNum = 0; // number of the node.
		sourceGraph = new Graph(); // the graph.
		sourceGraph.addMetadataLayer(); // the layer that associates the sourceGraph elements to the layoutGraph elements.
		sourceGraph.addMetadataLayer(); // the layer that contains the code cycles.
		sourceGraph.addMetadataLayer(); // the layer that contains the code instructions.
		attributes = new LinkedList<VariableDeclarationFragment>(); // the class attributes.
		enumFields = new LinkedList<EnumDeclaration>(); // the enum class attributes.
		prevNode = new Stack<Node>(); // stack that contains the predecessor nodes.
		continueNode = new Stack<Node>(); // stack that contains the node to be linked if a continue occurs.
		breakNode = new Stack<Node>(); // stack that contains the node to be linked if a break occurs.
		controlFlag = false; // flag that controls if a continue or a break occurs.
		returnFlag = false; // flag that controls if a return occurs.
		caseFlag = false; // flag that controls the occurrence of a break in the previous case;  
		Node initial = new Node(nodeNum); // the initial node.
		sourceGraph.addInitialNode(initial); // adds first node to the graph.
		prevNode.push(initial); // adds first node to the previous node stack.
		finalnode = initial; // The final node.
		infos = new GraphInformation(); // the graph informations.
	}

	/**
	 * A pretty printer debug method, for inside methods.
	 */
	@Override
	public void preVisit(ASTNode node) {
		if (DEBUG) {
			String s = node.getClass().getSimpleName();
			if (s.equals("CompilationUnit"))
				System.out.println();
			ASTNode parent = node;
			int depth = 1;
			do {
				depth++;
				parent = parent.getParent();
			} while (parent != null);
			for (int i = 2; i < depth; i++) {
				System.out.print(" ");
			}
			System.out.println(s);
			super.preVisit(node);
		}
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		enumFields.add(node);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		if (node.getParent().getParent().getNodeType() != ASTNode.TYPE_DECLARATION) {
			List<VariableDeclarationFragment> fragments = node.fragments();
			for (VariableDeclarationFragment attribute : fragments)
				attributes.add(attribute);
		}
		return false;
	}

	//only visit the method indicated by the user.	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration node) {
		String signature = getMethodSignature(node);
		if (signature.equals(methodName)) {
			hash = getMethodHash(node);
			params = node.parameters();
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private String getMethodSignature(MethodDeclaration node) {
		String signature = node.getName() + "(";
		List<SingleVariableDeclaration> methodParams = node.parameters();
		for (SingleVariableDeclaration param : methodParams)
			signature += param.toString() + ", ";
		if (!methodParams.isEmpty())
			signature = signature.substring(0, signature.length() - 2);
		signature += ")";
		return signature;
	}


	private byte[] getMethodHash(MethodDeclaration method) {
		try {
			byte[] bytesOfMessage = method.getBody().toString()
					.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(bytesOfMessage);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		new HashCreationException().printStackTrace();
		return null;
	}

	public List<SingleVariableDeclaration> getMethodParameters() {
		return params;
	}

	public List<VariableDeclarationFragment> getClassAttributes() {
		return attributes;
	}

	public List<EnumDeclaration> getEnumClassAttributes() {
		return enumFields;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		String signature = getMethodSignature(node);
		if (signature.equals(methodName)) {
			List<Node> nodesToRemove = new LinkedList<Node>();
			List<Edge> edgesToRemove = new LinkedList<Edge>();
			for (Node graphNode : sourceGraph.getNodes()) {
				sourceGraph.selectMetadataLayer(Layer.INSTRUCTIONS.getLayer()); // select the layer to get the information.	
				if (sourceGraph.getMetadata(graphNode) == null
						&& sourceGraph.getNodeEdges(graphNode).size() == 1) {
					Set<Edge> edgeToRemove = sourceGraph
							.getNodeEndEdges(graphNode);
					Edge edgeToFinalNode = (Edge) sourceGraph
							.getNodeEdges(graphNode).toArray()[0];
					for (Edge edge : edgeToRemove) {
						Edge newEdge = sourceGraph.addEdge(
								edge.getBeginNode(),
								edgeToFinalNode.getEndNode());
						sourceGraph
								.selectMetadataLayer(Layer.GUARDS.getLayer()); // select the layer to get the information.
						infos.addInformationToLayer2(sourceGraph, newEdge,
								(String) sourceGraph.getMetadata(edge)); // add information newEdge.
						sourceGraph.removeEdge(edge);
					}
					nodesToRemove.add(graphNode);
					edgesToRemove.add(edgeToFinalNode);
				}
			}

			for (Node n : sourceGraph.getNodes())
				if (sourceGraph.getNodeEndEdges(n).size() == 0
						&& !sourceGraph.isInitialNode(n)
						&& !nodesToRemove.contains(n)) {
					edgesToRemove.addAll(sourceGraph.getNodeEdges(n));
					nodesToRemove.add(n);
				}

			for (Edge edge : edgesToRemove)
				sourceGraph.removeEdge(edge);

			for (Node n : nodesToRemove)
				sourceGraph.removeNode(n);

			if (!sourceGraph.getInitialNodes().iterator().hasNext())
				sourceGraph.addInitialNode(sourceGraph.getNodes().iterator()
						.next());

			RenumNodesGraphVisitor visitor = new RenumNodesGraphVisitor();
			sourceGraph.accept(visitor);
			sourceGraph.sortNodes();
		}
	}

	public byte[] getMethodHash() {
		return hash;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		infos.addInformationToLayer1(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override
	public boolean visit(AssertStatement node) {
		infos.addInformationToLayer1(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		infos.addInformationToLayer1(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		infos.addInformationToLayer1(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		if (!prevNode.isEmpty()) {
			Edge edge = createConnection(); // create the edge from the previous node to the throws node.
			infos.addInformationToLayer2(sourceGraph, edge, "throws;"); // add information to previous node - throws.
			sourceGraph.addFinalNode(edge.getEndNode()); // add the throws node to the final nodes.
			infos.addInformationToLayer1(sourceGraph, edge.getEndNode(), node,
					unit); // add information to throws node.
			returnFlag = true;
			finalnode = null;
		}
		return false;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		infos.addInformationToLayer1(sourceGraph, prevNode.peek(), node, unit);
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		Edge edge = createConnection(); // connect the previous node to this node.
		Node noIf = edge.getEndNode(); // the initial node of the IFStatement.
		prevNode.push(noIf); // the graph continues from the initial node of the IFStatement.
		infos.addInformationToLayer1(sourceGraph, noIf, node, unit); // add information to noIF node.
		Edge edgeThen = createConnection(); // visit the Then block.
		infos.addInformationToLayer2(sourceGraph, edgeThen, node
				.getExpression().toString()); // add information to noIF - noIFThen edge.
		Node noIfThen = edgeThen.getEndNode(); // create the IFThen node.
		prevNode.push(noIfThen); // the graph continues from the IFThen node.
		node.getThenStatement().accept(this);
		boolean breakThenFlag = controlFlag; // verify if a break or a continue occur in the IFThen.
		boolean returnThenFlag = returnFlag; // verify if a return occur in the IFThen.
		controlFlag = false;
		returnFlag = false;
		prevNode.push(noIf); // the graph continues from the initial node of the IFStatement.
		Statement elseStatement = node.getElseStatement(); // get the Else block.
		if (elseStatement != null) { // if exists visit the Else block.
			Edge edgeElse = createConnection();
			infos.addInformationToLayer2(sourceGraph, edgeElse, NEG + "("
					+ node.getExpression().toString() + ")"); // add information to noIF - noIFElse edge.
			Node noIfElse = edgeElse.getEndNode(); // create the IFElse node. 
			prevNode.push(noIfElse); // the graph continues from the IFElse node.
			elseStatement.accept(this);
		}
		boolean breakElseFlag = controlFlag; // verify if a break or a continue occur in the IFElse.
		boolean returnElseFlag = returnFlag; // verify if a return occur in the IFElse.
		if (!returnThenFlag || !returnElseFlag) { // if exist in maximum one return.
			if (!prevNode.isEmpty()) {
				edge = createConnection(); // create the final node of the IFStatement.
				if (elseStatement == null)
					infos.addInformationToLayer2(sourceGraph, edge, NEG + "("
							+ node.getExpression().toString() + ")"); // add information to noIF - noIFElse edge.
				returnFlag = (returnThenFlag || returnElseFlag);
				if (!returnFlag) { // if there are no returns.
					if (!breakThenFlag || !breakElseFlag) { // if exist in maximum one break or continue.
						controlFlag = (breakThenFlag || breakElseFlag);
						if (!controlFlag) // if there are no breaks or continues.
							sourceGraph.addEdge(prevNode.pop(),
									edge.getEndNode()); // the connection from previous node to the final node of the IFStatement.
						else
							controlFlag = false;
					}
				} else
					returnFlag = false;
				finalnode = edge.getEndNode(); // update the final node.
				prevNode.push(edge.getEndNode());
			}
		}
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		if (!prevNode.isEmpty()) {
			Edge edge = createConnection(); // create the edge from the previous node to the try-catch node.
			infos.addInformationToLayer2(sourceGraph, edge, "try;"); // add information to previous node.
			Node noTry = edge.getEndNode(); // the initial node of the TryStatement.
			prevNode.push(noTry); // the graph continues from the initial node of the TryStatement.
			node.getBody().accept(this);
			edge = createConnection();
			infos.addInformationToLayer2(sourceGraph, edge, "finally;"); // add information to previous node.
			Node noFinally = edge.getEndNode();
			prevNode.push(noFinally);
			node.getFinally().accept(this);
			//prevNode.push(noTry);
		}
		return true;
	}

	@Override
	public boolean visit(CatchClause node) {
		if (!prevNode.isEmpty()) {
			Edge edge = createConnection();
			infos.addInformationToLayer2(sourceGraph, edge, "catch;"); // add information to previous node.
			sourceGraph.addFinalNode(edge.getEndNode());
			Node body = edge.getEndNode();
			infos.addInformationToLayer1(sourceGraph, edge.getEndNode(), node,
					unit);
			prevNode.push(body);
			node.getBody().accept(this);
			finalnode = null;
		}
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		Edge edge = createConnection(); // connect the previous node to this node.
		Node noWhile = edge.getEndNode(); // the initial node of the WhileStatement.
		prevNode.push(noWhile); // the graph continues from the initial node of the WhileStatement.
		infos.addInformationToLayer1(sourceGraph, noWhile, node, unit); // add information to noWhile node.
		Node noEndWhile = sourceGraph.addNode(++nodeNum); // the final node of the WhileStatement.
		breakNode.push(noEndWhile); // if a break occur goes to the final node of the WhileStatement.
		continueNode.push(noWhile); // if a continue occur goes to the initial node of the WhileStatement.
		Edge edgeBody = createConnection(); // visit the while body block.
		infos.addInformationToLayer2(sourceGraph, edgeBody, node
				.getExpression().toString()); // add information to noWhile - noWhileBody edge.
		Node noWhileBody = edgeBody.getEndNode(); // create the WhileBody node. 
		prevNode.push(noWhileBody); // the graph continues from the WhileBody node.
		node.getBody().accept(this);
		continueNode.pop(); // when ends clean the stack.
		breakNode.pop(); // when ends clean the stack.
		if (!returnFlag) { // verify if a return occur in the WhileBody.
			if (!controlFlag) // verify if a break or a continue occur in the WhileBody. 
				sourceGraph.addEdge(prevNode.pop(), noWhile); // the loop connection.
			else
				controlFlag = false;
		} else
			returnFlag = false;
		edge = sourceGraph.addEdge(noWhile, noEndWhile); // the connection from the initial node to the final node of the WhileStatement.
		infos.addInformationToLayer2(sourceGraph, edge, NEG + "("
				+ node.getExpression().toString() + ")"); // add information to noWhile - noEndWhile edge.
		prevNode.push(noEndWhile); // the graph continues from the final node of the WhileStatement.
		finalnode = noEndWhile; // update the final node.
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		Edge edge = createConnection(); // connect the previous node to this node.
		Node noDoWhileBody = edge.getEndNode(); // create the DoWhileBody node. 
		prevNode.push(noDoWhileBody); // the graph continues from the DoWhileBody node.
		Node noWhile = sourceGraph.addNode(++nodeNum); // the node of the WhileStatement.
		infos.addInformationToLayer1(sourceGraph, noWhile, node, unit);
		Node noEndDoWhile = sourceGraph.addNode(++nodeNum); // the final node of the DoStatement.
		breakNode.push(noEndDoWhile); // if a break occur goes to the final node of the DoStatement.
		continueNode.push(noWhile); // if a continue occur goes to the WhileStatement node.
		node.getBody().accept(this);
		continueNode.pop(); // when ends clean the stack.
		breakNode.pop(); // when ends clean the stack.
		if (!returnFlag) { // verify if a return occur in the DoWhileBody.
			if (!controlFlag) // verify if a break or a continue occur in the DoWhileBody.
				sourceGraph.addEdge(prevNode.pop(), noWhile); // the connection from the DoWhileBody node to the WhileStatement node.
			else
				controlFlag = false;
		} else
			returnFlag = false;
		edge = sourceGraph.addEdge(noWhile, noDoWhileBody); // the loop connection.
		infos.addInformationToLayer2(sourceGraph, edge, node.getExpression()
				.toString()); // add information to noWhile - noDoWhile edge.
		edge = sourceGraph.addEdge(noWhile, noEndDoWhile); // the connection from the WhileStatement node to the final node of the DoWhileStatement.
		infos.addInformationToLayer2(sourceGraph, edge, NEG + "("
				+ node.getExpression().toString() + ")"); // add information to noWhile - noEndDoWhile edge.
		prevNode.push(noEndDoWhile); // the graph continues from the final node of the DoWhileStatement.
		finalnode = noEndDoWhile; // update the final node.
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		Edge edge = createConnection(); // initialization of the ForStatement.
		for (Object initNode : node.initializers())
			infos.addInformationToLayer1(sourceGraph, edge.getBeginNode(),
					(ASTNode) initNode, unit); // add information to noInitFor node.
		Node noFor = edge.getEndNode(); // the initial node of the ForStatement.
		infos.addInformationToLayer1(sourceGraph, noFor, node, unit);
		Node incFor = sourceGraph.addNode(++nodeNum); // the node of the incFor.
		for (Object incNode : node.updaters())
			infos.addInformationToLayer1(sourceGraph, incFor,
					(ASTNode) incNode, unit);
		Node noEndFor = sourceGraph.addNode(++nodeNum); // the final node of the ForStatement.
		breakNode.push(noEndFor); // if a break occur goes to the final node of the ForStatement.
		continueNode.push(incFor); // if a continue occur goes to the incFor node.
		prevNode.push(noFor); // the graph continues from the initial node of the ForStatement.
		Edge edgeBody = createConnection(); // visit the ForStatement body block.
		infos.addInformationToLayer2(sourceGraph, edgeBody, node
				.getExpression().toString()); // add information to noFor - ForBody edge.
		Node noForBody = edgeBody.getEndNode(); // create the ForBody node. 
		prevNode.push(noForBody); // the graph continues from the ForBody node.
		node.getBody().accept(this);
		continueNode.pop(); // when ends clean the stack.
		breakNode.pop(); // when ends clean the stack.
		if (!returnFlag) { // verify if a return occur in the ForBody.
			if (!controlFlag) // verify if a break or a continue occur in the ForBody.
				sourceGraph.addEdge(prevNode.pop(), incFor); // connect the previous node to the increment node.
			else
				controlFlag = false;
		} else
			returnFlag = false;
		edge = sourceGraph.addEdge(incFor, noFor); // the loop connection.
		edge = sourceGraph.addEdge(noFor, noEndFor); // the connection from the initial node to the final node of the ForStatement.
		infos.addInformationToLayer2(sourceGraph, edge, NEG + "("
				+ node.getExpression().toString() + ")"); // add information to noFor - noEndFor edge.
		prevNode.push(noEndFor); // the graph continues from the final node of the DoWhileStatement.
		finalnode = noEndFor; // update the final node.
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		Edge edge = createConnection(); // connect the previous node to this node.
		Node noForEach = edge.getEndNode(); // the initial node of the EnhancedForStatement.
		prevNode.push(noForEach);
		infos.addInformationToLayer1(sourceGraph, noForEach, node, unit); // add information to noForBody node.
		Node noEndForEach = sourceGraph.addNode(++nodeNum); // the final node of the EnhancedForStatement.
		breakNode.push(noEndForEach); // if a break occur goes to the final node of the EnhancedForStatement.
		continueNode.push(noForEach); // if a break occur goes to the initial node of the EnhancedForStatement.
		Edge edgeBody = createConnection(); // visit the forEach body block.
		infos.addInformationToLayer2(sourceGraph, edgeBody, node
				.getExpression().toString() + ".hasNext()"); // add information to noForEach - noForEachBody edge.
		Node noForEachBody = edgeBody.getEndNode(); // create the ForEachBody node.
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
		infos.addInformationToLayer2(sourceGraph, edge, NEG + "("
				+ node.getExpression().toString() + ".hasNext()" + ")"); // add information to noForEach - noEndForEach edge.
		prevNode.push(noEndForEach); // the graph continues from the final node of the EnhancedForStatement.
		finalnode = noEndForEach; // update the final node.
		return false;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		Edge edge = createConnection(); // connect the previous node to this node.
		Node noSwitch = edge.getEndNode(); // the initial node of the SwitchStatement.
		infos.addInformationToLayer1(sourceGraph, noSwitch, node, unit); // add information to noSwitch node.
		Node noEndSwitch = sourceGraph.addNode(++nodeNum); // the final node of the SwitchStatement.
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
		if (prevNode.size() > 2) {
			sourceGraph.addEdge(prevNode.pop(), breakNode.peek());
			while (prevNode.size() != 2)
				// if one or more cases doesn't have a break.
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
		if (!returnFlag) { // verify if a return occur in the SwitchBody.
			if (controlFlag) { // if there is a case with no break.
				while (prevNode.size() != 2)
					// the number 2 represents the initial and the final nodes of the SwitchStatement.
					prevNode.pop();
				controlFlag = false;
				caseFlag = true;
			} else
				caseFlag = false;
		} else
			returnFlag = false;
		nodeNum++;
		Node n = sourceGraph.addNode(nodeNum); // create the node of the case.
		infos.addInformationToLayer1(sourceGraph, n, node, unit);
		Edge edge = null;
		if (!caseFlag && prevNode.size() > 2)
			edge = sourceGraph.addEdge(prevNode.pop(), n); // create a edge from the previous node to this node.
		edge = sourceGraph.addEdge(prevNode.peek(), n); // create a edge from the begin of switch to this node.
		if (!node.isDefault()) // if the node is the default of the switch. 	
			infos.addInformationToLayer2(sourceGraph, edge, "case "
					+ node.getExpression().toString() + ":"); // add information to noSwitch - case edge.
		else
			infos.addInformationToLayer2(sourceGraph, edge, "default:"); // add information to noSwitch - default edge.
		prevNode.push(n); // the graph continues from the case node of the SwitchStatement.
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		if (!prevNode.isEmpty()) {
			Edge edge = sourceGraph.addEdge(prevNode.pop(),
					breakNode.peek()); // create the edge from the previous node to the break node.
			infos.addInformationToLayer2(sourceGraph, edge, "break;"); // add information to previous node - break.
			infos.addInformationToLayer1(sourceGraph, edge.getBeginNode(),
					node, unit);
			controlFlag = true;
		}
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		if (!prevNode.isEmpty()) {
			Edge edge = sourceGraph.addEdge(prevNode.pop(),
					continueNode.peek()); // create the edge from the previous node to the continue node.
			infos.addInformationToLayer2(sourceGraph, edge, "continue;"); // add information to previous node - continue.
			infos.addInformationToLayer1(sourceGraph, edge.getBeginNode(),
					node, unit);
			controlFlag = true;
		}
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		if (!prevNode.isEmpty()) {
			Edge edge = createConnection(); // create the edge from the previous node to the return node.
			infos.addInformationToLayer2(sourceGraph, edge, "return;"); // add information to previous node - return.
			sourceGraph.addFinalNode(edge.getEndNode()); // add the return node to the final nodes.
			infos.addInformationToLayer1(sourceGraph, edge.getEndNode(), node,
					unit); // add information to return node.
			returnFlag = true;
			finalnode = null;
		}
		return false;
	}

	private Edge createConnection() {
		nodeNum++; // increase the node number.
		Node node = sourceGraph.addNode(nodeNum); // create a new node.
		return sourceGraph.addEdge(prevNode.pop(), node); // create a edge to the previous node to the new one.
	}

	public Graph getGraph() {
		if (finalnode != null)
			sourceGraph.addFinalNode(finalnode); // add final node to the final nodes of the graph.
		return sourceGraph;
	}
}