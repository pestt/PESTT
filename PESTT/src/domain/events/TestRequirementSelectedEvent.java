package domain.events;

import adt.graph.AbstractPath;

public class TestRequirementSelectedEvent {

	public final AbstractPath selectedTestRequirement;

	public TestRequirementSelectedEvent(AbstractPath selected) {
		this.selectedTestRequirement = selected;
	}

}
