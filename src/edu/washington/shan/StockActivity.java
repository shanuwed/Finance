/**
 * 
 */
package edu.washington.shan;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import edu.washington.shan.stock.DBAdapter;
import edu.washington.shan.stock.DBConstants;

/**
 * @author shan@uw.edu
 * 
 */
public class StockActivity extends ListActivity implements AsyncTaskCompleteListener<String> {
    
    private static final String TAG = "StockActivity";
    private SyncManager mSyncMan;
    private DBAdapter mDbAdapter;
    private CustomViewBinder mCustomViewBinder;
    
    // TODO get the symbols dynamically...
    private static final String[] symbols = 
        new String[]{"IBM","MSFT","YHOO","GOOG","AMZN","DIA","TEVA","FNF","VAR"};

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);
        
        mCustomViewBinder = new CustomViewBinder(this);
        
        // Immediately display the list from the database
        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        fillData();

        // Check to see if we're restarting with a sync manager.
        // If so, restore the sync manager instance.
        if(null != (mSyncMan = (SyncManager)getLastNonConfigurationInstance())){
            mSyncMan.setContext(this, this);
        }else{
            mSyncMan = new SyncManager(this, this);
            mSyncMan.sync(symbols); // TODO DEBUG ONLY
        }
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        mDbAdapter.close();
        super.onDestroy();
    }

    /* 
     * This gets called before onDestroy(). 
     * Pass forward a reference to sync manager which contains async task
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return mSyncMan;
    }

    @Override
    public void onTaskComplete(String result) {
        Log.v(TAG, "onTaskComplete");
        
        // BUGBUG if you try to update the UI here it throws
        // java.lang.IllegalStateException: attempt to acquire a reference on a close SQLiteClosable
        // TODO need a better way to update the UI 
        //fillData(); 
    }

    private void fillData() {
        try {
            // Get the rows from the database and create the item list
            Cursor cursor = mDbAdapter.fetchAllItems();
            startManagingCursor(cursor);

            // Create an array to specify the fields we want to display in the
            // list (only TITLE)
            String[] from = new String[] { 
                DBConstants.symbol_NAME,
                DBConstants.last_NAME,
                DBConstants.change_NAME,
                DBConstants.perc_change_NAME,
                DBConstants.company_NAME
                };

            // and an array of the fields we want to bind those fields to
            int[] to = new int[] { 
                R.id.stock_row_text1,
                R.id.stock_row_text2,
                R.id.stock_row_text3,
                R.id.stock_row_text4,
                R.id.stock_row_text_company_name
                };

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.stock_row, cursor, from, to);
            adapter.setViewBinder(mCustomViewBinder);
            setListAdapter(adapter);
        } catch (java.lang.IllegalStateException e) {
            Log.e(TAG, "Exception in fillData", e);
        } catch (java.lang.RuntimeException e) {
            Log.e(TAG, "Exception in fillData", e);
        }

    }    
}
