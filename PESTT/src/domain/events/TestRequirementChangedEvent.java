package domain.events;

import adt.graph.Path;

public class TestRequirementChangedEvent {
	
	public final Iterable<Path<Integer>> testRequirementSet;
	public final Iterable<Path<Integer>> manuallyAdded;
	public final Iterable<Path<Integer>> infeasigles;
	public final boolean hasInfinitePath;
	
	public TestRequirementChangedEvent(Iterable<Path<Integer>> testRequirementSet, Iterable<Path<Integer>> infeasigles, Iterable<Path<Integer>> manuallyAdded, boolean hasInfinitePath) {
		this.testRequirementSet = testRequirementSet;
		this.manuallyAdded = manuallyAdded;
		this.infeasigles = infeasigles;
		this.hasInfinitePath = hasInfinitePath;
	}
}
