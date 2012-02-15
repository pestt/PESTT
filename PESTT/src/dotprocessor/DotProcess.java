package dotprocessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DotProcess implements IDotProcess {

	private PrintWriter stdin;
	private Scanner stdout;

	public Map<String, ArrayList<String>> DotToPlain(String dotsource) {
		LinkedHashMap<String, ArrayList<String>> elements = null;
		String cmd = "dot -Tplain"; // the Graphviz command.
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