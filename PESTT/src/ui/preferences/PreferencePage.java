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
		setDescription(ConstantLabels.DESCRIPTION);
	}

	public void createFieldEditors() {
		addField(new FileFieldEditor(ConstantLabels.DOT_PATH,
				ConstantLabels.DOT_PATH_LABEL, getFieldEditorParent()));
		//TODO: fmartins: construct these options from a constants class. One for all UI.
		addField(new RadioGroupFieldEditor("tourType",
		        "Default graph tour type:", 3,
		        new String[][] { { "&Tour", "TOUR" },
		            { "&Sidetrip", "SIDETRIP" }, { "&Detour", "DETOUR" } }, getFieldEditorParent()));
		addField(new RadioGroupFieldEditor("graphCoverageCriterium",
		        "Default graph coverage criterium:", 3,
		        new String[][] { { "&Node Coverage", "NODE" },
		            { "&Edge Coverage", "EDGE" }, { "E&dge Pair Coverage", "EDGE_PAIR" },
		            { "&Prime Path Coverage", "PRIME_PATH" }, { "&Complete Roundtrip Coverage", "COMPLETE_ROUND_TRIP" },
		            { "&Simple Roundtrip Coverage", "SIMPLE_ROUND_TRIP" },
		            { "&Complete Path Coverage", "COMPLETE_PATH" }, { "All-&Defs Coverage", "ALL_DEFS_COVERAGE" }, 
		            { "All-&Uses Coverage", "ALL_USES" }, { "&All-du-Paths Coverage", "ALL_DU_PATHS" }},
		            getFieldEditorParent()));
		StringFieldEditor sfe = new StringFieldEditor("defaultFilename",
				"Default XML test filename:", getFieldEditorParent());
		sfe.setEmptyStringAllowed(false);
		addField(sfe);
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
