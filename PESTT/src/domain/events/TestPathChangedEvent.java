package domain.events;

import adt.graph.Path;

public class TestPathChangedEvent {

	public final Iterable<Path<Integer>> testPathSet;
	public final Iterable<Path<Integer>> manuallyAdded;

	public TestPathChangedEvent(Iterable<Path<Integer>> testPathSet,
			Iterable<Path<Integer>> manuallyAdded) {
		this.testPathSet = testPathSet;
		this.manuallyAdded = manuallyAdded;
	}
}
