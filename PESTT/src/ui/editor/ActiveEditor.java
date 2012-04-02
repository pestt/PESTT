package ui.editor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import main.activator.Activator;

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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import ui.constants.Description;
import ui.constants.JavadocTagAnnotations;
import ui.constants.Messages;
import ui.events.TourChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.SourceGraph;
import domain.events.TestPathChangedEvent;
import domain.events.TestRequirementChangedEvent;
import domain.events.TestRequirementSelectedCriteriaEvent;

public class ActiveEditor implements Observer {

	private IEditorPart part;
	private ITextEditor editor;
	private ITextSelection textSelect; // text selected in editor.
	private IFile file; // the current open file.
	private Markers marker; // marker to add.
	private ICompilationUnit compilationUnit;
	private IJavaProject javaProject;
	private boolean listenUpdates;
	private boolean updated;
	private IDocumentListener listener;

	public ActiveEditor() {
		listenUpdates = true;
		updated = true;
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestRequirementController().addObserverTestRequirement(this);
		Activator.getDefault().getTestPathController().addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		editor = (ITextEditor) part; // obtain the text editor.
		addChangeListener();
		ISelection select = editor.getSelectionProvider().getSelection(); // the selected text.
		textSelect = (ITextSelection) select; // get the text selected.
		file = (IFile) part.getEditorInput().getAdapter(IFile.class); // get the file
		marker = new Markers(file);
		IProject project = file.getProject();
		try {
			if(project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				javaProject = JavaCore.create(project);
				compilationUnit = JavaCore.createCompilationUnitFrom(file);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void setListenUpdates(boolean listenUpdates) {
		this.listenUpdates = listenUpdates;
	}
	
	public void deleteObservers() {
		Activator.getDefault().getTestRequirementController().deleteObserver(this);
		Activator.getDefault().getTestRequirementController().deleteObserverTestRequirement(this);
		Activator.getDefault().getTestPathController().deleteObserverTestPath(this);
		Activator.getDefault().getTestPathController().deleteObserver(this);
		deleteChangeListener();
	}
	
	private void addChangeListener() {
		if(editor != null) {
			listener = new IDocumentListener() {
	
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					// do nothing
				}
	
				@Override
				public void documentChanged(DocumentEvent event) {
					removeALLMarkers();
					updated = false;
				}
			};
			editor.getDocumentProvider().getDocument(editor.getEditorInput()).addPrenotifiedDocumentListener(listener);
		}
	}
	
	private void deleteChangeListener() {
		IEditorInput input = editor.getEditorInput();
		if(editor != null && listener != null && input != null)
			editor.getDocumentProvider().getDocument(input).removePrenotifiedDocumentListener(listener);
	}
	
	public void createMarker(String markerType, int offset, int length) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(part);
		marker.createMarks(markerType, offset, length);
	}

	public void removeALLMarkers() {
		marker.deleteAllMarkers();
	}
	
	public boolean isEverythingMatching() {
		return updated;
	}
	
	public void everythingMatch() {
		this.updated = true;;
	}

	public String getProjectName() {
		return javaProject.getElementName();
	}

	public String getPackageName() {
		IPackageDeclaration[] pd;
		try {
			pd = compilationUnit.getPackageDeclarations();
			if(pd.length != 0)
				return pd[0].getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private MethodDeclaration getMethodDeclaration(CompilationUnit unit) {
		for(MethodDeclaration method : ((TypeDeclaration) unit.types().get(0)).getMethods()) 
			if(method.getName().toString().equals(getSelectedMethod())) 
				return method;
		return null;
	}

	public List<String> getMethodNames() {
		List<String> methodNames = new LinkedList<String>();
		try {
			for(IType type : compilationUnit.getAllTypes())
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
					int methodEnd = method.getSourceRange().getOffset() + method.getSourceRange().getLength();
					if(methodStart <= cursorPosition && cursorPosition <= methodEnd)
						return method.getElementName();
				}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isInMethod() {
		return getSelectedMethod() != null ? true : false;
	}

	public String getClassName() {
		return compilationUnit.getElementName().substring(0, compilationUnit.getElementName().length() - 5);
	}

	public String getLocation() {
		return !getPackageName().equals(Description.EMPTY) ? getPackageName() + "." + getClassName() : getClassName();
	}

	public String getClassFilePath() {
		try {
			String outputFolder = javaProject.getOutputLocation().toOSString().substring(getProjectName().length() + 2, javaProject.getOutputLocation().toOSString().length());
			return javaProject.getResource().getLocation().toOSString() + IPath.SEPARATOR + outputFolder + IPath.SEPARATOR + getPackageName() + IPath.SEPARATOR + getClassName() + ".class";
		} catch (JavaModelException e) {
			e.printStackTrace();
			return "";
		}
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public void update(Observable obs, Object data) {
		if(listenUpdates) {
			CompilationUnit unit = Activator.getDefault().getSourceGraphController().getCompilationUnit(compilationUnit);
			unit.recordModifications();
			MethodDeclaration method = getMethodDeclaration(unit);
			if(method != null && Activator.getDefault().getTestRequirementController().isCoverageCriteriaSelected()) 
				if(data instanceof TestRequirementSelectedCriteriaEvent  ||
				   data instanceof TourChangeEvent ||
				   data instanceof TestRequirementChangedEvent ||
				   data instanceof TestPathChangedEvent) {
						String criteria = Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria().toString();
						String tour = Activator.getDefault().getTestPathController().getSelectedTourType().toString();
						Iterable<AbstractPath<Integer>> infeasibles = Activator.getDefault().getTestRequirementController().getInfeasiblesTestRequirements();
						Iterable<Path<Integer>> manuallyAdded = Activator.getDefault().getTestRequirementController().getTestRequirementsManuallyAdded();
						Iterable<Path<Integer>> testPath = Activator.getDefault().getTestPathController().getTestPathsManuallyAdded();
						setJavadocAnnotation(unit, method, criteria, tour, infeasibles, manuallyAdded, testPath);		
				}
		}
	}
	
	private void setJavadocAnnotation(CompilationUnit unit, MethodDeclaration method, String criteria, String tour, Iterable<AbstractPath<Integer>> infeasibles, Iterable<Path<Integer>> testRequireents, Iterable<Path<Integer>> testPath) {	
		boolean temp = updated;
		Javadoc javadoc = method.getAST().newJavadoc();
		method.setJavadoc(javadoc);
		createTag(method, JavadocTagAnnotations.COVERAGE_CRITERIA, criteria, javadoc);
		createTag(method, JavadocTagAnnotations.TOUR_TYPE, tour, javadoc);
		for(AbstractPath<Integer> path : infeasibles)
			createTag(method, JavadocTagAnnotations.INFEASIBLE_PATH, path.toString(), javadoc);
		for(Path<Integer> path : testRequireents)
			createTag(method, JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH, path.toString(), javadoc);
		for(Path<Integer> path : testPath)
			createTag(method, JavadocTagAnnotations.ADDITIONAL_TEST_PATH, path.toString(), javadoc); 
		applychanges(unit);
		updated = temp;
		verifyChanges(method);
	}

	@SuppressWarnings("unchecked")
	private void createTag(MethodDeclaration method, JavadocTagAnnotations tagAnnotation, String input, Javadoc javadoc) {
		TagElement newTag = method.getAST().newTagElement();
		newTag.setTagName(tagAnnotation.getTag());
		TextElement newText = method.getAST().newTextElement();
		newText.setText(input);
		newTag.fragments().add(newText);
		javadoc.tags().add(newTag);
	}
	
	private void applychanges(CompilationUnit unit) {
		ITextEditor editor = (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();;
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

	private void verifyChanges(MethodDeclaration method) {
		CompilationUnit unit = Activator.getDefault().getSourceGraphController().getCompilationUnit(compilationUnit);
		unit.recordModifications();
		MethodDeclaration temp = getMethodDeclaration(unit);
		byte[] currentHash = Activator.getDefault().getSourceGraphController().getMethodHash();
		byte[] tempHash = getMethodHash(temp);
		boolean result = Arrays.equals(currentHash, tempHash) ? true : false;
		if(isEverythingMatching() && result) {
			Activator.getDefault().getEditorController().removeALLMarkers();
			SourceGraph source = new SourceGraph(); 
			source.create(compilationUnit, getSelectedMethod());
			Activator.getDefault().getSourceGraphController().updateMetadataInformation(source.getSourceGraph());
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.GRAPH_UPDATE_MSG);
			updated = true;
		}
	}
	
	private byte[] getMethodHash(MethodDeclaration method) {
		try {
			byte[] bytesOfMessage = method.getBody().toString().getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(bytesOfMessage);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}