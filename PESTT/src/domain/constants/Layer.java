package domain.constants;

public enum Layer {

	EMPTY(0), INSTRUCTIONS(1), GUARDS(2), GUARDS_TRUE(3), GUARDS_FALSE(4);

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
