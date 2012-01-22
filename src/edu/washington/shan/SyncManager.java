/**
 * 
 */
package edu.washington.shan;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.washington.shan.stock.DownloadStockTask;
import edu.washington.shan.util.UIUtilities;

/**
 * @author shan@uw.edu
 *
 */
public class SyncManager {
    
    private static final String TAG="SyncManager";
    private final long mIntervalInMillisec = 240000L; // 4 min
    private Context mContext;
    private Map<String, SyncRecord> mRecords ;
    private DownloadStockTask mDownloadTask;
    private AsyncTaskCompleteListener<String> mCallback;
    
    public SyncManager(Context context, AsyncTaskCompleteListener<String> callback){
        mContext = context;
        mCallback = callback;
        mRecords = new Hashtable<String, SyncRecord>();
    }
    
    public void sync(String key){
        Log.v(TAG, "sync for " + key);
        if(mRecords.containsKey(key)){
            SyncRecord r = mRecords.get(key);
            if(false == r.getStatus()){
                // if the last sync status "failed" try to sync again
                downloadStockData();
            }else{
                // the last sync status was "successful"
                // check how long it's been since the last successful sync
                long last = r.getWhen();
                long now = Calendar.getInstance().getTimeInMillis();
                if(now - last > mIntervalInMillisec){
                    downloadStockData();
                }
            }
        }else{
            mRecords.put(key, new SyncRecord(key));
            // First time
            downloadStockData();
        }
    }
    
    private void downloadStockData() {
        Log.v(TAG, "downloadStockData");
        if (mDownloadTask == null
                || mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mDownloadTask = new DownloadStockTask(mContext, mCallback);
            mDownloadTask.execute("AAPL");
        } else {
            UIUtilities.showToast(mContext,
                    R.string.error_already_in_progress);
        }
    }
    
    private class SyncRecord{
        
        private String mKey;
        private Object mLock = new Object();
        private long mWhen;
        private boolean mStatus;
        
        public SyncRecord(String key){
            synchronized(mLock){
                mKey = key;
                mStatus = true; // TODO status is always true for now - need to change
                markTime();
            }
        }
        public boolean getStatus() {
            synchronized(mLock){
                return mStatus;
            }
        }
        public void markTime(){
            synchronized(mLock){
                Calendar c = Calendar.getInstance();
                mWhen = c.getTimeInMillis();
            }
        }
        public void setStatus(boolean status) {
            synchronized (mLock) {
                mStatus = status;
            }
        }
        public long getWhen(){
            synchronized(mLock){
                return mWhen;
            }
        }
    }
}
