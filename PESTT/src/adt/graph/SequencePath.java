package adt.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO rename
public class SequencePath<V extends Comparable<V>> extends AbstractPath<V> {

	/**
	 * The subpaths of this path.
	 */
	protected List<AbstractPath<V>> subPaths;

	/**
	 * Creates a new SequencePath.
	 */
	public SequencePath() {
		subPaths = new LinkedList<AbstractPath<V>>();
	}

	/**
	 * Adds a subpath.
	 * 
	 * @param subPath
	 */
	public void addSubPath(AbstractPath<V> subPath) {
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
	private String subPathToString(AbstractPath<V> path) {
		StringBuilder result = new StringBuilder();
		if (path instanceof SequencePath) {
			SequencePath<V> seq = (SequencePath<V>) path;
			Iterator<AbstractPath<V>> it = seq.subPaths.iterator();
			if (it.hasNext()) {
				AbstractPath<V> subPath = it.next();
				result.append(subPathToString(subPath));
				while (it.hasNext()) {
					subPath = it.next();
					result.append(", " + subPathToString(subPath));
				}
			}
		} else {
			Iterator<Node<V>> itNodes = path.iterator();
			result.append(itNodes.next());
			while (itNodes.hasNext())
				result.append(", " + itNodes.next());
		}
		if (path instanceof CyclePath || path instanceof InfinitePath)
			result.append(", ..., " + path.from());
		return result.toString();
	}

	@Override
	public boolean toursWithSideTrip(AbstractPath<V> path) {
		return false;
	}

	@Override
	public boolean toursWithDetour(AbstractPath<V> path) {
		return false;
	}

	@Override
	public Iterator<Node<V>> iterator() {
		List<Node<V>> result = new LinkedList<Node<V>>();
		for (AbstractPath<V> path : subPaths)
			for (Node<V> node : path)
				result.add(node);
		return result.iterator();
	}

	@Override
	public boolean isSubPath(AbstractPath<V> path) {
		return false;
	}
}