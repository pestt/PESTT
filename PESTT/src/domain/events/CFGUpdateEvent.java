package domain.events;

import adt.graph.Graph;

public class CFGUpdateEvent {

	public Graph<Integer> sourceGraph;

	public CFGUpdateEvent(Graph<Integer> sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

}
