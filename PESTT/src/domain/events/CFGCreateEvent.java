package domain.events;

import adt.graph.Graph;

public class CFGCreateEvent {

	public final Graph sourceGraph;

	public CFGCreateEvent(Graph sourceGraph) {
		this.sourceGraph = sourceGraph;
	}
}
