package domain.dot.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.activator.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

import ui.constants.Preferences;

public class DotProcess implements IDotProcess {

	private PrintWriter stdin;
	private Scanner stdout;

	public Map<String, List<String>> dotToPlain(String dotsource) {
		Map<String, List<String>> elements = null;
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String dot = preferenceStore.getString(Preferences.DOT_PATH);
		String cmd = dot + " -Tplain"; // the Graphviz command.
		try {
			Process p = Runtime.getRuntime().exec(cmd, null, null); // run the Graphviz command.
			stdin = new PrintWriter(p.getOutputStream()); // pass the dot string.
			stdin.write(dotsource);
			stdin.flush();
			stdout = new Scanner(p.getInputStream()); // gets the plain graph.
			Parser parser = new Parser();
			elements = parser.parsePlainInfo(stdout); // parse the plain graph information.
			close(); // close the input and output streams.
		} catch (IOException e) {
			e.printStackTrace();
		}
		return elements;
	}

	private void close() {
		stdin.close();
		stdout.close();
	}
}