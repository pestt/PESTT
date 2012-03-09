package domain.events;

import java.util.Iterator;

import adt.graph.Path;

public class TestPathChangedEvent {
	
	public final Iterator<Path<Integer>> testPathSet;
	
	public TestPathChangedEvent(Iterator<Path<Integer>> testPathSet) {
		this.testPathSet = testPathSet;
	}
}
