package ui.constants;

public enum JavadocTagAnnotations {
	
	COVERAGE_CRITERIA("CoverageCriteria"), 
	TOUR_TYPE("TourType"),
	INFEASIBLE_PATH("InfeasiblePath"),
	ADDITIONAL_TEST_REQUIREMENT_PATH("AdditionalTestRequirementPath"),
	ADDITIONAL_TEST_PATH("AdditionalTestPath");
	
	
	private final String tag;
	
	JavadocTagAnnotations(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return "@" + tag;
	}
}