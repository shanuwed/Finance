/**
 * 
 */
package edu.washington.shan.news;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author shan@uw.edu
 *
 */
public class NewsSyncManager {
	
	private static final String TAG = "NewsSyncManager";
	
	/**
	 * Use a synchronized map to allow concurrent access from 
	 * the UI activity as well as from a worker thread. 
	 * Keys  Tab tag
	 * Values Whether new feeds are available for the tab identified by the key 
	 */
	private Map<String, Boolean> mSyncStatusMap = 
		Collections.synchronizedMap(new HashMap<String, Boolean>());
	
	/**
	 * Worker thread to retreive RSS feeds
	 */
    private Thread mWorkerThread;
    
    /**
     * Use the context of the main activity
     */
    private Context mContext;
    
    /**
     * Handler to the client callback.
     * Must be passed in from the caller that wants to be 
     * notified when worker thread returns.
     */
    private Handler mClientHandler;
    
    /**
     * Handler to a private callback function
     * that gets invoked when worker thread returns.
     */
    private Handler mPrivateHandler;  

	
	/**
	 * 
	 * @param context Context with which to create DbAdapter (eventually at SubscriptionManager level). Must not be null. 
	 * @param handler Callback handler to call when worker thread returns. May be null.
	 */
    public NewsSyncManager(Context context, Handler handler)
	{
    	if(context == null)
    		throw new NullPointerException();
    	
		mContext = context;
		mClientHandler = handler;
		mPrivateHandler = new Handler(mPrivateCallback);
	}
	
	public void sync(String[] tabTags)
	{
        // Start a worker thread to sync.
        // The worker thread retreives the latest RSS feeds from server.
        // If there are new items, it adds them to the db.
        // Then the thread signals back to the caller that
        // new items are available. The caller sends out a
        // broadcast message. Unon receiving a broadcast message
        // a tab (RssActivity) refreshes its list view.
        mWorkerThread = new Thread(new WorkerThreadRunnable(mContext, mPrivateHandler, tabTags));
        mWorkerThread.start();
	}
	
	private Handler.Callback mPrivateCallback = new Handler.Callback() 
	{
		@Override
		public boolean handleMessage(Message msg) {
			Log.v(TAG, "Handler.Callback entered");
			
			Bundle bundle = msg.getData();
			boolean[] results  = bundle.getBooleanArray(Constants.KEY_STATUS);
			String[] tabTags = bundle.getStringArray(Constants.KEY_TAB_TAG);
			
			synchronized(mSyncStatusMap)
			{
				mSyncStatusMap.clear();
				for(int index=0; index< results.length; index++)
				{
					// Store the tag and the result
					mSyncStatusMap.put(tabTags[index], results[index]);
				}
			}
			
			if(mClientHandler != null)
			{
				Message newMsg = mClientHandler.obtainMessage();
				newMsg.copyFrom(msg);
				mClientHandler.sendMessage(newMsg);
			}
			
			return false;
		}
	};
	
	/**
	 * Checks to see if new data is available
	 * @param key Tab tag
	 * @return True is new data is available
	 */
	public boolean isNewDataAvailable(String key)
	{
		boolean ret = false;
		synchronized(mSyncStatusMap)
		{
			if(mSyncStatusMap.containsKey(key))
			{
				ret = mSyncStatusMap.get(key);
			}
		}
		return ret;
	}
	
	/**
	 * Clears New Data Available flag
	 * @param key Tag tag
	 */
	public void clearNewDataAvailableFlag(String key)
	{
		synchronized(mSyncStatusMap)
		{
			if(mSyncStatusMap.containsKey(key))
			{
				mSyncStatusMap.put(key, false);
			}
		}
	}
}
