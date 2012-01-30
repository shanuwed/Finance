/**
 * 
 */
package edu.washington.shan.stock;

import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.washington.shan.AsyncTaskCompleteListener;
import edu.washington.shan.R;
import edu.washington.shan.util.UIUtilities;

/**
 * @author shan@uw.edu
 *
 */
public class StockSyncManager implements AsyncTaskCompleteListener<String> {
    
    private static final String TAG="StockSyncManager";
    
    private final long mIntervalInMillisec = 240000L; // 4 min
    private Context mContext;
    private AsyncTaskCompleteListener<String> mCallback;
    private boolean mDownloadStatus; // true = successful download
    private long mLastSyncedInMillisec;
    private DownloadStockTask mDownloadTask;
    
    public StockSyncManager(Context context, AsyncTaskCompleteListener<String> callback){
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
     * @param symbols
     */
    public void sync(String... symbols){
        Log.v(TAG, "attempting to sync");
        
        if(!mDownloadStatus){
            // if the last sync status "failed" try to sync again
            downloadStockData(symbols);
        }else{
            // the last sync status was "successful"
            // check how long it's been since the last successful sync
            long now = Calendar.getInstance().getTimeInMillis();
            if(now - mLastSyncedInMillisec > mIntervalInMillisec){
                downloadStockData(symbols);
            }else{
                Log.v(TAG, "won't sync because it hasn't been long enough since the last sync");
            }
        }
    }
    
    public void syncForce(String... symbols){
        downloadStockData(symbols);
    }
    
    private void downloadStockData(String... symbols) {
        Log.v(TAG, "downloadStockData");
        
        if (mDownloadTask == null
                || mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mDownloadTask = new DownloadStockTask(mContext, this);
            mDownloadTask.execute(symbols);
        } else {
            Log.v(TAG, "download is already in progress");
            UIUtilities.showToast(mContext,
                    R.string.error_already_in_progress);
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
