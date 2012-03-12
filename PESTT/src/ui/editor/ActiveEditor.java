package ui.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import ui.constants.Description;
import domain.constants.JavadocTagAnnotations;

public class ActiveEditor {

	private IEditorPart part;
	private ITextSelection textSelect; // text selected in editor.
	private IFile file; // the current open file.
	private Markers marker; // marker to add.
	private ICompilationUnit compilationUnit;
	private IJavaProject javaProject;

	public ActiveEditor() {
		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		ITextEditor editor = (ITextEditor) part; // obtain the text editor.
		ISelection select = editor.getSelectionProvider().getSelection(); // the
																			// selected
																			// text.
		textSelect = (ITextSelection) select; // get the text selected.
		file = (IFile) part.getEditorInput().getAdapter(IFile.class); // get the
																		// file
		marker = new Markers(file);
		IProject project = file.getProject();
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				javaProject = JavaCore.create(project);
				compilationUnit = JavaCore.createCompilationUnitFrom(file);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void createMarker(String markerType, int offset, int length) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.bringToTop(part);
		marker.createMarks(markerType, offset, length);
	}

	public void removeALLMarkers() {
		marker.deleteAllMarkers();
	}

	public String getProjectName() {
		return javaProject.getElementName();
	}

	public String getPackageName() {
		IPackageDeclaration[] pd;
		try {
			pd = compilationUnit.getPackageDeclarations();
			if (pd.length != 0)
				return pd[0].getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}

	public List<String> getMethodNames() {
		List<String> methodNames = new LinkedList<String>();
		try {
			for (IType type : compilationUnit.getAllTypes())
				for (IMethod method : type.getMethods())
					methodNames.add(method.getElementName());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return methodNames;
	}

	public String getSelectedMethod() {
		try {
			for(IType type : compilationUnit.getAllTypes())
				for(IMethod method : type.getMethods()) {
					int cursorPosition = textSelect.getOffset();
					int methodStart = method.getSourceRange().getOffset();
					int methodEnd = method.getSourceRange().getOffset()
							+ method.getSourceRange().getLength();
					if (methodStart <= cursorPosition
							&& cursorPosition <= methodEnd)
						return method.getElementName();
				}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isInMethod() {
		if (getSelectedMethod() != null)
			return true;
		return false;
	}

	public String getClassName() {
		return compilationUnit.getElementName().substring(0,
				compilationUnit.getElementName().length() - 5);
	}

	public String getLocation() {
		if (!getPackageName().equals(Description.EMPTY))
			return getPackageName() + "." + getClassName();
		else
			return getClassName();
	}

	public String getClassFilePath() {
		try {
			String outputFolder = javaProject
					.getOutputLocation()
					.toOSString()
					.substring(
							getProjectName().length() + 2,
							javaProject.getOutputLocation().toOSString()
									.length());
			return javaProject.getResource().getLocation().toOSString()
					+ IPath.SEPARATOR + outputFolder + IPath.SEPARATOR
					+ getPackageName() + IPath.SEPARATOR + getClassName()
					+ ".class";
		} catch (JavaModelException e) {
			e.printStackTrace();
			return "";
		}
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@SuppressWarnings("unchecked")
	public void addJavadocTagAnnotation(CompilationUnit unit, MethodDeclaration method, JavadocTagAnnotations tagAnnotation, String path) {	
		unit.recordModifications();
		Javadoc javadoc = method.getJavadoc();
		if(javadoc == null) {
			javadoc = method.getAST().newJavadoc();
			method.setJavadoc(javadoc);
		}
		TagElement newTag = method.getAST().newTagElement();
		newTag.setTagName(tagAnnotation.getTag());
		TextElement newText = method.getAST().newTextElement();
		newText.setText(path);
		newTag.fragments().add(newText);
		javadoc.tags().add(newTag);
		applychanges(unit);
	}
	
	@SuppressWarnings("unchecked")
	public void removeJavadocTagAnnotation(CompilationUnit unit, MethodDeclaration method, JavadocTagAnnotations tagAnnotation, String path) {	
		unit.recordModifications();
		Javadoc javadoc = method.getJavadoc();
		List<TagElement> tags = (List<TagElement>) javadoc.tags();
		int index = -1;
		for(TagElement tag : tags) 
			if(tag.getTagName().equals(tagAnnotation.getTag()))
				if(tag.fragments().get(0).toString().equals(" " + path))
					index = tags.indexOf(tag);
		if(index != -1)
			tags.remove(index);
		applychanges(unit);
					
	}
	
	private void applychanges(CompilationUnit unit) {
		ITextEditor editor = (ITextEditor) part;
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		TextEdit edit = unit.rewrite(document, javaProject.getOptions(true));
		try {
			edit.apply(document);
		} catch (MalformedTreeException e) {
			e.printStackTrace(); 
		} catch (BadLocationException e) {
			e.printStackTrace(); 
		}
	}
}