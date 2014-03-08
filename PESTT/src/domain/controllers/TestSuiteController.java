package domain.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import domain.MethodTest;
import domain.TestSuite;
import domain.constants.CoverageCriteriaId;
import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;

public class TestSuiteController extends Observable {

	private Map<String, TestSuite> testSuites;
	private String filename;
	private MethodTest methodUnderTest;
	private TestPathController testPathController;

	public TestSuiteController() {
		testSuites = new HashMap<String, TestSuite>();
	}
	
	/**
	 * @param projectName
	 * @param packageName
	 * @param className
	 * @param methodSignature
	 * @requires hasTestSuite(projectName) 
	 */
	public void setMethodUnderTest(String projectName, String packageName, String className, String methodSignature) {
		methodUnderTest = testSuites.get(packageName).getMethodTest(packageName, className, methodSignature);
		if (methodUnderTest == null) {
			methodUnderTest = testSuites.get(packageName).addMethodTest(packageName, className, methodSignature,
					testPathController.getSelectedTourType());
			flush();	
		}
		
	}

	public boolean hasTestSuite(String projectName) {
		return testSuites.containsKey(projectName);
	}
	
	public void setTestSuite(String projectName, String filename, String coverageCriterium, String tourType) {
		TestSuite testSuite;
		try {
			testSuite = loadTestSuite(filename);
		} catch (JAXBException|FileNotFoundException e) {
			// in case it cannot parse the XML file or the file does not exist
			//TODO: fmartins: adjust to work with LogicCriteria when they appear.
			testSuite = new TestSuite(GraphCoverageCriteriaId.valueOf(coverageCriterium), TourType.valueOf(tourType));
		}				

		testSuites.put(filename, loadTestSuite(filename));
//		filename = "/Users/fmartins/Documents/eclipse-workspaces/projects/runtime-EclipseApplication/testePESTT/default.xml";
	}

	
	public void setTestPathController (TestPathController testPathController) {
		this.testPathController = testPathController;
	}
	
	public MethodTest getMethodUnderTest() {
		return methodUnderTest;
	}

	private TestSuite loadTestSuite(String filename) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(TestSuite.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (TestSuite) unmarshaller.unmarshal(new FileInputStream(filename));
	}
	
	public void flush () {
		try {
			JAXBContext context = JAXBContext.newInstance(TestSuite.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Writer writer = new FileWriter(filename);
			marshaller.marshal(testSuite, writer);
			writer.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	
	/**
	 * Sets the tour type for the method under test.
	 * 
	 * @param selectedTourType The selected tour type
	 */
	public void setTourType(TourType tourType) {
		if (methodUnderTest != null) {
			methodUnderTest.setTourType(tourType);
			flush();
		}
	}	
	
}
