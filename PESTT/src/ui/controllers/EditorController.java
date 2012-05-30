package ui.controllers;

import java.util.List;
import java.util.Observable;

import main.activator.Activator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.zest.core.widgets.GraphItem;

import ui.editor.ActiveEditor;
import ui.source.GraphInformation;
import domain.constants.Layer;
import domain.events.TestPathSelectedEvent;

public class EditorController extends Observable {
	
	private ActiveEditor editor;
	private GraphInformation information;	
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
	
	public ActiveEditor getActiveEditor() {
		return editor;
	}
	
	public IEditorPart getEditorPart() {
		return editor.getEditorPart();
	}
	
	public String getPackageName() {
		return editor.getPackageName();
	}
	
	public String getClassName() {
		return editor.getClassName();
	}
	
	public boolean isInMethod() {
		return editor.isInMethod();
	}
	
	public String getSelectedMethod() {
		return editor.getSelectedMethod();
	}
	
	public ICompilationUnit getCompilationUnit() {
		return editor.getCompilationUnit();
	}
	
	public void createMarker(String markerType, int offset, int length) {
		editor.createMarker(markerType, offset, length);		
	}
	
	public void removeALLMarkers() {
		editor.removeALLMarkers();
	}
	
	public void setListenUpdates(boolean b) {
		editor.setListenUpdates(b);
	}
	
	public boolean isEverythingMatching() {
		return editor.isEverythingMatching();
	}
	
	public void everythingMatch() {
		editor.everythingMatch();
	}
	
	public IJavaProject getJavaProject() {
		return editor.getJavaProject();
	}
	
	public void setGraphInformation(GraphInformation information) {
		this.information = information;		
	}
	
	public void creatorSelectToEditor() {
		information.creatorSelectToEditor();
	}
	
	public void removeSelectToEditor() {
		information.removeSelectToEditor();
	}
	
	public void setLayerInformation(Layer layer) {
		information.setLayerInformation(layer);		
	}

	public void setVisualCoverage(Object data, List<GraphItem> items) {
		if(Activator.getDefault().getCFGController().getLinkState()) // if the link button is on.
			if(data instanceof TestPathSelectedEvent) 
				information.setVisualCoverageStatus(Activator.getDefault().getTestPathController().getCoverageData(), items);
			else 
				information.setLayerInformation(Layer.INSTRUCTIONS); // set the information to the instructions layer.
	}
}