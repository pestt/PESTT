package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import domain.constants.TourType;

@XmlAccessorType(XmlAccessType.NONE)
public class ClassTest {
	@XmlAttribute private String qualifiedName;
	private Map<String, MethodTest> methods;
	
	public ClassTest() {
	}
	
	public ClassTest(String qualifiedName) {
		this.qualifiedName = qualifiedName;
		this.methods = new HashMap<String, MethodTest> ();
	}

	public void addMethodTest(MethodTest mt) {
		methods.put(mt.getMethodSignature(), mt);
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	@XmlElementWrapper(name = "methods")
	@XmlElement(name = "method") 
    public MethodTest[] getMethods() {
    	MethodTest [] mt = new MethodTest[methods.size()];
    	int i = 0;
    	for (Entry<String, MethodTest> entry : methods.entrySet()) 
    		mt[i++] = entry.getValue();
      return mt;
    }
	
	public void setMethods (MethodTest[] mt) {
		methods = new HashMap<String, MethodTest>();
    	for (MethodTest m : mt) 
    		methods.put(m.getMethodSignature(), m);
 	}

	public MethodTest getMethodTest(String methodSignature) {
		MethodTest mt = methods.get(methodSignature);
		return mt == null ? null : mt;
	}

	public MethodTest addMethodTest(String methodSignature, TourType tourType) {
		MethodTest mt = new MethodTest(methodSignature, tourType);
		methods.put(methodSignature, mt);
		return mt;
	}

}
