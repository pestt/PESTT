package domain.events;

import adt.graph.Path;

public class TestRequirementSelectedEvent {

	public final Path<Integer> selectedTestRequirement;

	public TestRequirementSelectedEvent(Path<Integer> selectedTestRequirement) {
		this.selectedTestRequirement = selectedTestRequirement;
	}

}
