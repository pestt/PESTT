package domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import domain.constants.GraphCoverageCriteriaId;
import domain.constants.TourType;

public class TestSuiteCatalog {

	private Map<String, FileTestSuite> testSuites;
	private String projectName;
	private String packageName;
	private String className;
	private String methodSignature;

	private class FileTestSuite {
		private String filename;
		private TestSuite testSuite;
		
		private FileTestSuite (String filename, TestSuite testSuite) {
			this.filename = filename;
			this.testSuite = testSuite;
		}
	}
	
	/**
	 * @return The method under test
	 * @requires hasTestSuite(getProjectNameUnderTest())
	 */
	public MethodTest getMethodUnderTest() {
		return testSuites.get(projectName).testSuite.getMethodTest(packageName, className, methodSignature);
	}
	
	public TestSuiteCatalog() {
		testSuites = new HashMap<String, FileTestSuite>();
	}

	public String getProjectNameUnderTest() {
		return projectName;
	}
	public boolean hasTestSuite(String projectName) {
		return testSuites.containsKey(projectName);
	}

	public void addTestSuite(String projectName, String filename, 
			GraphCoverageCriteriaId coverageCriterium, TourType tourType) {

		TestSuite testSuite;

		try {
			testSuite = loadTestSuite(filename);
		} catch (JAXBException|FileNotFoundException e) {
			// in case it cannot parse the XML file or the file does not exist
			testSuite = new TestSuite(coverageCriterium, tourType);
		}				

		testSuites.put(filename, new FileTestSuite(filename, testSuite));
	}

	private TestSuite loadTestSuite(String filename) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(TestSuite.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (TestSuite) unmarshaller.unmarshal(new FileInputStream(filename));
	}
	
	/**
	 * @param projectName The project name whose file is to be flushed
	 * @requires hasTestSuite(projectName)
	 */
	public void flush (String projectName) {
		try {
			FileTestSuite fts = testSuites.get(projectName);
			JAXBContext context = JAXBContext.newInstance(TestSuite.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Writer writer = new FileWriter(fts.filename);
			marshaller.marshal(fts.testSuite, writer);
			writer.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * @param tourType
	 * @requires hasTestSuite(getProjectNameUnderTest())
	 */
	public void setTourType(TourType tourType) {
		if (methodSignature != null) 
			getMethodUnderTest().setTourType(tourType);
	}

	/**
	 * Records information about the method being tested.
	 * It does create the MethodTest object until the method under
	 * test been ask for. 
	 * 
	 * @param projectName The name of the project being tested
	 * @param packageName The name of the package being tested
	 * @param className The name of the class being tested
	 * @param methodSignature The signature of the method being tested
	 */
	public void setMethodUnderTest(String projectName, String packageName,
			String className, String methodSignature) {
		this.projectName = projectName;
		this.packageName = packageName;
		this.className = className;
		this.methodSignature = methodSignature; 
	}

	public void flushCurrentProject() {
		flush(projectName);
	}
}
