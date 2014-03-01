package domain.events;

import adt.graph.AbstractPath;
import adt.graph.Path;

public class InfeasibleChangedEvent {

	public final Iterable<AbstractPath> testRequirementSet;
	public final Iterable<Path> manuallyAdded;
	public final Iterable<AbstractPath> infeasigles;
	public final boolean hasInfinitePath;

	public InfeasibleChangedEvent(
			Iterable<AbstractPath> testRequirementSet,
			Iterable<AbstractPath> infeasigles,
			Iterable<Path> manuallyAdded, boolean hasInfinitePath) {
		this.testRequirementSet = testRequirementSet;
		this.manuallyAdded = manuallyAdded;
		this.infeasigles = infeasigles;
		this.hasInfinitePath = hasInfinitePath;
	}
}
