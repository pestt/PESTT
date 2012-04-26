package domain.coverage.instrument;

public class Rules {
	
	public String createRuleForMethodEntry(String className, String methodName) {
		return "RULE trace " + methodName + " entry\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT ENTRY\nIF true\nDO traceln(\"entering " + methodName + "\")\nENDRULE\n\n";
	}
	
	public String createRuleForMethodExit(String className, String methodName) {
		return "RULE trace " + methodName + " exit\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT EXIT\nIF true\nDO traceln(\"exiting " + methodName + "\")\nENDRULE\n\n";
	}

}
