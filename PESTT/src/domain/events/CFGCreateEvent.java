package domain.events;

import adt.graph.Graph;

public class CFGCreateEvent {

	public final Graph<Integer> sourceGraph;

	public CFGCreateEvent(Graph<Integer> sourceGraph) {
		this.sourceGraph = sourceGraph;
	}
}
