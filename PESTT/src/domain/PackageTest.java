package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public class PackageTest {

	@XmlAttribute private String qualifiedName;
	private Map<String, ClassTest> classes;
	
	public PackageTest() {
	}
	
	public PackageTest(String qualifiedName) {
		this.qualifiedName = qualifiedName;
		this.classes = new HashMap<String, ClassTest> ();
	}

	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public void addClassTest(ClassTest ct) {
		classes.put(ct.getQualifiedName(), ct);
	}
	
	@XmlElementWrapper(name = "classes")
	@XmlElement(name = "class") 
    public ClassTest[] getClasses() {
    	ClassTest [] ct = new ClassTest[classes.size()];
    	int i = 0;
    	for (Entry<String, ClassTest> entry : classes.entrySet()) 
    		ct[i++] = entry.getValue();
      return ct;
    }

	public void setClasses (ClassTest[] ct) {
		classes = new HashMap<String, ClassTest>();
    	for (ClassTest c : ct) 
    		classes.put(c.getQualifiedName(), c);
 	}

	public MethodTest getMethodTest(String className, String methodSignature) {
		ClassTest ct = classes.get(className);
		if (ct == null) {
			ct = new ClassTest(className);
			classes.put(className, ct);
		}
		return ct.getMethodTest(methodSignature);
	}
}
