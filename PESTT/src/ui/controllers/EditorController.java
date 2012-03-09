package ui.controllers;

import java.util.Observable;

import main.activator.Activator;

import org.eclipse.jdt.core.ICompilationUnit;

import ui.editor.ActiveEditor;
import domain.constants.Layer;
import domain.events.TestPathSelectedEvent;

public class EditorController extends Observable {
	
	private ActiveEditor editor;
	private ui.source.GraphInformation information;	
	
	public void setGraphInformation(ui.source.GraphInformation information) {
		this.information = information;		
	}
	
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

	public void creatorSelectToEditor() {
		information.creatorSelectToEditor();
	}

	public void removeSelectToEditor() {
		information.removeSelectToEditor();
	}

	public void removeALLMarkers() {
		editor.removeALLMarkers();
	}

	public void setLayerInformation(Layer layer) {
		information.setLayerInformation(layer);		
	}

	public void setVisualCoverage(Object data) {
		if(Activator.getDefault().getCFGController().getLinkState()) // if the link button is on.
			if(data instanceof TestPathSelectedEvent) 
				information.setVisualCoverageStatus(Activator.getDefault().getTestPathController().getCoverageData());
			else 
				information.setLayerInformation(Layer.INSTRUCTIONS); // set the information to the instructions layer.
	}
}
