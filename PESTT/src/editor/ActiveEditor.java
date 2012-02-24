package editor;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import constants.CompilationUnits_ID;

public class ActiveEditor {

	private IEditorPart part; // editor part.
	private ITextEditor editor;  // text editor.
	private ITextSelection textSelect; // text selected in editor.
	private IFile file; // the current open file.
	private Markers marker; // marker to add.
	private List<String> location; // file location;
	private boolean methodSelected;
	private String classPath;
	private String targetName;
	
	public ActiveEditor() {
		location = new LinkedList<String>();
		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(); // obtain the active editor.
		editor = (ITextEditor) part; // obtain the text editor.
		ISelection select = editor.getSelectionProvider().getSelection(); // the selected text.
		textSelect = (ITextSelection) select; // get the text selected.
		file = (IFile) part.getEditorInput().getAdapter(IFile.class); // get the file
		marker = new Markers(file);
		targetName = getLocation().get(CompilationUnits_ID.PACKAGE) + "." + getClassName();
		getClassPath();
	}
	
	public String getSelectedText() {
		return textSelect.getText(); // the text selected.
	}
	
	public void createMarker(String markerType, int offset, int length) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(part);
		marker.createMarks(markerType, offset, length);
	}
	
	public void deleteALLMarkers() {
		marker.deleteAllMarkers();
	}
	
	public List<String> getLocation() {
		location.clear();
		StringTokenizer strtok = new StringTokenizer(file.getRawLocation().toOSString(), "/");
		// separate the current open editor location.
		while(strtok.hasMoreTokens())
			location.add(strtok.nextToken());
		
		int projectPosition = 0;
		String relativePath = "/";
		String packageName = "";
		String projectName = file.getProject().getName();
		String className = location.get(location.size() - 1);
		
		// get the relative path of the project of the current open class.
		for(String str : location)
			if(!str.equals(file.getProject().getName()))
				relativePath += str + "/";
			else {
				projectPosition = location.indexOf(str) + 2;
				break;
			}
				
		// drop the final /
		if(!relativePath.equals("/"))
			relativePath = relativePath.substring(0, relativePath.length() - 1);
				
		// if the package is composed (e.g. org.eclipse)
		for(int i = projectPosition; i < location.size() - 1; i++) {
			String str = location.get(i);
			if(!str.equals(className))
				packageName += location.get(i) + ".";
		}
		
		// drop the final .
		if(!packageName.equals(""))
			packageName = packageName.substring(0, packageName.length() - 1);
				
		location.clear();
		location.add(CompilationUnits_ID.SYSTEM_LOCATION, relativePath);
		location.add(CompilationUnits_ID.PROJECT, projectName);
		location.add(CompilationUnits_ID.PACKAGE, packageName);
		location.add(CompilationUnits_ID.CLASS, className);
		
		IMethod[] iMethods =  getMethods(location);
		for(IMethod method : iMethods) {
			try {
				int cursorPosition = textSelect.getOffset();
				int methodStart = method.getSourceRange().getOffset();
				int methodEnd = method.getSourceRange().getOffset() + method.getSourceRange().getLength();
				if(methodStart <= cursorPosition && cursorPosition <= methodEnd) {
					methodSelected = true;
					location.add(CompilationUnits_ID.METHOD, method.getElementName());	
					break;
				} else 
					methodSelected = false;
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		return location;
	}
	
	public List<String> getMethodNames() {
		List<String> methods = new LinkedList<String>();
		for(IMethod method : getMethods(location))
			methods.add(method.getElementName());
		return methods;
			
		
	}
	
	public boolean isInMethod() {
		return methodSelected;
	}
		
	public String getClassName() {
		return location.get(CompilationUnits_ID.CLASS).substring(0, file.getName().length() - 5);
	}
	
	private IMethod[] getMethods(List<String> location) {
		IMethod[] methods = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects(); // get all projects in the workspace.
		for(IProject project : projects)  // loop over all projects.
			if(project.getName().equals(location.get(CompilationUnits_ID.PROJECT))) { // if the project name match.
				try {
					if(project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
						IPackageFragment[] packages = JavaCore.create(project).getPackageFragments(); // get all packages for the project.
						for(IPackageFragment mypackage : packages)  // loop over all packages.
							if(mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) 
								if(mypackage.getElementName().equals(location.get(CompilationUnits_ID.PACKAGE)))  // if the package name match.
									for(ICompilationUnit unit : mypackage.getCompilationUnits())  // loop over all compilation units in the package.
										if(unit.getElementName().equals(location.get(CompilationUnits_ID.CLASS)))  // if the compilation unit name match.
											for(IType type : unit.getAllTypes()) { //loop over all types in the compilation unit.
												methods = type.getMethods(); // get all methods for the type.									
											}	
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		return methods;
	} 
	
	public String getClassFile() {
		return classPath + "/" + targetName.replace('.', '/') + ".class";
	}
	
	public String getClassPath() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects(); // get all projects in the workspace.
		for(IProject project : projects)  // loop over all projects.
			if(project.getName().equals(getLocation().get(CompilationUnits_ID.PROJECT))) { // if the project name match.
				IJavaProject targetProject = JavaCore.create(project);
				try {
					classPath = getLocation().get(CompilationUnits_ID.SYSTEM_LOCATION) + targetProject.getOutputLocation();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			};
			return classPath;
	}
	
	public String getTargetName() {
		return targetName;
	}
	
}
