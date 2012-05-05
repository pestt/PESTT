package domain.coverage.instrument;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class RulesFileCreator {
	
	private File file;
	private File dir;
	private PrintWriter writer;
	
	public RulesFileCreator(String dir, String file) {
		createDir(dir);
		createFile(file);
	}
	
	private void createDir(String dir) {
		this.dir = new File(dir);
		this.dir.mkdir();
	}

	private void createFile(String file) {
		this.file = new File(dir, file);
		try {
			boolean created = this.file.createNewFile();
			writer = new PrintWriter(this.file);
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
