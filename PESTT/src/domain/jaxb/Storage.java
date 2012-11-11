package domain.jaxb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

public class Storage {

	private static final String INFO_XML = "pestt_info.xml";
	JAXBContext context;

	public Storage() {
		try {
			context = JAXBContext.newInstance(InformationDetails.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void objectToXML(InformationDetails info) {
		try {	     
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(info, System.out);
			Writer writer = new FileWriter(INFO_XML);
			marshaller.marshal(info, writer);
			writer.close();
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void XMLToObject() {
		/*try {
			System.out.println();
			System.out.println("Output from our XML File: ");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Bookstore bookstore2 = (Bookstore) unmarshaller.unmarshal(new FileReader(INFO_XML));
			for (int i = 0; i < bookstore2.getBooksList().toArray().length; i++) {
			      System.out.println("Book " + (i + 1) + ": "
			          + bookstore2.getBooksList().get(i).getName() + " from "
			          + bookstore2.getBooksList().get(i).getAuthor());
			    }
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
	}

}
