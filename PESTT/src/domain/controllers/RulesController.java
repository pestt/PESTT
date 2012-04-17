package domain.controllers;

import main.activator.Activator;
import domain.coverage.instrument.Rules;
import domain.coverage.instrument.RulesFileCreator;

public class RulesController {
	
	public String getExecutedPath() {
		RulesFileCreator file = new RulesFileCreator();
		Rules rules = new Rules();
		file.createRulesFile();
		String className = Activator.getDefault().getEditorController().getClassName();
		String methodName = Activator.getDefault().getEditorController().getSelectedMethod();
		
		file.writeRule(rules.createRuleForMethodEntry(className, methodName));
		file.writeRule(rules.createRuleForMethodExit(className, methodName));
		file.close();
		return "";
	}

}
