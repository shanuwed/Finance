/**
 * 
 */
package edu.washington.shan;

import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.washington.shan.stock.DownloadStockTask;

/**
 * @author shan@uw.edu
 *
 */
public class SyncManager implements AsyncTaskCompleteListener<String> {
    
    private static final String TAG="SyncManager";
    
    private final long mIntervalInMillisec = 240000L; // 4 min
    private Context mContext;
    private AsyncTaskCompleteListener<String> mCallback;
    private boolean mDownloadStatus; // true = successful download
    private long mLastSyncedInMillisec;
    private DownloadStockTask mDownloadTask;
    
    public SyncManager(Context context, AsyncTaskCompleteListener<String> callback){
        mContext = context;
        mCallback = callback;
        mDownloadStatus = false; // initialize to false to force sync first time around
    }
    

    public void setContext(Context context, 
            AsyncTaskCompleteListener<String> callback){
        mContext = context;
        mCallback = callback;
    }
    
    /**
     * It syncs for all given stock symbols
     * @param keys
     */
    public void sync(String... keys){
        Log.v(TAG, "attempting to sync");
        
        if(!mDownloadStatus){
            // if the last sync status "failed" try to sync again
            downloadStockData(keys);
        }else{
            // the last sync status was "successful"
            // check how long it's been since the last successful sync
            long now = Calendar.getInstance().getTimeInMillis();
            if(now - mLastSyncedInMillisec > mIntervalInMillisec){
                downloadStockData(keys);
            }else{
                Log.v(TAG, "won't sync because it hasn't been long enough since the last sync");
            }
        }
    }
    
    public void syncForce(String symbol){
        downloadStockData(new String[]{symbol});
    }
    
    private void downloadStockData(String... keys) {
        Log.v(TAG, "downloadStockData");
        
        if (mDownloadTask == null
                || mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mDownloadTask = new DownloadStockTask(mContext, this);
            mDownloadTask.execute(keys);
        } else {
            // TODO need to rethink the logic here...
            // what does it mean if mDownloadTask != null. Can we restart the same async task?
            //UIUtilities.showToast(mContext,
            //        R.string.error_already_in_progress);
            Log.v(TAG, "download is already in progress");
        }
    }

    @Override
    public void onTaskComplete(String result) {
        Log.v(TAG, "onTaskComplete");
        
        // If async task finished successfully 
        // update the last sync time and status. 
        if(result.equals("success")){
            mLastSyncedInMillisec = Calendar.getInstance().getTimeInMillis();
            mDownloadStatus = true;
        }else{
            mDownloadStatus = false;
        }
        
        // Notify the client that it's finished.
        if(mCallback != null){
            mCallback.onTaskComplete(result);
        }
    }
}
