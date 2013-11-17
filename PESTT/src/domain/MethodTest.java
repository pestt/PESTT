package domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import adt.graph.AbstractPath;
import adt.graph.Path;
import domain.constants.CoverageCriteriaId;


@XmlAccessorType(XmlAccessType.FIELD)
public class MethodTest {

	@XmlAttribute private String methodSignature;
	@XmlElement private CoverageCriteriaId coverageCriteria;
	@XmlElement private String tourType;
	@XmlElementWrapper(name = "infeasiblePaths")
	@XmlElement(name = "infeasiblePath")
	private Set<AbstractPath<Integer>> infeasibleSet;
	@XmlElementWrapper(name = "testPaths")
	@XmlElement(name = "testPath")
	private Set<Path<Integer>> manuallyTestRequirementSet;
		
	public MethodTest() {
	}

	public MethodTest(String methodSignature) {
		this.methodSignature = methodSignature;
	}
	
	public String getMethodSignature() {
		return methodSignature;
	}
}
