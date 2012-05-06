package domain.coverage.instrument;

public class HelperContent {
	
	public String getContent(String packageName) {
		return 	"";
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
 
 */