package domain.coverage.instrument;

public class Rules {
	
	public String createRuleForMethodEntry(String className, String methodName, String helperClass) {
		return  "RULE trace " + methodName + " entry\n" + 
				"CLASS " + className + "\n" + 
				"METHOD " + methodName + "\n" +
				"HELPER " + helperClass + "\n" +
				"AT ENTRY\nIF true\nDO debug(\"entering\")\n" + 
				"ENDRULE\n\n";
	}
	
	public String createRuleForMethodExit(String className, String methodName, String helperClass) {
		return 	"RULE trace " + methodName + " exit\n" + 
				"CLASS " + className + "\n" + 
				"METHOD " + methodName + "\n" +
				"HELPER " + helperClass + "\n" +
				"AT EXIT\nIF true\nDO debug(\"exiting\")\n" + 
				"ENDRULE\n\n";
	}

	public String createRuleForLine(String className, String methodName, Integer line, String helperClass) {
		return 	"RULE trace " + methodName + " Line" + line + "\n" + 
				"CLASS " + className + "\n" + 
				"METHOD " + methodName + "\n" +
				"HELPER " + helperClass + "\n" +
				"AT LINE " + line + "\n" + 
				"IF true\nDO debug(\"passed in line " + line + "\")\n" +
				"ENDRULE\n\n";
	}	
}
