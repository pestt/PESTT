package tour;

import java.util.LinkedList;
import java.util.List;

import sourcegraph.Graph;
import sourcegraph.Node;
import sourcegraph.Path;

public class Tour {
	
	private List<Object> executedPaths;
	private List<Path<Integer>> testRequirements;
	private List<Path<Integer>> coveredPaths;
	
	public Tour(List<Object> executedPaths, List<Path<Integer>> testRequirements) {
		this.executedPaths = executedPaths;
		this.testRequirements = testRequirements;
	}
	
	@SuppressWarnings("unchecked")
	public List<Path<Integer>> getTourPathCoverage(Object executedPath) {
		coveredPaths = new LinkedList<Path<Integer>>();
		if(executedPath instanceof Graph<?>) {
			for(Path<Integer> path : testRequirements) 
				if(((Graph<Integer>) executedPath).isPath(path))
					coveredPaths.add(path);
		} else if(executedPath instanceof Path<?>) {
			for(Path<Integer> path : testRequirements) 
				if(((Path<Integer>) executedPath).isSubPath(path))
					coveredPaths.add(path);
	} else {
			List<Path<Integer>> total = new LinkedList<Path<Integer>>();
			for(Object obj : executedPaths) 
				if(!(obj instanceof String)) {
					List<Path<Integer>> aux = getTourPathCoverage(obj);
					for(Path<Integer> covered : aux)
						if(!total.contains(covered))
							total.add(covered);
				}
			coveredPaths = total;
		}
		return coveredPaths;
	}
	
	@SuppressWarnings("unchecked")
	public List<Path<Integer>> getSidetripPathCoverage(Object executedPath) {
		if(executedPath instanceof Path<?> && containsLoop((Path<Integer>) executedPath)) {
			getTourPathCoverage(executedPath);
			List<Path<Integer>> notCoveredPaths = getNotCoveredPaths();
			for(Path<Integer> notCoveredPath : notCoveredPaths)
				if(containsAllNodesInOrder((Path<Integer>) executedPath, notCoveredPath))
					getSidetrips(executedPath, notCoveredPath);
		} else if(executedPath instanceof String) {
			List<Path<Integer>> total = new LinkedList<Path<Integer>>();
			for(Object obj : executedPaths) 
				if(!(obj instanceof String)) {
					List<Path<Integer>> aux = getSidetripPathCoverage(obj);
					for(Path<Integer> covered : aux)
						if(!total.contains(covered))
							total.add(covered);
				}
			coveredPaths = total;
		} else
			getTourPathCoverage(executedPath);
		return coveredPaths;
	}

	@SuppressWarnings("unchecked")
	public List<Path<Integer>> getDetourPathCoverage(Object executedPath) {
		if(executedPath instanceof Path<?>) {
			getTourPathCoverage(executedPath);
			List<Path<Integer>> notCoveredPaths = getNotCoveredPaths();
			for(Path<Integer> notCoveredPath : notCoveredPaths)
				if(containsAllNodesInOrder((Path<Integer>) executedPath, notCoveredPath))
					coveredPaths.add(notCoveredPath);
		} else if(executedPath instanceof String) {
			List<Path<Integer>> total = new LinkedList<Path<Integer>>();
			for(Object obj : executedPaths) 
				if(!(obj instanceof String)) {
					List<Path<Integer>> aux = getDetourPathCoverage(obj);
					for(Path<Integer> covered : aux)
						if(!total.contains(covered))
							total.add(covered);
				}
			coveredPaths = total;
		} else
			getTourPathCoverage(executedPath);
		return coveredPaths;
	}

	@SuppressWarnings("unchecked")
	private void getSidetrips(Object executedPath, Path<Integer> notCoveredPath) {
		for(Node<Integer> node : notCoveredPath)
			if(isLoopNode((Path<Integer>) executedPath, node)) {
				List<Integer> indexes = getLoopIndexes((Path<Integer>) executedPath, node);
				Path<Integer> fakePath = createPath(indexes, (Path<Integer>) executedPath);
				if(((Path<Integer>) fakePath).isSubPath(notCoveredPath))
					coveredPaths.add(notCoveredPath);
			}
	}

	private Path<Integer> createPath(List<Integer> indexes, Path<Integer> executedPath) {
		Path<Integer> path = null;
		for(int i = 0; i < executedPath.getPathNodes().size(); i++)
			if(i <= indexes.get(0) || i >= indexes.get(1) + 1) 
				if(path == null)
					path = new Path<Integer>(executedPath.getPathNodes().get(i));
				else
					path.addNode(executedPath.getPathNodes().get(i));
		return path;
	}

	private List<Path<Integer>> getNotCoveredPaths() {
		LinkedList<Path<Integer>> notCoveredGraphs = new LinkedList<Path<Integer>>();
		for(Path<Integer> path : testRequirements) 
			if(!coveredPaths.contains(path))
				notCoveredGraphs.add(path);
		return notCoveredGraphs;
	}
	
	private boolean containsAllNodesInOrder(Path<Integer> executedPath, Path<Integer> path) {
		int index = 0;
		for(Node<Integer> node : path.getPathNodes())
			if(!executedPath.containsNode(node))
				return false;
			else {
				List<Node<Integer>> executedPathNodes = executedPath.getPathNodes();
				while (index < executedPathNodes.size() && executedPathNodes.get(index) != node)
					index++;
				if (index == executedPathNodes.size())
					return false;
			}
		return true;
	}
	
	private boolean containsLoop(Path<Integer> executedPath) {
		for(Node<Integer> node : executedPath)
			if(getNumberOfOccurrences(executedPath, node) >= 2)
				return true;
		return false;
	}
	
	private int getNumberOfOccurrences(Path<Integer> executedPath, Node<Integer> node) {
		int occurrences = 0;
		for(Node<Integer> n : executedPath)
			if(n == node)
				occurrences++;
		return occurrences;
	}
	
	private boolean isLoopNode(Path<Integer> executedPath, Node<Integer> node) {
		if(getNumberOfOccurrences(executedPath, node) >= 2)
			return true;
	return false;
	}
	
	private List<Integer> getLoopIndexes(Path<Integer> executedPath, Node<Integer> node) {
		List<Integer> indexes = new LinkedList<Integer>();
		int last = -1;
		for(int i = 0; i < executedPath.getPathNodes().size(); i++) {
			Node<Integer> n = executedPath.getPathNodes().get(i);
			if(n == node)
				if(indexes.isEmpty())
					indexes.add(i);
				else
					last = i;					
		}
		indexes.add(last);
		return indexes;
	} 
}