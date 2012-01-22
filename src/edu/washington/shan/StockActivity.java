/**
 * 
 */
package edu.washington.shan;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);
        
        // Immediately display the list from the database
        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        fillData();

        // Starts to update- since this is an async task,
        // it may take a few seconds to finish.
        mSyncMan = new SyncManager(this, this);
        mSyncMan.sync(TAG);
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
        mSyncMan.sync(TAG);
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        mDbAdapter.close();
        super.onDestroy();
    }
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTaskComplete(String result) {
        // TODO Auto-generated method stub
        Log.v(TAG, "onTaskComplete");
        fillData();
    }

    private void fillData(){
        //StockAdapter mAdapter = new StockAdapter(this);
        //this.setListAdapter(mAdapter);

        try
        {
                // Get the rows from the database and create the item list
                Cursor cursor = mDbAdapter.fetchItemsBySymbol("AAPL");
                startManagingCursor(cursor);
        
                // Create an array to specify the fields we want to display in the list (only TITLE)
                String[] from = new String[]{DBConstants.symbol_NAME};
        
                // and an array of the fields we want to bind those fields to
                int[] to = new int[]{R.id.stock_row_text1};
        
                SimpleCursorAdapter adapter = 
                    new SimpleCursorAdapter(this, R.layout.stock_row, cursor, from, to);
                //adapter.setViewBinder(customViewBinder);
                setListAdapter(adapter);
        }
        catch(java.lang.IllegalStateException e)
        {
            Log.e(TAG, "Exception in fillData", e);
        }
        catch(java.lang.RuntimeException e)
        {
            Log.e(TAG, "Exception in fillData", e);
        }

    }
    
    

    /*
     * protected List getData(String prefix) { List<Map> myData = new
     * ArrayList<Map>();
     * 
     * 
     * for (int i = 0; i < 5; i++) { Map<String, Object> entries = new
     * HashMap<String, Object>(); entries.put("title", prefix + i);
     * entries.put("intent", (i%2)==0); myData.add(entries); }
     * 
     * return myData; }
     */

    class StockAdapter extends BaseAdapter {
        private String[] symbols = { "MSFT", "IBM", "BAC", "FNF", "AAPL" };
        private LayoutInflater mInflater;

        public StockAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return symbols.length;
        }

        @Override
        public String getItem(int position) {
            return symbols[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View v = convertView;
            if ((v == null) || v.getTag() == null) {
                v = mInflater.inflate(R.layout.stock_row, null);
                holder = new ViewHolder();
                holder.mTitle = (TextView) v.findViewById(R.id.stock_row_text1);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            String item = getItem(position);
            holder.mTitle.setText(item);
            v.setTag(holder);
            return v;
        }

        class ViewHolder {
            TextView mTitle;
        }

    }

}
