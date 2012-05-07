package ui.preferences;

import java.net.URL;

import main.activator.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import ui.constants.Preferences;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Preferences.DESCRIPTION);
	}

	public void createFieldEditors() {
		addField(new FileFieldEditor(Preferences.DOT_PATH, Preferences.DOT_PATH_LABEL, getFieldEditorParent()));
		
		Link link = new Link(getFieldEditorParent(), SWT.NONE);
		link.setText(Preferences.SUPPORT_PAGE);
		link.addSelectionListener(new SelectionAdapter(){
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	        	try {
	        		PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
	              } 
	             catch(Exception ex) {
	                 ex.printStackTrace();
	            } 
	        }
	    });
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
