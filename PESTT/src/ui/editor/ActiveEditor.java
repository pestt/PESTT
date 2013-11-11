package ui.editor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ui.constants.Description;
import ui.constants.JavadocTagAnnotations;
import ui.events.TourChangeEvent;
import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.SourceGraph;
import domain.events.InfeasibleChangedEvent;
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
		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		editor = (ITextEditor) part; // obtain the text editor.
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

	public void addObservers() {
		Activator.getDefault().getTestRequirementController().addObserver(this);
		Activator.getDefault().getTestRequirementController()
				.addObserverTestRequirement(this);
		Activator.getDefault().getTestPathController()
				.addObserverTestPath(this);
		Activator.getDefault().getTestPathController().addObserver(this);
		addChangeListener();
	}

	public void deleteObservers() {
		Activator.getDefault().getTestRequirementController()
				.deleteObserver(this);
		Activator.getDefault().getTestRequirementController()
				.deleteObserverTestRequirement(this);
		Activator.getDefault().getTestPathController()
				.deleteObserverTestPath(this);
		Activator.getDefault().getTestPathController().deleteObserver(this);
		deleteChangeListener();
	}

	private void addChangeListener() {
		if (editor != null) {
			listener = new IDocumentListener() {

				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
					// does nothing
				}

				@Override
				public void documentChanged(DocumentEvent event) {
					updated = false;
				}
			};
			editor.getDocumentProvider().getDocument(editor.getEditorInput())
					.addPrenotifiedDocumentListener(listener);
		}
	}

	private void deleteChangeListener() {
		IEditorInput input = editor.getEditorInput();
		if (editor != null && listener != null && input != null) {
			IDocumentProvider provider = editor.getDocumentProvider();
			if (provider != null)
				provider.getDocument(input).removePrenotifiedDocumentListener(
						listener);
		}
	}

	public void setListenUpdates(boolean listenUpdates) {
		this.listenUpdates = listenUpdates;
	}

	public IEditorPart getEditorPart() {
		return part;
	}

	public void createMarker(String markerType, int offset, int length) {
		Activator.getDefault().getViewController().bringEditorToTop(part);
		marker.createMarks(markerType, offset, length);
	}

	public void removeALLMarkers() {
		marker.deleteAllMarkers();
		// Activator.getDefault().getViewController().bringEditorToTop(part);
		part.setFocus();
	}

	public boolean isEverythingMatching() {
		return updated;
	}

	public void everythingMatch() {
		this.updated = true;
		;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
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

	private MethodDeclaration getMethodDeclaration(CompilationUnit unit) {
		for (MethodDeclaration method : ((TypeDeclaration) unit.types().get(0))
				.getMethods())
			if (method.getName().toString().equals(getSelectedMethod()))
				return method;
		return null;
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
			for (IType type : compilationUnit.getAllTypes())
				for (IMethod method : type.getMethods()) {
					int cursorPosition = textSelect.getOffset();
					int methodStart = method.getSourceRange().getOffset();
					int methodEnd = method.getSourceRange().getOffset()
							+ method.getSourceRange().getLength();
					if (methodStart <= cursorPosition
							&& cursorPosition <= methodEnd) {
						String[] parameterTypes = method.getParameterTypes();
						String[] parameterNames = method.getParameterNames();
						String name = method.getElementName() + "(";
						for (int i = 0; i < parameterTypes.length; ++i)
							name += Signature.toString(parameterTypes[i]) + " "
									+ parameterNames[i] + ", ";
						if (parameterTypes.length != 0)
							name = name.substring(0, name.length() - 2);
						name += ")";
						return name;
					}
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
		return compilationUnit.getElementName().substring(0,
				compilationUnit.getElementName().length() - 5);
	}

	public String getLocation() {
		return !getPackageName().equals(Description.EMPTY) ? getPackageName()
				+ "." + getClassName() : getClassName();
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
			if (!getPackageName().isEmpty())
				return javaProject.getResource().getLocation().toOSString()
						+ IPath.SEPARATOR + outputFolder + IPath.SEPARATOR
						+ getPackageName() + IPath.SEPARATOR + getClassName()
						+ ".class";
			else
				return javaProject.getResource().getLocation().toOSString()
						+ IPath.SEPARATOR + outputFolder + IPath.SEPARATOR
						+ getClassName() + ".class";
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
		if (listenUpdates) {
			CompilationUnit unit = Activator.getDefault()
					.getSourceGraphController()
					.getCompilationUnit(compilationUnit);
			unit.recordModifications();
			MethodDeclaration method = getMethodDeclaration(unit);
			if (method != null
					&& Activator.getDefault().getTestRequirementController()
							.isCoverageCriteriaSelected())
				if (data instanceof TestRequirementSelectedCriteriaEvent
						|| data instanceof InfeasibleChangedEvent
						|| data instanceof TourChangeEvent
						|| data instanceof TestRequirementChangedEvent
						|| data instanceof TestPathChangedEvent) {
					String criteria = Activator.getDefault()
							.getTestRequirementController()
							.getSelectedCoverageCriteria().toString();
					String tour = Activator.getDefault()
							.getTestPathController().getSelectedTourType()
							.toString();
					Iterable<AbstractPath<Integer>> infeasibles = Activator
							.getDefault().getTestRequirementController()
							.getInfeasiblesTestRequirements();
					Iterable<Path<Integer>> testRequirementsManuallyAdded = Activator
							.getDefault().getTestRequirementController()
							.getTestRequirementsManuallyAdded();
					Iterable<Path<Integer>> testPathManuallyAdded = Activator
							.getDefault().getTestPathController()
							.getTestPathsManuallyAdded();
					setJavadocAnnotation(unit, method, criteria, tour,
							infeasibles, testRequirementsManuallyAdded,
							testPathManuallyAdded);
				}
		}
	}

	/***
	 * Update the method Javadoc annotations.
	 * 
	 * @param unit
	 *            - The current CompilationUnit.
	 * @param method
	 *            - The method in use.
	 * @param criteria
	 *            - The selected coverage criteria.
	 * @param tour
	 *            - The tour in use.
	 * @param infeasibles
	 *            - The infeasible paths identified.
	 * @param testRequireents
	 *            - The Test Requirements manually added to Test Requirement
	 *            set.
	 * @param testPath
	 *            - The Test Paths manually added to Test Path set.
	 */
	private void setJavadocAnnotation(CompilationUnit unit,
			MethodDeclaration method, String criteria, String tour,
			Iterable<AbstractPath<Integer>> infeasibles,
			Iterable<Path<Integer>> testRequireents,
			Iterable<Path<Integer>> testPath) {
		boolean temp = updated;
		Javadoc javadoc = getJavadoc(method);
		List<String> input = new ArrayList<String>();
		input.add(criteria);
		createTag(method, JavadocTagAnnotations.COVERAGE_CRITERIA.getTag(),
				getTextInput(method, input), javadoc);
		input.clear();
		input.add(tour);
		createTag(method, JavadocTagAnnotations.TOUR_TYPE.getTag(),
				getTextInput(method, input), javadoc);
		input.clear();
		for (AbstractPath<Integer> path : infeasibles) {
			input.add(path.toString());
			createTag(method, JavadocTagAnnotations.INFEASIBLE_PATH.getTag(),
					getTextInput(method, input), javadoc);
			input.clear();
		}
		for (Path<Integer> path : testRequireents) {
			input.add(path.toString());
			createTag(method,
					JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH
							.getTag(), getTextInput(method, input), javadoc);
			input.clear();
		}
		for (Path<Integer> path : testPath) {
			input.add(path.toString());
			createTag(method,
					JavadocTagAnnotations.ADDITIONAL_TEST_PATH.getTag(),
					getTextInput(method, input), javadoc);
			input.clear();
		}
		applychanges(unit);
		updated = temp;
		verifyChanges(method);
	}

	private List<TextElement> getTextInput(MethodDeclaration method,
			List<String> inputs) {
		List<TextElement> input = new ArrayList<TextElement>();
		for (String text : inputs) {
			TextElement newText = method.getAST().newTextElement();
			newText.setText(text);
			input.add(newText);
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	private void createTag(MethodDeclaration method, String tagAnnotation,
			List<TextElement> input, Javadoc javadoc) {
		TagElement newTag = method.getAST().newTagElement();
		newTag.setTagName(tagAnnotation);
		newTag.fragments().addAll(input);
		javadoc.tags().add(newTag);
	}

	@SuppressWarnings("unchecked")
	private Javadoc getJavadoc(MethodDeclaration method) {
		List<TagElement> tagToKeep = keepTags(method);
		Javadoc javadoc = method.getAST().newJavadoc();
		method.setJavadoc(javadoc);
		if (!tagToKeep.isEmpty()) {
			List<String> input = new ArrayList<String>();
			input.add("");
			createTag(method, null, getTextInput(method, input), javadoc);
			for (TagElement tag : tagToKeep) {
				List<TextElement> fragments = tag.fragments();
				input.clear();
				for (Object obj : fragments)
					if (obj instanceof TextElement) {
						TextElement text = (TextElement) obj;
						if (tag.getTagName() != null) {
							if (fragments.indexOf(text) == 0)
								input.add(text.getText().substring(1,
										text.getText().length())
										+ "\n");
							else if (fragments.indexOf(text) == fragments
									.size() - 1)
								input.add(text.getText());
							else
								input.add(text.getText() + "\n *");
						} else {
							if (fragments.indexOf(text) == fragments.size() - 1)
								input.add(text.getText());
							else
								input.add(text.getText() + "\n *");
						}
					} else {
						SimpleName text = (SimpleName) obj;
						input.add(text.getIdentifier());
					}
				createTag(method, tag.getTagName(),
						getTextInput(method, input), javadoc);
			}
			input.clear();
			input.add("");
			createTag(method, null, getTextInput(method, input), javadoc);
		}
		return javadoc;
	}

	/***
	 * View all tags in the method Javadoc. If the tags is external to the
	 * program it must be kept.
	 * 
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<TagElement> keepTags(MethodDeclaration method) {
		List<TagElement> tagToKeep = new ArrayList<TagElement>();
		if (method.getJavadoc() != null) {
			List<TagElement> currentTags = method.getJavadoc().tags();
			for (TagElement tag : currentTags)
				if (tag.getTagName() != null) {
					if (!tag.getTagName().equals(
							JavadocTagAnnotations.COVERAGE_CRITERIA.getTag())
							&& !tag.getTagName().equals(
									JavadocTagAnnotations.TOUR_TYPE.getTag())
							&& !tag.getTagName().equals(
									JavadocTagAnnotations.INFEASIBLE_PATH
											.getTag())
							&& !tag.getTagName()
									.equals(JavadocTagAnnotations.ADDITIONAL_TEST_REQUIREMENT_PATH
											.getTag())
							&& !tag.getTagName().equals(
									JavadocTagAnnotations.ADDITIONAL_TEST_PATH
											.getTag()))
						tagToKeep.add(tag);
					else {
						if (tag.fragments().size() > 1) {
							tag.fragments().remove(0);
							tag.setTagName(null);
							tagToKeep.add(tag);
						}
					}
				} else
					tagToKeep.add(tag);
		}
		return tagToKeep;
	}

	private void applychanges(CompilationUnit unit) {
		IDocument document = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
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
		CompilationUnit unit = Activator.getDefault()
				.getSourceGraphController().getCompilationUnit(compilationUnit);
		unit.recordModifications();
		MethodDeclaration temp = getMethodDeclaration(unit);
		byte[] currentHash = Activator.getDefault().getSourceGraphController()
				.getMethodHash();
		byte[] tempHash = getMethodHash(temp);
		boolean result = Arrays.equals(currentHash, tempHash) ? true : false;
		if (isEverythingMatching() && result) {
			SourceGraph source = new SourceGraph();
			source.create(compilationUnit, getSelectedMethod());
			Activator.getDefault().getSourceGraphController()
					.updateMetadataInformation(source.getSourceGraph());
		} else
			updated = false;
	}

	private byte[] getMethodHash(MethodDeclaration method) {
		try {
			byte[] bytesOfMessage = method.getBody().toString()
					.getBytes("UTF-8");
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