package domain.coverage.instrument;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class RulesFileCreator {
	
	private File file;
	private File dir;
	private PrintWriter writer;
	
	public RulesFileCreator() {
		dir = new File(System.getProperty("java.io.tmpdir"));
	}
	
	public void createRulesFile() {
		file = new File(dir, "rules.btm");
		try {
			boolean created = file.createNewFile();
			writer = new PrintWriter(file);
			if(!created)
				cleanContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFileLocation() {
		return file.getAbsolutePath();
	}
	
	private void cleanContent() throws IOException {
		writer.write("");
		writer.flush();	
	}
	
	public void deleteRulesFile() {
		file.delete();
	}
	
	public void close() {
		writer.close();
	}
	
	public void writeRule(String rule) {
		writer.append(rule);
		writer.flush();
	}
}
