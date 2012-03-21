package domain.events;

import adt.graph.AbstractPath;

public class TestRequirementSelectedEvent {

	public final AbstractPath<Integer> selectedTestRequirement;

	public TestRequirementSelectedEvent(AbstractPath<Integer> selected) {
		this.selectedTestRequirement = selected;
	}

}
