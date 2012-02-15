package dotprocessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import constants.Description_ID;

public class Parser {

	public LinkedHashMap<String, ArrayList<String>> parsePlainInfo(Scanner input) {
		LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
		String line = input.nextLine();
		int nodeCount = 1;
		int edgeCount = 1;
		StringTokenizer tok = null;
		String type = null;
		while(!line.equals(Description_ID.STOP) && input.hasNext()) {
			ArrayList<String> values;
			tok = new StringTokenizer(line);
			type = tok.nextToken();
			if(type.equals(Description_ID.GRAPH)) {
				values = new ArrayList<String>();
				tok.nextToken(); // discard the graph scale.
				values.add(tok.nextToken()); // the graph width.
				values.add(tok.nextToken()); // the graph height.
				map.put(Description_ID.GRAPH, values);
			} else if(type.equals(Description_ID.NODE)) {
				values = new ArrayList<String>();
				values.add(tok.nextToken()); // the node value.
				values.add(tok.nextToken()); // the node x position.
				values.add(tok.nextToken()); // the node y position.
				for(int x = 0;x < 6;x++)
					tok.nextToken(); // discard the remain values until the fillcolor.   
				values.add(tok.nextToken()); // the node fillcolor.
				map.put(Description_ID.NODE + nodeCount++, values);
			} else if(type.equals(Description_ID.EDGE)) {
				values = new ArrayList<String>();
				values.add(tok.nextToken()); // the edge tail.
				values.add(tok.nextToken()); // the edge head.
				int coordenates = Integer.parseInt(tok.nextToken()) * 2 + 1;
				for(int x = 0;x < coordenates;x++)
					tok.nextToken(); // discard the remain values until the fillcolor. 
				values.add(tok.nextToken()); // the edge color.
				map.put(Description_ID.EDGE + edgeCount++, values);
			}
			line = input.nextLine();
		}
		return map;
	}

}