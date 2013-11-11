package domain.tests.execution.instrument;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

public class HelperClass extends Helper {

	protected HelperClass(Rule rule) {
		super(rule);
	}

	public boolean debug(String message) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					System.getProperty("java.io.tmpdir") + IPath.SEPARATOR
							+ "script" + IPath.SEPARATOR + "output.txt", true));
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}