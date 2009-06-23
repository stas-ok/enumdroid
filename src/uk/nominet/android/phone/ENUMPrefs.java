package uk.nominet.android.phone;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class ENUMPrefs extends PreferenceActivity implements Preference.OnPreferenceClickListener {

	/* Exported constants */
	static public final String ENUM_PREF_ENABLE = "enum_enable";
	static public final String ENUM_PREF_CUSTOM = "enum_custom";
	static public final String ENUM_PREF_SUFFIX = "enum_suffix";
	static public final String ENUM_PREF_MOBILE = "enum_mobile";
	
	/* Member variables */
	private CheckBoxPreference mEnumEnablePref;
	private CheckBoxPreference mEnumMobilePref;
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		/* clicking either of these two preferences can change the notification icon status */
		PreferenceScreen prefSet = getPreferenceScreen();
		mEnumEnablePref = (CheckBoxPreference)prefSet.findPreference(ENUM_PREF_ENABLE);
		mEnumEnablePref.setOnPreferenceClickListener(this);
		
		mEnumMobilePref = (CheckBoxPreference)prefSet.findPreference(ENUM_PREF_MOBILE);
		mEnumMobilePref.setOnPreferenceClickListener(this);
	}

	protected void setReceiverEnabled(boolean enabled) {
		int state = enabled ?
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		PackageManager pm = getPackageManager();
		ComponentName cn = new ComponentName(getApplicationContext(), ENUMReceiver.class);
		pm.setComponentEnabledSetting(cn, state, PackageManager.DONT_KILL_APP);
	}
	
	public boolean onPreferenceClick(Preference preference) {
		
		/* (de)activate the BroadcastReceiver as required */
		boolean enabled = mEnumEnablePref.isChecked();
		setReceiverEnabled(enabled);

		/* and update the status notification icon */
		ENUMUtil.updateNotification(getApplicationContext());
		
		return true;
	}
}
