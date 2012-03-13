package domain.events;

import java.util.Iterator;

import adt.graph.Path;

public class TestRequirementChangedEvent {
	
	public final Iterator<Path<Integer>> testRequirementSet;
	public final Iterator<Path<Integer>> infeasibles;
	public final boolean hasInfinitePath;
	
	public TestRequirementChangedEvent(Iterator<Path<Integer>> testRequirementSet, Iterator<Path<Integer>> infeasibles, boolean hasInfinitePath) {
		this.testRequirementSet = testRequirementSet;
		this.infeasibles = infeasibles;
		this.hasInfinitePath = hasInfinitePath;
	}
}
