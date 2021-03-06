package domain.tests.instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import main.activator.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ui.constants.Messages;
import domain.constants.Byteman;

public class JUnitLaunch extends JUnitLaunchShortcut {
	
	private static final String PLUGIN_ID = Activator.getDefault().getBundle().getSymbolicName();
	private static final String PLUGIN_VERSION = Activator.getDefault().getBundle().getVersion().toString();
	private static final String PLUGIN_JAR = PLUGIN_ID + "_" + PLUGIN_VERSION + ".jar";
	private static final String BYTEMAN_JAR = "lib" + IPath.SEPARATOR + "byteman.jar";

	@Override
	public void launch(IEditorPart editor, String mode) {
		Activator.getDefault().getBytemanController().createScripts();
		Activator.getDefault().getBytemanController().addListener();
		Activator.getDefault().getTestPathController().clearAutomaticTestPaths();
		super.launch(editor, mode);		
		IEditorPart part = Activator.getDefault().getEditorController().getEditorPart();
		Activator.getDefault().getViewController().bringEditorToTop(part);
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType("org.eclipse.jdt.junit.launchconfig");
		try {
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			if(configurations.length != 0) {
				ILaunchConfiguration configuration = configurations[configurations.length - 1];
				IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
				IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath, IRuntimeClasspathEntry.STANDARD_CLASSES);			
				IRuntimeClasspathEntry projectEntry = JavaRuntime.newDefaultProjectClasspathEntry(Activator.getDefault().getEditorController().getJavaProject()); 
				projectEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);					
				IPath pluginPath = JavaCore.getClasspathVariable("ECLIPSE_HOME").append("plugins").append(PLUGIN_JAR);
				IRuntimeClasspathEntry pluginEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(pluginPath);
				pluginEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);				
				List<String> classpath = new ArrayList<String>();
				classpath.add(systemLibsEntry.getMemento());
				classpath.add(projectEntry.getMemento());
				classpath.add(pluginEntry.getMemento());
				ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);				
				String bytemanPath = extractJar(pluginPath);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-javaagent:" + bytemanPath + "=script:" + Byteman.SCRIPT_DIR + IPath.SEPARATOR + Byteman.SCRIPT_FILE);
				configuration = workingCopy.doSave();
			}
		} catch (CoreException e) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			MessageDialog.openInformation(window.getShell(), Messages.PREFERENCES_TITLE, Messages.PREFERENCES_TOOLS_MSG);
			return;
		}
	}

	private String extractJar(IPath pluginPath) {
		File file = null;
		try {
			JarFile jar = new JarFile(pluginPath.toOSString());
			for(Enumeration<JarEntry> enumEntry = jar.entries(); enumEntry.hasMoreElements();) {
				JarEntry entry = enumEntry.nextElement();
				if(entry.getName().equals(BYTEMAN_JAR)) {
					file = new File(System.getProperty("java.io.tmpdir") + IPath.SEPARATOR + "byteman.jar");
					InputStream is = jar.getInputStream(entry);
					FileOutputStream fos = new FileOutputStream(file);
					while (is.available() > 0) {
						fos.write(is.read());
						fos.flush();
					}
					fos.close();
					is.close();
				}
			}
			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}	
}