package edu.washington.shan;

import edu.washington.shan.news.DBAdapter;
import edu.washington.shan.news.DBConstants;
import edu.washington.shan.news.NewsViewBinder;
import edu.washington.shan.news.PrefKeyManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NewsActivity extends ListActivity {
    
    private static final String TAG="NewsActivity";
    private DBAdapter mDbAdapter;
    private Cursor mCursor;
    private RefreshBroadcastReceiver refreshBroadcastReceiver = new RefreshBroadcastReceiver();
    private NewsViewBinder newsViewBinder = new NewsViewBinder();
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        
        // Display the items to show
        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        fillData();
        
        registerReceiver(refreshBroadcastReceiver, 
                new IntentFilter(Consts.REFRESH_NEWS_VIEW));
    }
    
    @Override
    public void onPause()
    {
        unregisterReceiver(refreshBroadcastReceiver);
        super.onPause();
    }
    
    @Override
    public void onResume()
    {
        registerReceiver(refreshBroadcastReceiver, 
                new IntentFilter(Consts.REFRESH_NEWS_VIEW));
        super.onResume();
    }
    
    @Override
    public void onDestroy()
    {
        mDbAdapter.close();
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        super.onListItemClick(l, v, position, id);
        
        launchSelectedItemInBrowser(id);
    }
    
    /**
     * Given a rowId (_id in the rssentries table in the database)
     * it gets the record, figures out the URL and
     * starts an Intent to open the URL in the browser. 
     * @param rowId
     */
    private void launchSelectedItemInBrowser(long rowId)
    {
        // Using the id get the URL from the db
        Cursor cursor = mDbAdapter.fetchItemsByRowId(rowId);
        startManagingCursor(cursor);
        if(cursor != null)
        {
            int colIndex = cursor.getColumnIndex(DBConstants.URL_NAME);
            String uri = cursor.getString(colIndex);
            if(uri != null && uri.length() > 0)
            {
                // Intent to open a browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        }
    }

    private void fillData() 
    {
        try
        {
            //int topicId = PrefKeyManager.getInstance().keyToValue(mTabTag); 
            //Log.v(TAG, "fillData called for key: " + mTabTag + " topicId: " + topicId);
            
            // Get the rows from the database and create the item list
            mCursor = mDbAdapter.fetchAllItems();
            startManagingCursor(mCursor);
    
            // Create an array to specify the fields we want to display in the list (only TITLE)
            String[] from = new String[]{DBConstants.TITLE_NAME, 
                    DBConstants.URL_NAME,
                    DBConstants.TIME_NAME,
                    DBConstants.STATUS_NAME};
    
            // and an array of the fields we want to bind those fields to
            int[] to = new int[]{R.id.rss_row_text_content, 
                    R.id.rss_row_text_title,
                    R.id.rss_row_text_date,
                    R.id.rss_row_thumbImage};
    
            // Now create a simple cursor adapter and set it to display
            SimpleCursorAdapter adapter = 
                new SimpleCursorAdapter(this, R.layout.news_row, mCursor, from, to);
            adapter.setViewBinder(newsViewBinder);
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
    
    /**
     * The message is to 
     * notify that the RSS data retrieval is complete.
     * As soon as we get the message, update the list.
     */
    protected class RefreshBroadcastReceiver extends BroadcastReceiver 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if(action.equals(Consts.REFRESH_NEWS_VIEW))
            {
                Log.v(TAG, "RefreshBroadcastReceiver for news");
                fillData();
            }
        }
    }
    
}