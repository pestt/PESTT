package domain.tests.execution.instrument;

import domain.constants.Byteman;

public class Rules {

	public String createRuleForMethodEntry(String helper, String mthd,
			String cls) {
		return "RULE " + mthd + " entry\n" + "CLASS " + cls + "\n" + "METHOD "
				+ mthd + "\n" + "HELPER " + helper + "\n" + "AT ENTRY\n"
				+ "IF true\n" + "DO debug(\"" + Byteman.ENTERING_METHOD + mthd
				+ "\")\n" + "ENDRULE\n\n";
	}

	public String createRuleForMethodExit(String helper, String mthd, String cls) {
		return "RULE " + mthd + " exit\n" + "CLASS " + cls + "\n" + "METHOD "
				+ mthd + "\n" + "HELPER " + helper + "\n" + "AT EXIT\n"
				+ "IF true\n" + "DO debug(\"" + Byteman.EXITING_METHOD + mthd
				+ "\")\n" + "ENDRULE\n\n";
	}

	public String createRuleForLine(String helper, String mthd, String cls,
			String edges, int line) {
		return "RULE " + mthd + " Line" + line + "\n" + "CLASS " + cls + "\n"
				+ "METHOD " + mthd + "\n" + "HELPER " + helper + "\n"
				+ "AT LINE " + line + "\n" + "IF true\n" + "DO debug(\""
				+ edges + "\")\n" + "ENDRULE\n\n";
	}
}
