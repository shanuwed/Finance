/**
 * 
 */
package edu.washington.shan.news;

import edu.washington.shan.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author shan@uw.edu
 *
 */
public class SubscriptionPrefActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.subscriptionoptions);
	}
}
