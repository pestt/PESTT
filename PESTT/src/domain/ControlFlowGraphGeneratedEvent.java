package domain;

import adt.graph.Graph;

public class ControlFlowGraphGeneratedEvent {

	public final Graph<Integer> sourceGraph;

	public ControlFlowGraphGeneratedEvent(Graph<Integer> sourceGraph) {
		this.sourceGraph = sourceGraph;
	}
}
