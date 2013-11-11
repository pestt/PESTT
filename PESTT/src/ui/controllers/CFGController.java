package ui.controllers;

import java.util.Observable;

import ui.events.LayerChangeEvent;
import ui.events.LinkChangeEvent;
import ui.events.RefreshLogicGraphEvent;
import ui.events.RefreshStructuralGraphEvent;
import domain.constants.Layer;

public class CFGController extends Observable {

	private Layer layer;
	private boolean state;

	public void settLinkState(boolean state) {
		this.state = state;
		setChanged();
		notifyObservers(new LinkChangeEvent(state));
	}

	public boolean getLinkState() {
		return state;
	}

	public void selectLayer(String selected) {
		switch (Integer.parseInt(selected)) {
		case 1:
			layer = Layer.INSTRUCTIONS;
			break;
		case 2:
			layer = Layer.GUARDS;
			break;
		case 3:
			layer = Layer.GUARDS_TRUE;
			break;
		case 4:
			layer = Layer.GUARDS_FALSE;
			break;
		default:
			layer = Layer.EMPTY;
			;
		}
		setChanged();
		notifyObservers(new LayerChangeEvent(layer));
	}

	public Layer getLayer() {
		return layer;
	}

	public void refreshStructuralCoverageGraph() {
		setChanged();
		notifyObservers(new RefreshStructuralGraphEvent());
	}

	public void refreshLogicCoverageGraph() {
		setChanged();
		notifyObservers(new RefreshLogicGraphEvent());
	}
}
