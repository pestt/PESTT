package domain.events;

import adt.graph.Path;

public class AutomaticTestPathChangedEvent {
	
	public final Iterable<Path<Integer>> testPathSet;
	public final Iterable<Path<Integer>> manuallyAdded;
	
	public AutomaticTestPathChangedEvent(Iterable<Path<Integer>> testPathSet, Iterable<Path<Integer>> manuallyAdded) {
		this.testPathSet = testPathSet;
		this.manuallyAdded = manuallyAdded;
	}

}
