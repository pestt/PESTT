package domain.constants;

public enum JavadocTagAnnotations {
	
	COVERAGE_CRITERIA("CoverageCriteria"), 
	INFEASIBLE_PATH("InfeasiblePath"),
	ADICIONAL_TEST_REQUIREMENT_PATH("AdicionalTestRequirementPath"),
	ADICIONAL_TEST_PATH("AdicionaltestPath");
	
	
	private final String tag;
	
	JavadocTagAnnotations(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return "@" + tag;
	}
}