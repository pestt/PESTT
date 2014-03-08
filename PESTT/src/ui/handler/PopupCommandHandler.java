package ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.compiler.env.ISourceMethod;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.constants.Description;
import domain.constants.TourType;

public class PopupCommandHandler extends AbstractHandler {

	private String option = Description.EMPTY;
	private String old = TourType.TOUR.toString();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    Shell shell = HandlerUtil.getActiveShell(event);
	    ISelection sel = HandlerUtil.getActiveMenuSelection(event);
	    IStructuredSelection selection = (IStructuredSelection) sel;

	    String packageName = "";
	    String className = "";
	    String methodName = "";
	    int type = 0; // 0 = not recognised, 1 = suite, 2 = package, 3 = class, 4 = method
	    
	    Object firstSelectedElement = selection.getFirstElement();
	    if (firstSelectedElement instanceof IProject) 
	    	type = 1;
	    else if (firstSelectedElement instanceof IPackageFragment) {
	    	type = 2;
	    	IPackageFragment pf = (IPackageFragment) firstSelectedElement;
	    	packageName = pf.getElementName();
	    } else if (firstSelectedElement instanceof ICompilationUnit) {
	        type = 3;
	    	ICompilationUnit cu = (ICompilationUnit) firstSelectedElement;
	        IResource res = cu.getResource();
	        packageName = "?";
	        className = res.getName();
	    } else if (firstSelectedElement instanceof ISourceType) {
	    	type = 3;
	    	ISourceType st = (ISourceType) firstSelectedElement;
	    	className = st.toString();
	    	
	    } else if (firstSelectedElement instanceof ISourceMethod) {
	    	type = 4;
	    	ISourceMethod sm = (ISourceMethod) firstSelectedElement;
	    	methodName = sm.toString();
	    }
	    	
	    // em cima de um projeto é Project
	    // em cima de um pacote é um PackageFragment
	    // em cima de um ficheiro java é uma CompilationUnit
	    // em cima de uma classe é SourceType
	    // em cima de um método é SourceMethod
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
}