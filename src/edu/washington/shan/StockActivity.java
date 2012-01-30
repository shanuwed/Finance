/**
 * 
 */
package edu.washington.shan;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import edu.washington.shan.stock.StockViewBinder;
import edu.washington.shan.stock.DBAdapter;
import edu.washington.shan.stock.DBConstants;

/**
 * Handles the Stock tab
 * 
 */
public class StockActivity extends ListActivity {
    
    private static final String TAG = "StockActivity";
    private DBAdapter mDbAdapter;
    private StockViewBinder mCustomViewBinder;
    private RefreshBroadcastReceiver refreshBroadcastReceiver;
    
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);
        
        mCustomViewBinder = new StockViewBinder(this);
        refreshBroadcastReceiver = new RefreshBroadcastReceiver();
        
        // Display the list from the database
        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        fillData();
        
        LocalBroadcastManager.getInstance(this).
        registerReceiver(refreshBroadcastReceiver, 
                new IntentFilter(Consts.REFRESH_STOCK_VIEW));
    }
    
    /**
     * Clean up the adapter
     */
    @Override
    public void onDestroy()
    {
        mDbAdapter.close();
        super.onDestroy();
    }

    /**
     * Populate the list view from the data in db
     */
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
    
    /**
     * Upon receiving a broadcast message,
     * refresh the list view.
     */
    public class RefreshBroadcastReceiver extends BroadcastReceiver 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if(action.equals(Consts.REFRESH_STOCK_VIEW))
            {
                Log.v(TAG, "RefreshBroadcastReceiver for stocks");
                fillData();
            }
        }
    }
}
