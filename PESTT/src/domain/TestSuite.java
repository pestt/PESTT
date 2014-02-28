package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TestSuite {
	private String filename;
	private Map<String, PackageTest> packages;
	
	public TestSuite (String filename) {
		packages = new HashMap<String, PackageTest>();
		this.filename = filename;
	}
	
	public TestSuite() {
	}
	
	public void addPackageTest(PackageTest pt) {
		packages.put(pt.getQualifiedName(), pt);
	}
	
	@XmlElementWrapper(name = "packages")
	@XmlElement(name = "package") 
	public PackageTest[] getPackages () {
	   	PackageTest [] pt = new PackageTest[packages.size()];
    	int i = 0;
    	for (Entry<String, PackageTest> entry : packages.entrySet()) 
    		pt[i++] = entry.getValue();
    	return pt;
	}

	public void setPackages (PackageTest[] pt) {
		packages = new HashMap<String, PackageTest>();
    	for (PackageTest t : pt) 
    		packages.put(t.getQualifiedName(), t);
 	}

	public void update() {
	}
	
	public void flush () {
		try {
			JAXBContext context = JAXBContext.newInstance(TestSuite.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Writer writer = new FileWriter(filename);
			marshaller.marshal(this, writer);
			writer.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
