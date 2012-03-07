package domain;

import domain.constants.Layer;

public class LayerChangeEvent {
	
	public final Layer layer;

	public LayerChangeEvent(Layer layer) {
		this.layer = layer;
	}

}
