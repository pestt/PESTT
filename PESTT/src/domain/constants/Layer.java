package domain.constants;

public enum Layer {
	
	EMPTY(0),
	GUARDS(1),
	INSTRUCTIONS(2);
	
	private final int layer;
	
	Layer(int layer) {
		this.layer = layer;
	}
	
	public int getLayer() {
		return layer;
	}
	
	@Override
	public String toString() {
		return Integer.toString(layer);
	}
}
