package domain.controllers;

import java.util.Observable;

import domain.TestSuiteCatalog;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;

public class TestSuiteController extends Observable {

	private TestSuiteCatalog testSuiteCatalog;

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
	
}
