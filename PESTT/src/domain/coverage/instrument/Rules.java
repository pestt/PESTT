package domain.coverage.instrument;

import ui.constants.BytemanLog;

public class Rules {
	
	public String createRuleForMethodEntry(String helper, String mthd, String cls) {
		return  "RULE trace " + mthd + " entry\n" + 
				"CLASS " + cls + "\n" + 
				"METHOD " + mthd + "\n" +
				"HELPER " + helper + "\n" +
				"AT ENTRY\n" +
				"IF true\n" +
				"DO debug(\"" + BytemanLog.ENTERING_METHOD + mthd + "\")\n" + 
				"ENDRULE\n\n";
	}
	
	public String createRuleForMethodExit(String helper, String mthd, String cls) {
		return 	"RULE trace " + mthd + " exit\n" + 
				"CLASS " + cls + "\n" + 
				"METHOD " + mthd + "\n" +
				"HELPER " + helper + "\n" +
				"AT EXIT\n" +
				"IF true\n" +
				"DO debug(\"" + BytemanLog.EXITING_METHOD + mthd  + "\")\n" + 
				"ENDRULE\n\n";
	}

	public String createRuleForLine(String helper, String mthd, String cls, String edges, int line) {
		return 	"RULE trace " + mthd + " Line" + line + "\n" + 
				"CLASS " + cls + "\n" + 
				"METHOD " + mthd + "\n" +
				"HELPER " + helper + "\n" +
				"AT LINE " + line + "\n" + 
				"IF true\n" +
				"DO debug(\"" + edges + "\")\n" +
				"ENDRULE\n\n";
	}	
}
