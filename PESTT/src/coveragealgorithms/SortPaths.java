package coveragealgorithms;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sourcegraph.Path;

public class SortPaths<V> {

	private static final int UNKNOW = -1; // flag to control the position.

	public List<Path<V>> sort(List<Path<V>> paths) {
		// check for empty or null array
		if(paths == null || paths.isEmpty())
			return null;
		return sortPaths(paths); // returns the list of paths sorted.
	}

	private List<Path<V>> sortPaths(List<Path<V>> paths) {
		int length = 1; // the minimum length of a path.
		List<Path<V>> aux = new LinkedList<Path<V>>(); // auxiliary list.
		List<Integer> indexes = new LinkedList<Integer>(); // index to be removed.
		while(!paths.isEmpty()) { // while the original list is not empty.
			indexes.clear(); // clean the list with the indexes to be remove. 
			for(Path<V> path : paths) // through all paths.
				if(path.getPathNodes().size() == length) { // if the path length is equal to length.
					if(aux.isEmpty())
						aux.add(path); // add the path directly.
					else
						insertPaths(aux, path); // add the path in the list.
					indexes.add(paths.indexOf(path)); // add index to path to be removed.
				}
			removePaths(indexes, paths); // remove paths from the original list.
			length++; // update the length.
		}
		paths = aux; // copy the auxiliary list to the original.
		return paths;
	}
	
	private void insertPaths(List<Path<V>> list, Path<V> path) {
		int pos = findPlace(list, path); // find the position in the list to insert the path.
		if(pos != UNKNOW) // if the position have a specific position.
			list.add(pos, path); // insert in that position.
		else 
			list.add(path); // insert in the last position.
	}

	private int findPlace(List<Path<V>> list, Path<V> path) {
		int pos = UNKNOW; // the position in the list.
		int pSize = UNKNOW; // the size of the current path in the list. 
		int pathSize = UNKNOW; // the size of the path to be added to the list.
		int pValue = UNKNOW; // the value of the last node in the current path in the list.
		int pathValue = UNKNOW; // the value of the last node in the path to be added to the list. 
		boolean flag = false; // control flag to check the start position to find the current place.
		for(int i = 0; i < list.size(); i++) { // through the list of paths.
			Path<V> p = list.get(i); // the current path in the list.
			pathSize = path.getPathNodes().size(); // update the size of the path to be added. 
			pSize = p.getPathNodes().size(); // update the size of the current path in the list. 
			if(pathSize > 1 && !flag) { // only enters if the flag is false and the size of the path to be added to the list is greater than one.  
				flag = true; // only need to find the subpath of the path to be added to the list one time.
				// verifies the position of the subpath of the path to be added to the list.
				do{
					p = list.get(i);
					i++;
				} while((i < list.size()) && (!path.getPathNodes().subList(0, pathSize - 1).equals(p.getPathNodes())));
					
				if(i == list.size()) {
					int z = 0;
					pathValue = Integer.parseInt(path.getPathNodes().get(0).getValue().toString()); // the value of the first node in the path to be added to the list. 
					while(z < list.size()) {
						p = list.get(z);
						pSize = p.getPathNodes().size();
						pValue = Integer.parseInt(p.getPathNodes().get(0).getValue().toString()); // the value of the first node in the current path in the list. 
						z++;
						if(pValue > pathValue) {
							i = list.indexOf(p);
							return i;
						} else if(pValue == pathValue) {
							int min = Math.min(pSize, pathSize);
							for(int x = 1; x < min; x++)
								if(Integer.parseInt(path.getPathNodes().get(x).getValue().toString()) < Integer.parseInt(p.getPathNodes().get(x).getValue().toString())) {
									i = list.indexOf(p);
									return i;
								} else if(Integer.parseInt(path.getPathNodes().get(x).getValue().toString()) > Integer.parseInt(p.getPathNodes().get(x).getValue().toString()))
									break;
						}
					}
					if(i == list.size()) 
						break;
						
				}
				
				// if exists duplicated paths.
				while(path.getPathNodes().subList(0, pathSize - 1).equals(p.getPathNodes())) {
					p = list.get(i);
					i++;
				}
				
				
				p = list.get(--i); // get the next path in the list.
				pSize = p.getPathNodes().size(); // update the size of the current path in the list.
			}
			if(pSize == pathSize) { // if the size of the current path in the list and the path to be added are equals.
				pValue = Integer.parseInt(p.getPathNodes().get(pSize - 1).getValue().toString()); // the value of the last node in the current path in the list. 
				pathValue = Integer.parseInt(path.getPathNodes().get(pathSize - 1).getValue().toString()); // the value of the last node in the path to be added to the list. 
				if(pValue > pathValue) { 
					pos = list.indexOf(p); // get the position of the current path in the list
					break;
				}
			} else if((list.indexOf(p) != list.size() - 1) || (!path.getPathNodes().subList(0, pathSize - 1).equals(p.getPathNodes()))) { // if is not the last position in the list and if the current path in the list is not subpath of the path to be added. 
				pos = list.indexOf(p); // get the position of the current path in the list
				break;
			}
		}
		return pos;
	}
	
	private void removePaths(List<Integer> indexes, List<Path<V>> paths) {
		Collections.reverse(indexes); // reverse the list.
		for(int i : indexes) 
			paths.remove(i); // remove the indexes.
	}
}