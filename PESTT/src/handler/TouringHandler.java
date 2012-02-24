package handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import view.GraphsCreator;
import view.ViewRequirementSet;
import constants.Description_ID;
import constants.Tour_ID;

public class TouringHandler extends AbstractHandler {

	String option = Description_ID.EMPTY;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(GraphsCreator.INSTANCE.isDisplayed()) { // the graph is displayed.
			ViewRequirementSet viewRequirementSet = (ViewRequirementSet) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Description_ID.VIEW_REQUIREMENT_SET); // get the view requirement set.
			String newOption = event.getParameter(RadioState.PARAMETER_ID); // get the current selected state.
			if(option.equals(Description_ID.EMPTY) && newOption.equals(Description_ID.NONE)) {
				option = Tour_ID.TOUR_ID;
			   	HandlerUtil.updateRadioState(event.getCommand(), option); // update the current state.
			   	viewRequirementSet.setTour(option);
			   	return null;
			} 

			if(newOption != null) 
				if(!newOption.equals(Description_ID.NONE)) {
				   	HandlerUtil.updateRadioState(event.getCommand(), newOption); // update the current state.
				   	option = newOption;
				}
			viewRequirementSet.setTour(option);
		}
		return null;
	}
}