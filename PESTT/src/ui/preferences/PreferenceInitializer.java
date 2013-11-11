package ui.preferences;

import main.activator.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		Activator.getDefault().getPreferenceStore();
	}

}
