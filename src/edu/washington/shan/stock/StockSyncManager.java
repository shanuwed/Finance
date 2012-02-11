/**
 * 
 */
package edu.washington.shan.stock;

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
    
    private Context mContext;
    private AsyncTaskCompleteListener<String> mCallback;
    private DownloadStockTask mDownloadTask;
    
    public StockSyncManager(Context context, AsyncTaskCompleteListener<String> callback){
        mContext = context;
        mCallback = callback;
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
        
        Log.v(TAG, "downloadStockData");
        
        if (mDownloadTask == null
                || mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mDownloadTask = new DownloadStockTask(mContext, this);
            mDownloadTask.execute(symbols);
        } else {
            Log.v(TAG, "stocks download is already in progress");
            UIUtilities.showToast(mContext,
                    R.string.error_already_in_progress);
        }
    }
    
    @Override
    public void onTaskComplete(String result) {
        Log.v(TAG, "onTaskComplete");
        
        // If async task finished successfully 
        // Notify the client that it's finished.
        if(mCallback != null){
            mCallback.onTaskComplete(result);
        }
    }
}
