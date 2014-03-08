package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import domain.constants.CoverageCriteriaId;
import domain.constants.TourType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TestSuite {
	private Map<String, PackageTest> packages;
	
	public TestSuite (CoverageCriteriaId coverageCriteria, TourType tourType) {
		this();
		this.coverageCriteria = coverageCriteria;
		this.tourType = tourType;
	}
	
	
	/**
	 * Constructor for the XML serialization framework 
	 */
	public TestSuite () {
		packages = new HashMap<String, PackageTest>();
	}
	
	public void addPackageTest(PackageTest pt) {
		packages.put(pt.getQualifiedName(), pt);
	}

	@XmlAttribute private CoverageCriteriaId coverageCriteria;
	@XmlAttribute private TourType tourType;
	
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

	public MethodTest getMethodTest(String packageName, String className,
			String methodSignature) {
		PackageTest pt = packages.get(packageName);
		return pt == null ? null : pt.getMethodTest(className, methodSignature);
	}
	
	public MethodTest addMethodTest(String packageName, String className,
			String methodSignature, TourType tourType) {
		PackageTest pt = packages.get(packageName);
		if (pt == null) {
			pt = new PackageTest(packageName);
			packages.put(packageName, pt);
		}
		return pt.addMethodTest(className, methodSignature, tourType);
	}
}
