package handler;

import layoutgraph.GraphInformation;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import constants.Description_ID;
import constants.Layer_ID;
import constants.Messages_ID;
import view.GraphsCreator;

public class LayerHandler extends AbstractHandler {

	String option = Description_ID.EMPTY;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(GraphsCreator.INSTANCE.isDisplayed()) { // the graph is displayed.
			GraphInformation info = new GraphInformation();
			String newOption = event.getParameter(RadioState.PARAMETER_ID); // get the current selected state.
			if(option.equals(Description_ID.EMPTY) && newOption.equals(Description_ID.NONE)) {
				option = Integer.toString(Layer_ID.EMPTY);
			   	HandlerUtil.updateRadioState(event.getCommand(), option); // update the current state.
			   	info.setLayerInformation(Integer.parseInt(option));
			   	return null;
			} 

			if(newOption != null) {
				if(!newOption.equals(Description_ID.NONE)) {
				   	HandlerUtil.updateRadioState(event.getCommand(), newOption); // update the current state.
				   	option = newOption;
				}
			}
			info.setLayerInformation(Integer.parseInt(option));
		} else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), Messages_ID.DRAW_GRAPH_TITLE, Messages_ID.DRAW_GRAPH_MSG); // message displayed when the graph is not designed.
		}
		return null;
	}
}