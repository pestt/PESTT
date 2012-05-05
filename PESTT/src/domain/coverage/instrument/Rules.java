package domain.coverage.instrument;

public class Rules {
	
	public String createRuleForMethodEntry(String className, String methodName) {
		return "RULE trace " + methodName + " entry\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT ENTRY\nIF true\nDO traceln(\"entering\")\nENDRULE\n\n";
	}
	
	public String createRuleForMethodExit(String className, String methodName) {
		return "RULE trace " + methodName + " exit\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT EXIT\nIF true\nDO traceln(\"exiting\")\nENDRULE\n\n";
	}

	public String createRuleForLine(String className, String methodName, Integer line) {
		return "RULE trace " + methodName + " Line" + line + "\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT LINE " + line + "\nIF true\nDO traceln(\"passed in line " + line + "\")\nENDRULE\n\n";
		}

}
