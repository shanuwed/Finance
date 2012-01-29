/**
 * 
 */
package edu.washington.shan.news;

import java.util.HashMap;
import java.util.Map;

import edu.washington.shan.R;

import android.content.Context;
import android.content.res.Resources;

/**
 * @author shan@uw.edu
 * 
 * Singleton class to manage preference keys
 * such as:
 *    SavingsLoans
 * The preference key coincides with the 
 * RSS URL that you append to RSS_BASE_URI
 * to build a full RSS URL.
 * 
 * This class is to convert a preference key to
 * an integer that can be stored in database, and vice versa.
 * Integer in both Maps is stored as 'topicId' in the db.
 *
 */
public class PrefKeyManager 
{
	private static PrefKeyManager instance = new PrefKeyManager();
	private Map<String, Integer> keyToValueMap = new HashMap<String, Integer>();
	private Map<Integer, String> valueToKeyMap = new HashMap<Integer, String>();
	
	public static PrefKeyManager getInstance()
	{
		return instance;
	}
	
	// Private ctor
	private PrefKeyManager()
	{
	}
	
	// Client must call this to initialize
	public void initialize(Context context)
	{
		Resources resources = context.getResources();
		
		// Get the keys from the resource to check each preference
		String[] optionKeys = resources.getStringArray(R.array.subscriptionoptions_keys);
		int value = 0;
		for(String key : optionKeys)
		{
			keyToValueMap.put(key, value);
			valueToKeyMap.put(value, key);
			value++;
		}
	}
	
	// Returns an index for the key.
	// Returns -1 if key is not found.
	public int keyToValue(String key)
	{
		int value = -1;
		if(keyToValueMap.containsKey(key))
		{
			value = keyToValueMap.get(key);
		}
		return value;
	}
	
	// Returns a key for a value- reverse lookup
	// Returns an empty string if value is not found
	public String ValueToKey(int value)
	{
		String key = "";
		if(valueToKeyMap.containsKey(value))
		{
			key = valueToKeyMap.get(value);
		}
		return key;
	}
}
