package domain.dot.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import domain.constants.Description;

public class Parser {

	public Map<String, List<String>> parsePlainInfo(Scanner input) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		String line = input.nextLine();
		int nodeCount = 1;
		int edgeCount = 1;
		StringTokenizer tok = null;
		String type = null;
		while(!line.equals(Description.STOP) && input.hasNext()) {
			List<String> values;
			tok = new StringTokenizer(line);
			type = tok.nextToken();
			if(type.equals(Description.GRAPH)) {
				values = new LinkedList<String>();
				tok.nextToken(); // discard the graph scale.
				values.add(tok.nextToken()); // the graph width.
				values.add(tok.nextToken()); // the graph height.
				map.put(Description.GRAPH, values);
			} else if(type.equals(Description.NODE)) {
				values = new ArrayList<String>();
				values.add(tok.nextToken()); // the node value.
				values.add(tok.nextToken()); // the node x position.
				values.add(tok.nextToken()); // the node y position.
				for(int x = 0;x < 6;x++)
					tok.nextToken(); // discard the remain values until the fillcolor.   
				values.add(tok.nextToken()); // the node fillcolor.
				map.put(Description.NODE + nodeCount++, values);
			} else if(type.equals(Description.EDGE)) {
				values = new ArrayList<String>();
				values.add(tok.nextToken()); // the edge tail.
				values.add(tok.nextToken()); // the edge head.
				int coordenates = Integer.parseInt(tok.nextToken()) * 2 + 1;
				for(int x = 0;x < coordenates;x++)
					tok.nextToken(); // discard the remain values until the fillcolor. 
				values.add(tok.nextToken()); // the edge color.
				map.put(Description.EDGE + edgeCount++, values);
			}
			line = input.nextLine();
		}
		return map;
	}

}