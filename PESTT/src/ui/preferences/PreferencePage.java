package ui.preferences;

import java.net.URL;

import main.activator.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		if (getPreferenceStore().getString("defaultFilename") == "")
			getPreferenceStore().setValue("defaultFilename", "default.xml");
		setDescription(ConstantLabels.DESCRIPTION);
	}

	public void createFieldEditors() {
		addField(new FileFieldEditor(ConstantLabels.DOT_PATH,
				ConstantLabels.DOT_PATH_LABEL, getFieldEditorParent()));
		//TODO: fmartins: construct these options from a constants class. One for all UI.
		addField(new RadioGroupFieldEditor("tourType",
		        "Default structural tour type:", 3,
		        new String[][] { { "&Tour", "TOUR" },
		            { "&Sidetrip", "SIDETRIP" }, { "&Detour", "DETOUR" } }, getFieldEditorParent()));
		addField(new RadioGroupFieldEditor("structuralCoverageCriterium",
		        "Default structural coverage criterium:", 3,
		        new String[][] { { "&Node Coverage", "NODE_COVERAGE" },
		            { "&Edge Coverage", "EDGE_COVERAGE" }, { "E&dge Pair Coverage", "EDGE_PAIR_COVERAGE" },
		            { "&Prime Path Coverage", "PRIME_PATH_COVERAGE" }, { "&Complete Roundtrip Coverage", "COMPLETE_ROUNDTRIP_COVERAGE" },
		            { "&Simple Roundtrip Coverage", "SIMPLE_ROUNDTRIP_COVERAGE" },
		            { "&Complete Path Coverage", "COMPLETE_PAIR_COVERAGE" }, { "All-&Defs Coverage", "ALL_DEFS_COVERAGE" }, 
		            { "All-&Uses Coverage", "ALL_USES_COVERAGE" }, { "&All-du-Paths Coverage", "ALL_DU_PATHS_COVERAGE" }},
		            getFieldEditorParent()));
		addField(new StringFieldEditor("defaultFilename",
				"Default XML test filename:", getFieldEditorParent()));
		Link link = new Link(getFieldEditorParent(), SWT.NONE);
		link.setText(ConstantLabels.SUPPORT_PAGE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(new URL(e.text));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
