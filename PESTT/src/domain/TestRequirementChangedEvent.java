package domain;

public class TestRequirementChangedEvent {
	
	public final Object testRequirementSet;
	public final boolean hasInfinitePath;
	
	public TestRequirementChangedEvent(Object testRequirementSet, boolean hasInfinitePath) {
		this.testRequirementSet = testRequirementSet;
		this.hasInfinitePath = hasInfinitePath;
	}
}
