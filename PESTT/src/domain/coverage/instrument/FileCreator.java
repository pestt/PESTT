package domain.coverage.instrument;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import main.activator.Activator;

public class FileCreator {
	
	private File file;
	private File dir;
	private PrintWriter writer;
	
	public void createDirectory(String location) {
		dir = new File(location);
		dir.mkdir();
	}
	
	public void deleteDirectory() {
		dir.delete();
	}
	
	public String getDirectoryLocation() {
		return dir.getAbsolutePath();
	}
	
	public void createFile(String name) {
		try {
			file = new File(getDirectoryLocation(), name);
			boolean created;
			created = this.file.createNewFile();
			writer = new PrintWriter(this.file);
			if(!created)
				cleanFileContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFile() {
		file.delete();
	}
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public String getLocation() {
		String pckg = Activator.getDefault().getEditorController().getPackageName();
		return pckg + '.' + file.getName().substring(0, file.getName().length() - 5);
	}
	
	public void cleanFileContent() {
		writer.write("");
		writer.flush();	
	}
	
	public void writeFileContent(String content) {
		writer.append(content);
		writer.flush();
	}
	
	public void close() {
		writer.close();
	} 
}
