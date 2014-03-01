package adt.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO rename
public class SequencePath extends AbstractPath {

	/**
	 * The subpaths of this path.
	 */
	protected List<AbstractPath> subPaths;

	/**
	 * Creates a new SequencePath.
	 */
	public SequencePath() {
		subPaths = new LinkedList<AbstractPath>();
	}

	/**
	 * Adds a subpath.
	 * 
	 * @param subPath
	 */
	public void addSubPath(AbstractPath subPath) {
		subPaths.add(subPath);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		s.append(subPathToString(this));
		s.append("]");
		return s.toString();
	}

	/**
	 * Converts a subpath to String.
	 * 
	 * @param path
	 * @return
	 */
	private String subPathToString(AbstractPath path) {
		StringBuilder result = new StringBuilder();
		if (path instanceof SequencePath) {
			SequencePath seq = (SequencePath) path;
			Iterator<AbstractPath> it = seq.subPaths.iterator();
			if (it.hasNext()) {
				AbstractPath subPath = it.next();
				result.append(subPathToString(subPath));
				while (it.hasNext()) {
					subPath = it.next();
					result.append(", " + subPathToString(subPath));
				}
			}
		} else {
			Iterator<Node> itNodes = path.iterator();
			result.append(itNodes.next());
			while (itNodes.hasNext())
				result.append(", " + itNodes.next());
		}
		if (path instanceof CyclePath || path instanceof InfinitePath)
			result.append(", ..., " + path.from());
		return result.toString();
	}

	@Override
	public boolean toursWithSideTrip(AbstractPath path) {
		return false;
	}

	@Override
	public boolean toursWithDetour(AbstractPath path) {
		return false;
	}

	@Override
	public Iterator<Node> iterator() {
		List<Node> result = new LinkedList<Node>();
		for (AbstractPath path : subPaths)
			for (Node node : path)
				result.add(node);
		return result.iterator();
	}

	@Override
	public boolean isSubPath(AbstractPath path) {
		return false;
	}
}