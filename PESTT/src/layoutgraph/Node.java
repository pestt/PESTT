package layoutgraph;

import java.util.List;

import org.eclipse.swt.graphics.Color;

import constants.Colors_ID;

public class Node {
	
	private String name = null;
	private double xPos = 0.0;
	private double yPos = 0.0;
	private Color bgcolor;
	private Color fgcolor;
	
	public Node(List<String> attributes) {
		name = attributes.get(0);
		xPos = Double.parseDouble(attributes.get(1));
		yPos = Double.parseDouble(attributes.get(2));
		setBackgroundColor(attributes.get(3));
	}
	
	public String getName() {
		return name;
	}
	
	public double getXPosition() {
		return xPos;
	}
	
	public double getYPosition() {
		return yPos;
	}
	
	private void setBackgroundColor(String color) {
		if(color.equals(Colors_ID.GRENN_ID)) {
			bgcolor = Colors_ID.GREEN;
			setForegroundColor(Colors_ID.WHITE);
		} else if(color.equals(Colors_ID.RED_ID)) {
			bgcolor = Colors_ID.RED;
			setForegroundColor(Colors_ID.WHITE);
		} else if(color.equals(Colors_ID.VIOLET_ID)) {
			bgcolor = Colors_ID.VIOLET;
			setForegroundColor(Colors_ID.WHITE);
		} else
			setForegroundColor(Colors_ID.BLACK);
	}
	
	public Color getBackgroundColor() {
		return bgcolor;
	}
	
	private void setForegroundColor(Color color) {
		fgcolor = color;
	}
	
	public Color getForegroundColor() {
		return fgcolor;
	}

}
