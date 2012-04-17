package domain.coverage.instrument;

public class Rules {
	
	public String createRuleForMethodEntry(String className, String methodName) {
		return "RULE trace main entry\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT ENTRY\nIF true\nDO traceln(\"entering main\")\nENDRULE\n\n";
	}
	
	public String createRuleForMethodExit(String className, String methodName) {
		return "RULE trace main exit\nCLASS " + className + "\nMETHOD " + methodName + "\n" +
			   "AT EXIT\nIF true\nDO traceln(\"exiting main\")\nENDRULE\n\n";
	}

}
