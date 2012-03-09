package domain.events;

import java.util.Iterator;

import adt.graph.Path;

public class TestRequirementChangedEvent {
	
	public final Iterator<Path<Integer>> testRequirementSet;
	public final boolean hasInfinitePath;
	
	public TestRequirementChangedEvent(Iterator<Path<Integer>> testRequirementSet, boolean hasInfinitePath) {
		this.testRequirementSet = testRequirementSet;
		this.hasInfinitePath = hasInfinitePath;
	}
}
