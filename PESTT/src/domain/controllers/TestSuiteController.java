package domain.controllers;

import java.util.Observable;

import domain.TestSuiteCatalog;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;

public class TestSuiteController extends Observable {

	private TestSuiteCatalog testSuiteCatalog;
	private String projectName;
	private String packageName;
	private String className;
	private String methodSignature;

	public TestSuiteController(TestSuiteCatalog testSuiteCatalog) {
		this.testSuiteCatalog = testSuiteCatalog;
	}
	
	/**
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @param methodSignature
	 * @requires hasTestSuite(projectName) 
	 */
	public void setMethodUnderTest(String projectName, String packageName, 
			String className, String methodSignature) {
		this.projectName = projectName;
		this.packageName = packageName;
		this.className = className;
		this.methodSignature = methodSignature;
		testSuiteCatalog.setMethodUnderTest(projectName, packageName, className, methodSignature);
	}
		
	public boolean hasTestSuite(String projectName) {
		return testSuiteCatalog.hasTestSuite(projectName);
	}
	
	/**
	 * Adds a test suite based on structural coverage criteria
	 * 
	 * @param projectName The project name
	 * @param filename The XML filename to serialise test data
	 * @param coverageCriterium The structural coverage criterium
	 * @param tourType The tour type
	 */
	public void addTestSuite(String projectName, String filename, 
			String coverageCriterium, String tourType) {
		testSuiteCatalog.addTestSuite(projectName, filename, 
				GraphCoverageCriteriaId.valueOf(coverageCriterium), 
				TourType.valueOf(tourType));
	}
	
	/**
	 * Sets the tour type for the method under test.
	 * 
	 * @param selectedTourType The selected tour type
	 * @requires hasTestSuite(projectName)
	 */
	public void setTourType(TourType tourType) {
	}	
	
}
