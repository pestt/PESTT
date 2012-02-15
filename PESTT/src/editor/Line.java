package editor;

public class Line {
	
	private int startLine;
	private int endLine;
	private int startPosition;
	private int endPosition;
	
	public Line(int startLine, int endLine, int startPosition, int endPosition) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	public int getStartLine() {
		return startLine;
	}
	
	public int getEndLine() {
		return endLine;
	}
	
	public int getStartPosition() {
		return startPosition;
	}
	
	public int getEndPosition() {
		return endPosition;
	}
}