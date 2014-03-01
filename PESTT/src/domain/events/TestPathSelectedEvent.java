package domain.events;

import java.util.Set;

import adt.graph.Path;

public class TestPathSelectedEvent {

	public final Set<Path> selectedTestPaths;

	public TestPathSelectedEvent(Set<Path> selectedTestPathSet) {
		this.selectedTestPaths = selectedTestPathSet;
	}
}
