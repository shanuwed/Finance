/**
 * 
 */
package edu.washington.shan.news;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.washington.shan.R;
import edu.washington.shan.util.UIUtilities;

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
	 * Worker thread to retreive news
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
	}
	
    public void sync(String[] topics) {
        // Start a worker thread to sync.
        // The worker thread retreives the latest RSS feeds from server.
        // If there are new items, it adds them to the db.
        // Then the thread signals back to the caller that
        // new items are available. The caller sends out a
        // broadcast message. Unon receiving a broadcast message,
        // a tab refreshes its list view.
        if (null == mWorkerThread
                || mWorkerThread.getState() == Thread.State.TERMINATED) {
            mWorkerThread = new Thread(new WorkerThreadRunnable(mContext,
                    mClientHandler, topics));
            mWorkerThread.start();
        } else {
            Log.v(TAG, "download is already in progress");
            UIUtilities.showToast(mContext,
                    R.string.error_already_in_progress);
        }
    }
/*	
	private Handler.Callback mPrivateCallback = new Handler.Callback() 
	{
		@Override
		public boolean handleMessage(Message msg) {
			Log.v(TAG, "Handler.Callback entered");
			
			Bundle bundle = msg.getData();
			boolean[] results  = bundle.getBooleanArray(Constants.KEY_STATUS);
			String[] tabTags = bundle.getStringArray(Constants.KEY_TAB_TAG);
			
            for (int index = 0; index < results.length; index++) {
                Log.v(TAG, "result for " + tabTags[index] + " is " + results[index]);
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
*/}
