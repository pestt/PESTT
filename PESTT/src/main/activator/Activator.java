package main.activator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ui.controllers.CFGController;
import ui.controllers.EditorController;
import ui.controllers.StatisticsController;
import ui.controllers.ViewController;
import domain.controllers.BytemanController;
import domain.controllers.CoverageDataController;
import domain.controllers.DefUsesController;
import domain.controllers.SourceGraphController;
import domain.controllers.TestPathController;
import domain.controllers.TestRequirementController;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "PESTT";
	
	// The shared instance
	private static Activator plugin;
	private PESTT pestt;
	
	/**
	 * The constructor
	 */
	public Activator() {
		pestt = new PESTT();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	 /** 
     * Returns an image descriptor for the image file at the given 
     * plug-in relative path 
     * 
     * @param path the path 
     * @return the image descriptor 
     */  
    public static ImageDescriptor getImageDescriptor(String path) {  
        return imageDescriptorFromPlugin(PLUGIN_ID, path);  
    } 
    
    public SourceGraphController getSourceGraphController() {
		return pestt.getSourceGraphController();
	}
    
    public TestRequirementController getTestRequirementController() {
		return pestt.getTestRequirementController();
	}
    
    public TestPathController getTestPathController() {
		return pestt.getTestPathController();
	}
    
    public CoverageDataController getCoverageDataController() {
		return pestt.getCoverageDataController();
	}
    
    public StatisticsController getStatisticsController() {
		return pestt.getStatisticsController();
	}
    
    public EditorController getEditorController() {
		return pestt.getEditorController();
	}

	public CFGController getCFGController() {
		return pestt.getCFGController();
	}

	public DefUsesController getDefUsesController() {
		return pestt.getDefUsesController();
	}
	
	public BytemanController getBytemanController() {
		return pestt.getBytemanController();
	}
	
	public ViewController getViewController() {
		return pestt.getViewController();
	}
}