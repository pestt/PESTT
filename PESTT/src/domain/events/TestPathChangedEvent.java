package domain.events;

import adt.graph.Path;

public class TestPathChangedEvent {

	public final Iterable<Path> testPathSet;
	public final Iterable<Path> manuallyAdded;

	public TestPathChangedEvent(Iterable<Path> testPathSet,
			Iterable<Path> manuallyAdded) {
		this.testPathSet = testPathSet;
		this.manuallyAdded = manuallyAdded;
	}
}
