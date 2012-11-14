package domain.dot.processor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.activator.Activator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

public class DotProcess implements IDotProcess {

	private PrintWriter stdin;
	private Scanner stdout;

	public Map<String, List<String>> dotToPlain(String dotsource) {
		Map<String, List<String>> elements = null;
		try {
			String dot = getDotLocation();
			String cmd = dot + " -Tplain"; // the Graphviz command.
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
	
	private String getDotLocation() {
			try {
				String str = new File(FileLocator.toFileURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/")).toURI()).getAbsolutePath() + IPath.SEPARATOR + "lib" + IPath.SEPARATOR + "dot" + IPath.SEPARATOR;
				String os = System.getProperty("os.name").substring(0, 3).toLowerCase();
				if(os.equals("lin"))
					str += "lin" + File.separator + "dot";
				else if(os.equals("mac")) 
					str += "mac" + IPath.SEPARATOR + "dot";
				else if(os.equals("win")) 
					str += "win" + IPath.SEPARATOR + "dot.exe";
				return str;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
}