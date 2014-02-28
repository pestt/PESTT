package domain.events;

import domain.constants.GraphCoverageCriteriaId;

public class TestRequirementSelectedCriteriaEvent {

	public final GraphCoverageCriteriaId selectedCoverageCriteria;

	public TestRequirementSelectedCriteriaEvent(
			GraphCoverageCriteriaId selectedCoverageCriteria) {
		this.selectedCoverageCriteria = selectedCoverageCriteria;
	}

}
