package ui.source;

import java.util.List;

import org.eclipse.swt.graphics.Color;

public class Edge {

	private String start = null;
	private String end = null;
	private Color color;
	
	public Edge(List<String> attributes) {
		start = attributes.get(0);
		end = attributes.get(1);
		setColor(attributes.get(2));
	}
	
	public String getBeginNode() {
		return start;
	}
	
	public String getEndNode() {
		return end;
	}
	
	private void setColor(String color) {
		if(color.equals("blue"))
			this.color = new Color(null, 0, 0, 255);
		else
			this.color = new Color(null, 192, 192, 192);
	}
	
	public Color getColor() {
		return color;
	}
}