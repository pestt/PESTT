package domain.coverage.instrument;

public class HelperClass {
	
	public String create(String pckg, String output) {
		return 	"package " + pckg + ";\n\n" +
				"import java.io.BufferedWriter;\n" +
				"import java.io.FileWriter;\n" +
				"import java.io.IOException;\n" +
				"import java.io.PrintWriter;\n" + 
				"import org.jboss.byteman.rule.Rule;\n" +
				"import org.jboss.byteman.rule.helper.Helper;\n\n" +
				"public class PESTTHelper extends Helper {\n\n" +
				"\tprotected PESTTHelper(Rule rule) {\n" +
				"\t\tsuper(rule);\n" +
				"\t}\n\n" +
				"\tpublic boolean debug(String message) {\n" +
				"\t\ttry {\n" +
				"\t\t\tPrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(\"" + output + "\", true)));\n" +
				"\t\t\tout.println(message);\n" +
				"\t\t\tout.close();\n" +
				"\t\t} catch (IOException e) {\n" +
				"\t\t\te.printStackTrace();\n" +
				"\t\t}\n" +
				"\t\treturn false;\n" +
				"\t}\n" +
				"}";
	}
}



/*
  
 package my.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

public class PESTTHelper extends Helper {

	protected PESTTHelper(Rule rule) {
		super(rule);
	}

	public boolean debug(String message) {
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/rui/runtime-EclipseApplication/TestProject/script/output.txt", true)));
		    out.println(message);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}


public boolean someMethod() { 
        try { 
            throw new Exception(); 
        } catch (Exception e) { 
            return containsStackFrame(e, 
"org.eclipse.jdt.internal.junit.runner.RemoteTestRunner"); 
        } 
        return false; 
    } 




 
 */