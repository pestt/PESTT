package domain.constants;

public enum DefUsesView {

	NODE_EDGE("NODEEDGE"), VARIABLE("VARIABLE");

	private final String view;

	DefUsesView(String view) {
		this.view = view;
	}

	public String getTour() {
		return view;
	}

}
