package domain.coverage.instrument;

public class HelperClass {
	
	public String create(String pckg, String output) {
		return 	"package " + pckg + ";\n\n" +
				"import java.io.BufferedWriter;\n" +
				"import java.io.FileWriter;\n" +
				"import java.io.IOException;\n" +
				"import org.jboss.byteman.rule.Rule;\n" +
				"import org.jboss.byteman.rule.helper.Helper;\n\n" +
				"public class PESTTHelper extends Helper {\n\n" +
				"\tprotected PESTTHelper(Rule rule) {\n" +
				"\t\tsuper(rule);\n" +
				"\t}\n\n" +
				"\tpublic boolean debug(String message) {\n" +
				"\t\ttry {\n" +
				"\t\t\tBufferedWriter writer = new BufferedWriter(new FileWriter(\"" + output + "\", true));\n" +
				"\t\t\twriter.write(message);\n" +
				"\t\t\twriter.newLine();\n" +
				"\t\t\twriter.flush();\n" +
				"\t\t\twriter.close();\n" +
				"\t\t} catch (IOException e) {\n" +
				"\t\t\te.printStackTrace();\n" +
				"\t\t}\n" +
				"\t\treturn false;\n" +
				"\t}\n" +
				"}";
	}
}