package ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import domain.jaxb.InformationDetails;
import domain.jaxb.Storage;

public class ExportToXMLHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InformationDetails info = new InformationDetails();
		info.setInformation();
	    Storage storage = new Storage();
	    storage.objectToXML(info);
	    //info.XMLToObject();
	    return null;
	}

}
