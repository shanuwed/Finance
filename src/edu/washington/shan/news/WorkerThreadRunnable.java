/**
 * 
 */
package edu.washington.shan.news;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author shan@uw.edu
 *
 */
public class WorkerThreadRunnable implements Runnable {
	
	private static final String TAG = "WorkerThreadRunnable";
	private Handler mHandler;
	private Context mContext; 
	private String[] mTopics;
	
	/**
	 * Worker thread constructor
	 * @param context Context under which to create DbAdapter and manage cursor
	 * @param handler Callback function if the caller wants to be notified when worker thread is complete. May be null.
	 * @param topics Topics for which to retrieve RSS feeds
	 */
	public WorkerThreadRunnable(Context context, Handler handler, String[] topics)
	{
		// To enable the assertion do either of these:
		// 1) adb shell setprop debug.assert 1
		// 2) Send the command line argument "--enable-assert" to the dalvik VM
		assert mContext != null;
		assert topics != null && topics.length != 0;
		
		mContext = context;
		mHandler = handler;
		mTopics = topics;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		int index = 0;
		boolean[] results = new boolean[mTopics.length];
		for (index = 0; index < mTopics.length; index++)
			results[index] = false;

		SubscriptionManager subscription = new SubscriptionManager(mContext);
		if (subscription.checkConnection()) {
			for (index = 0; index < mTopics.length; index++) {
				Log.v(TAG, "requesting RSS feed for:" + mTopics[index]);
				results[index] = subscription.getRssFeed(mTopics[index]);
				// Even if one of them fails continue to process all.
			}
		}
		informFinish(results);
	}
	
	/**
	 * Notify the caller of the worker thread completion
	 * @param results
	 */
	public void informFinish(boolean[] results)
	{
		Log.v(TAG, "Finished retreiving RSS feeds");
		
		// Return the status
		Bundle bundle = new Bundle();
		bundle.putBooleanArray(Constants.KEY_STATUS, results);
		bundle.putStringArray(Constants.KEY_TAB_TAG, mTopics);
		
		if(mHandler != null)
		{
			Message msg = mHandler.obtainMessage();
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

}
