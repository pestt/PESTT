package domain.coverage.instrument;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IPath;

public class HelperCreator {
	
	private File file;
	private String dir;
	private String packageName;
	private PrintWriter writer;
	
	public HelperCreator(String dir, String packageName,  String file) {
		this.dir = dir + IPath.SEPARATOR + packageName.replace('.', IPath.SEPARATOR);
		this.packageName = packageName;
		createFile(file);
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
	
	public void deleteHelper() {
		file.delete();
	}
	
	public void close() {
		writer.close();
	}
	
	public void writeHelper(String rule) {
		writer.append(rule);
		writer.flush();
	}

	public String getName() {
		return packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
	}
}