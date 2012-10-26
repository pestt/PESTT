package domain.dot.processor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
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
	private String[] archx86 =  {"x86", "i386", "i486", "i586", "i686"};
	private String[] archx64 = {"amd64", "em64t", "x86_64"};
	private List<String> x86 = Arrays.asList(archx86);  
	private List<String> x64 = Arrays.asList(archx64);  

	public Map<String, List<String>> dotToPlain(String dotsource) {
		Map<String, List<String>> elements = null;
		try {
			String dot = new File(FileLocator.toFileURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/")).toURI()).getAbsolutePath() + IPath.SEPARATOR + "lib" + IPath.SEPARATOR + "dot" + IPath.SEPARATOR;
			String dotLocation = getDotLocation();
			dot += dotLocation;
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
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return elements;
	}

	private void close() {
		stdin.close();
		stdout.close();
	}
	
	private String getDotLocation() {
		String str = "";
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		os = os.substring(0, 3).toLowerCase();
		if(os.equals("lin") && x86.contains(arch))
			str = "lin" + IPath.SEPARATOR + "x86" + IPath.SEPARATOR + "dot";
		else if(os.equals("lin") && x64.contains(arch))
			str = "lin" + IPath.SEPARATOR + "x64" + IPath.SEPARATOR + "dot";
		else if(os.equals("mac") && x86.contains(arch))
			str = "mac" + IPath.SEPARATOR + "x86" + IPath.SEPARATOR + "dot";
		else if(os.equals("mac") && x64.contains(arch))
			str = "mac" + IPath.SEPARATOR + "x64" + IPath.SEPARATOR + "dot";
		else if(os.equals("win") && x86.contains(arch))
			str = "win" + IPath.SEPARATOR + "x86" + IPath.SEPARATOR + "dot.exe";
		else if(os.equals("win") && x64.contains(arch))
			str = "win" + IPath.SEPARATOR + "x64" + IPath.SEPARATOR + "dot.exe";
		return str;
	}
}