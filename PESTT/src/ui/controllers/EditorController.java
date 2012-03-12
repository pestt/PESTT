package ui.controllers;

import java.util.List;
import java.util.Observable;

import main.activator.Activator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ui.editor.ActiveEditor;
import domain.constants.JavadocTagAnnotations;
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

	
	public void addJavadocTagAnnotation(JavadocTagAnnotations tagAnnotation, String path) {
		CompilationUnit unit = Activator.getDefault().getSourceGraphController().getCompilationUnit(editor.getCompilationUnit());
		for(MethodDeclaration method : ((TypeDeclaration) unit.types().get(0)).getMethods())
			if(method.getName().toString().equals(getSelectedMethod())) {
				if(!existTag(method.getJavadoc(), tagAnnotation, path))
					editor.addJavadocTagAnnotation(unit, method, tagAnnotation, path);	
				break;
			}
	}

	@SuppressWarnings("unchecked")
	private boolean existTag(Javadoc javadoc, JavadocTagAnnotations tagAnnotation, String path) {
		if(javadoc != null) {
			List<TagElement> tags = (List<TagElement>) javadoc.tags();
			for(TagElement tag : tags) 
				if(tag.getTagName() != null)
					if(tag.getTagName().equals(tagAnnotation.getTag()) && !tag.fragments().isEmpty())
						if(tag.fragments().get(0).toString().equals(" " + path))
							return true;
		}
		return false;	
	}

	public void removeJavadocTagAnnotation(JavadocTagAnnotations tagAnnotation, String path) {
		CompilationUnit unit = Activator.getDefault().getSourceGraphController().getCompilationUnit(editor.getCompilationUnit());
		for(MethodDeclaration method : ((TypeDeclaration) unit.types().get(0)).getMethods())
			if(method.getName().toString().equals(getSelectedMethod())) 
				editor.removeJavadocTagAnnotation(unit, method, tagAnnotation, path);
	}
}
