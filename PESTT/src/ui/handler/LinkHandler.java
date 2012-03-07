package ui.handler;

import main.activator.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import domain.constants.Messages;

public class LinkHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    if(Activator.getDefault().getSourceGraphController().isGraphDisplayed()) {
	    	boolean state = HandlerUtil.toggleCommandState(event.getCommand());
	    	Activator.getDefault().getCFGController().settLinkState(!state);
	    } else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), Messages.DRAW_GRAPH_TITLE, Messages.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		}
	    return null; 
	}	
}