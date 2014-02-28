package domain.events;

import java.util.Set;

import adt.graph.Path;

public class TestPathSelectedEvent {

	public final Set<Path<Integer>> selectedTestPaths;

	public TestPathSelectedEvent(Set<Path<Integer>> selectedTestPathSet) {
		this.selectedTestPaths = selectedTestPathSet;
	}
}
