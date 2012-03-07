package domain;

import java.util.Observable;

import org.eclipse.jdt.core.ICompilationUnit;

import ui.editor.ActiveEditor;

public class EditorController extends Observable {
	
	private ActiveEditor editor;
	
	public boolean isInMethod() {
		return editor.isInMethod();
	}
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
	
	public ActiveEditor getActiveEditor() {
		return editor;
	}
	
	public String getSelectedMethod() {
		return editor.getSelectedMethod();
	}
	
	public ICompilationUnit getCompilationUnit() {
		return editor.getCompilationUnit();
	}
}
