package domain.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import main.activator.Activator;
import adt.graph.AbstractPath;

@XmlRootElement
public class InformationDetails {
	
	@XmlElement(name = "methodName")
	private String methodName;
	@XmlElement(name = "coverageCriteria")
	private String coverageCriteria;
	@XmlElement(name = "tourType")
	private String tourType;
	@XmlElement(name = "graph")
	private GraphDetails graphDetails; 
	@XmlElementWrapper(name = "requirementPathsList")
	@XmlElement(name = "requirementPath")
	private List<String> requirements;
	@XmlElementWrapper(name = "infeasiblePathsList")
	@XmlElement(name = "infeasiblePath")
	private List<String> infeasibles;
	@XmlElementWrapper(name = "testPathList")
	@XmlElement(name = "testPath")
	private List<String> testpaths;
	@XmlElement(name = "defusesList")
	private DefUsesDetails defusesDetails; 
	
	public InformationDetails() {
		graphDetails = new GraphDetails();
		requirements = new ArrayList<String>();
		infeasibles = new ArrayList<String>();
		testpaths = new ArrayList<String>();
		defusesDetails = new DefUsesDetails();
	}
	
	public void setInformation() {
		setMethodName();
		setCoverageCriteria();
		setTourType();
		graphDetails.setGraphDetails();
		setMethodRequirements();
		setMethodInfeasibles();
		setMethodTestPath();
		defusesDetails.setMethodDefUses();
	}
	
	private void setMethodName() {
		methodName  = Activator.getDefault().getEditorController().getSelectedMethod();
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	private void setCoverageCriteria() {
		coverageCriteria = Activator.getDefault().getTestRequirementController().getSelectedCoverageCriteria().toString();
	}
	
	public String getCoverageCriteria() {
		return coverageCriteria;
	}
	
	private void setTourType() {
		tourType = Activator.getDefault().getTestPathController().getSelectedTourType().toString();
	}

	public String getTourType() {
		return tourType;
	}
	
	public GraphDetails getGRaphDetails() {
		return graphDetails;
	}
	
	private void setMethodRequirements() {
		Set<AbstractPath<Integer>> temp = new TreeSet<AbstractPath<Integer>>();
		for(AbstractPath<Integer> path : Activator.getDefault().getTestRequirementController().getTestRequirements())
			temp.add(path);
		for(AbstractPath<Integer> path : Activator.getDefault().getTestRequirementController().getTestRequirementsManuallyAdded())
			temp.add(path);
		for(AbstractPath<Integer> path : temp)
			requirements.add(path.toString());
	}
	
	public List<String> getMethodRequirements() {
		return requirements;
	}
	
	private void setMethodInfeasibles() {
		for(AbstractPath<Integer> path : Activator.getDefault().getTestRequirementController().getInfeasiblesTestRequirements())
			infeasibles.add(path.toString());
	}
	
	public List<String> getMethodInfeasibles() {
		return infeasibles;
	}
	
	private void setMethodTestPath() {
		Set<AbstractPath<Integer>> temp = new TreeSet<AbstractPath<Integer>>();
		for(AbstractPath<Integer> path : Activator.getDefault().getTestPathController().getTestPaths())
			temp.add(path);
		for(AbstractPath<Integer> path : Activator.getDefault().getTestPathController().getTestPathsManuallyAdded())
			temp.add(path);
		for(AbstractPath<Integer> path : temp)
			testpaths.add(path.toString());
	}
	
	public List<String> getMethodTestPath() {
		return testpaths;
	}
	
	public DefUsesDetails getDefUsesDetails() {
		return defusesDetails;
	}
}