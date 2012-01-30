/**
 * 
 */
package edu.washington.shan;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author shan@uw.edu
 * 
 */
public abstract class SyncManagerBase {
    private static final String TAG = "SyncManagerBase";

    private static final String sharedPrefFilename = "settings_finance_syncmanager";
    private static final String prefKey = "stockLastSyncedAt";
    private final long mIntervalInMillisec;
    private Context mContext;

    public SyncManagerBase(Context context, long intervalInMillisec) {
        mContext = context;
        mIntervalInMillisec = intervalInMillisec;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    // TODO remove if not needed
    protected void syncAtStartup() {
        Log.v(TAG, "Entering syncAtStartup");
        
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                sharedPrefFilename, Context.MODE_PRIVATE);

        if (timeCheck()
                && sharedPref.getBoolean(mContext.getResources().getString(
                        R.string.settings_auto_sync_key), false)) {
            // sync();
        }
    }

    /**
     * Returns true if it's been x since last sync or 
     * if it's the first time this function is called. 
     * 
     * @return
     */
    protected boolean timeCheck() {
        Log.v(TAG, "timeCheck");

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                sharedPrefFilename, Context.MODE_PRIVATE);
        long lastSyncedAt = sharedPref.getLong(prefKey, -1);
        if (lastSyncedAt == -1)
            return true; // if the preference is not set it means it's first
                         // time.

        long offset = mIntervalInMillisec; // minutes -> milliseconds
        long now = Calendar.getInstance().getTimeInMillis();
        if (lastSyncedAt + offset <= now)
            return true;

        return false;
    }

    /**
     * Writes a timestamp for 'last synced time' in a shared preference
     */
    protected void markSyncTime() {
        Log.v(TAG, "markSyncTime");
        
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                sharedPrefFilename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(prefKey, Calendar.getInstance().getTimeInMillis());
        editor.commit();
    }

}
