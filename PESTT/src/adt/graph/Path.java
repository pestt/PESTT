package adt.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Path extends AbstractPath {

	/**
	 * The nodes of this path.
	 */
	@XmlElementWrapper(name = "nodes")
	@XmlElement(name = "node")
	private List<Node> nodes;

	// for serialization purposes
	protected Path() {
	}
	
	/**
	 * Creates a new path from the given collection of nodes.
	 * 
	 * @param nodes
	 * @requires nodes.size() > 0
	 */
	public Path(Collection<Node> nodes) {
		this.nodes = new LinkedList<Node>(nodes);
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		Iterator<Node> it = iterator();
		s.append(it.next()); // a path has always one node, at least!
		while (it.hasNext())
			s.append(", " + it.next());
		s.append("]");
		return s.toString();
	}

	@Override
	public boolean isSubPath(AbstractPath other) {
		if (other instanceof InfinitePath)
			return false;
		Path path = (Path) other;
		int i = 0;
		for (Iterator<Node> it = nodes.iterator(); it.hasNext(); i++, it
				.next())
			if (isConsecutive(i, path))
				return true;
		return false;
	}

	/**
	 * TODO document
	 * 
	 * @param i
	 * @param path
	 * @return True if this path is consecutive.
	 */
	private boolean isConsecutive(int i, Path path) {
		Iterator<Node> it = path.iterator();
		int size = path.nodes.size();
		if (nodes.size() >= size) {
			while (i < nodes.size() && it.hasNext()) {
				if (nodes.get(i) != it.next())
					return false;
				i++;
			}
			return it.hasNext() ? false : true;
		}
		return false;
	}

	@Override
	public boolean toursWithSideTrip(AbstractPath other) {
		if (other instanceof InfinitePath)
			return false;
		Path path = (Path) other;
		int i = 0;
		for (Iterator<Node> it = nodes.iterator(); it.hasNext(); i++, it
				.next())
			if (isConsecutiveSideTrip(i, path))
				return true;
		return false;
	}

	/**
	 * TODO Verifies if the current path tours another with a consecutive side
	 * trip.
	 * 
	 * @param i
	 * @param path
	 * @return
	 */
	private boolean isConsecutiveSideTrip(int i, Path path) {
		Iterator<Node> it = path.iterator();
		Node currentNode = null;
		while (i < nodes.size() && it.hasNext()) {
			Node node = it.next();
			if (nodes.get(i) != node) {
				// try advance loop

				while (i < nodes.size() && nodes.get(i) != currentNode)
					i++;

				if (i < nodes.size() && nodes.get(i) == node)
					return true;

				if ((i < nodes.size() && nodes.get(i) != currentNode)
						|| i >= nodes.size())
					return false;

				if (i + 1 < nodes.size() && nodes.get(i + 1) == node)
					i++;
			}
			currentNode = nodes.get(i);
			i++;
		}
		return true;
	}

	@Override
	public boolean toursWithDetour(AbstractPath other) {
		if (other instanceof InfinitePath)
			return false;
		Path path = (Path) other;
		int index = 0;
		for (Node node : path) {
			while (index < nodes.size() && nodes.get(index) != node)
				index++;
			if (index == nodes.size())
				return false;
		}
		return true;
	}
}
