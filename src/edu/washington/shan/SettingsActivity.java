/**
 * 
 */
package edu.washington.shan;

import java.util.regex.Pattern;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author shan@uw.edu
 *
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key)
    {
        if (key.equals("settings_zipcode")) {
            /*
            // Search for a valid pattern
            // If it's not numbers or length != 5 force it to the default value
            // settings_zipcode_defaultvalue
            String pattern = "[0-9][0-9][0-9][0-9][0-9]";
            String value = sharedPreferences.getString(key, null);
            if (!Pattern.matches(pattern, value)) {
                // The value is not a valid zip code.
                // TODO change the value
            }
            */
        }            
    }

}
