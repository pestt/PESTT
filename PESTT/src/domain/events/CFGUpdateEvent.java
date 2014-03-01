package domain.events;

import adt.graph.Graph;

public class CFGUpdateEvent {

	public Graph sourceGraph;

	public CFGUpdateEvent(Graph sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

}
