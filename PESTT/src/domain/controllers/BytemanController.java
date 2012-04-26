package domain.controllers;

import main.activator.Activator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

import domain.coverage.instrument.BytemanRunner;
import domain.coverage.instrument.Rules;
import domain.coverage.instrument.RulesFileCreator;

public class BytemanController {
	
	
	
	public String getExecutedPath() {
		try {
			RulesFileCreator file = new RulesFileCreator();
			Rules rules = new Rules();
			file.createRulesFile();
			String className = Activator.getDefault().getEditorController().getClassName();
			String methodName = Activator.getDefault().getEditorController().getSelectedMethod();
			ICompilationUnit unit = Activator.getDefault().getEditorController().getCompilationUnit();
			String classPath = unit.getJavaProject().getResource().getParent().getLocation().toOSString() + unit.getJavaProject().getOutputLocation().toOSString();
			file.writeRule(rules.createRuleForMethodEntry(className, methodName));
			file.writeRule(rules.createRuleForMethodExit(className, methodName));
			file.close();
			BytemanRunner runner = new BytemanRunner();
			runner.run(file.getFileLocation(), className, classPath);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
}
