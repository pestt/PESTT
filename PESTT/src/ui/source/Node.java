package ui.source;

import java.util.List;

import org.eclipse.swt.graphics.Color;

import domain.constants.Colors;

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
		if(color.equals(Colors.GRENN_ID)) {
			bgcolor = Colors.GREEN;
			setForegroundColor(Colors.WHITE);
		} else if(color.equals(Colors.RED_ID)) {
			bgcolor = Colors.RED;
			setForegroundColor(Colors.WHITE);
		} else if(color.equals(Colors.VIOLET_ID)) {
			bgcolor = Colors.VIOLET;
			setForegroundColor(Colors.WHITE);
		} else
			setForegroundColor(Colors.BLACK);
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
