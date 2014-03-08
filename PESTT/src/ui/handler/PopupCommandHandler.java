package ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import ui.constants.Description;
import ui.constants.PopupMenuEntries;
import domain.constants.TourType;

public class PopupCommandHandler extends AbstractHandler {

	private String option = Description.EMPTY;
	private String old = TourType.TOUR.toString();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    IProject project = null;
	    IPackageFragment packageFragment = null;
	    ICompilationUnit compilationUnit = null;
	    IMethod sourceMethod = null;
	    
	    Object firstSelectedElement = selection.getFirstElement();
	    if (firstSelectedElement instanceof IProject) { 
	    	project = (IProject) firstSelectedElement;
	    } else if (firstSelectedElement instanceof IPackageFragment) {
	    	packageFragment = (IPackageFragment) firstSelectedElement;
	    	project = packageFragment.getJavaProject().getProject();
	    } else if (firstSelectedElement instanceof ICompilationUnit) {
	    	compilationUnit = (ICompilationUnit) firstSelectedElement;
	    } else if (firstSelectedElement instanceof IType) {
	    	compilationUnit = ((IType) firstSelectedElement).getCompilationUnit();
	    } else if (firstSelectedElement instanceof IMethod) {
	    	sourceMethod = (IMethod) firstSelectedElement;
	    	compilationUnit = sourceMethod.getCompilationUnit();
	    }

    	if (firstSelectedElement instanceof ICompilationUnit || firstSelectedElement instanceof IType ||
    			firstSelectedElement instanceof IMethod) {
    		project = compilationUnit.getJavaProject().getProject();
    		packageFragment = (IPackageFragment) compilationUnit.getParent();
    	}

    	String projectName = "";
	    String packageName = "";
	    String className = "";
	    String methodSignature = "";
	    boolean toUpdate = project != null;
	    if (project != null)  
	    	projectName =  project.getName();
	    if (packageFragment != null) 
	    	if (packageFragment.isDefaultPackage())
	    		packageName = "(default package)";
	    	else
	    		packageName = packageFragment.getElementName();
       	if (compilationUnit != null) 
       		className = compilationUnit.getElementName().substring(0, compilationUnit.getElementName().length() - 5);
   		if (sourceMethod != null)
	    	try {
				methodSignature = methodSignature(sourceMethod);
			} catch (JavaModelException e) {
				toUpdate = false;
			}

   		Object o = PopupMenuEntries.valueOf(event.getParameter(RadioState.PARAMETER_ID));
   		
/*					option = event.getParameter(RadioState.PARAMETER_ID); // get the current selected state.
					if (option != null && !option.equals(old)) {
						if (option != null && !option.equals(Description.NONE)) {
							HandlerUtil.updateRadioState(event.getCommand(),
									option); // update the current state.
							old = option;
						}
						*/
						// update domain
//						MessageDialog.openInformation(shell,
//								Messages.DRAW_GRAPH_TITLE,
//								"");
					//}

		return null;
	}
	
	private String methodSignature(IMethod iMethod) throws JavaModelException {
		String[] parameterTypes = iMethod.getParameterTypes();
		String[] parameterNames;
		parameterNames = iMethod.getParameterNames();
		StringBuilder methodSig = new StringBuilder(iMethod.isConstructor() ? "" : Signature.toString(iMethod.getReturnType()) + " ");
		methodSig.append(iMethod.getElementName());
		methodSig.append('(');
		String comma = "";
		for (int i = 0; i < parameterTypes.length; ++i) {
			methodSig.append(comma);
			comma = ", ";
			methodSig.append(Signature.toString(parameterTypes[i]));
			methodSig.append(' ');
			methodSig.append(parameterNames[i]);
		}
		methodSig.append(')');
		return methodSig.toString();
	}
}