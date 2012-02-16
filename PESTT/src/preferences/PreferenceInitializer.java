package preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import activator.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		Activator.getDefault().getPreferenceStore();
	}

}
