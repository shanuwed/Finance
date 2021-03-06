package edu.washington.shan;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import edu.washington.shan.news.DBAdapter;
import edu.washington.shan.news.DBConstants;
import edu.washington.shan.news.NewsViewBinder;
import edu.washington.shan.news.PrefKeyManager;
import edu.washington.shan.util.UIUtilities;

/**
 * Activity for the News tab
 * @author shan@uw.edu
 *
 */
public class NewsActivity extends ListActivity {

    private static final String TAG = "NewsActivity";
    private RefreshBroadcastReceiver refreshBroadcastReceiver;
    private DBAdapter mDbAdapter;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);

        if(null != (mDbAdapter = (DBAdapter) getLastNonConfigurationInstance ())){
            mDbAdapter.open();
        }else{
            // Always use the application context instead of activity context.
            mDbAdapter = new DBAdapter(getApplicationContext());
            mDbAdapter.open();
        }
        fillData();

        refreshBroadcastReceiver = new RefreshBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
        refreshBroadcastReceiver,
                new IntentFilter(Consts.REFRESH_NEWS_VIEW));
        registerForContextMenu(getListView());
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        mDbAdapter.close();
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return mDbAdapter;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String url = getUrl(id);
        UIUtilities.browse(this, url, url);
    }

    /**
     * Inflate context menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu, menu);
    }

    /**
     * Handle context menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.contextmenu_browse:
            String url = getUrl(info.id);
            UIUtilities.browse(this, "News", url);
            return true;

        case R.id.contextmenu_share:
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    getUrl(info.id));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Returns an URL associated with an item if it's valid. Returns a default
     * URL otherwise.
     * 
     * @param id
     * @return
     */
    private String getUrl(long id) {
        // Using the id get the URL from the db
        Cursor cursor = mDbAdapter.fetchItemsByRowId(id);
        startManagingCursor(cursor);
        if (cursor != null) {
            int colIndex = cursor.getColumnIndex(DBConstants.URL_NAME);
            String url = cursor.getString(colIndex);
            if (url != null && url.length() > 0) {
                return url;
            }
        }
        return "http://www.google.com/finance"; // default
    }

    /**
     * Populate the list view
     */
    private void fillData() {
        try {
            // Get the rows from the database and create the item list
            Cursor mCursor = mDbAdapter.fetchItemsByTopicIds(getSelectedTopicIds());
            startManagingCursor(mCursor);

            // Specify the fields we want to display in the list
            String[] from = new String[] { DBConstants.TITLE_NAME,
                    DBConstants.URL_NAME, DBConstants.TIME_NAME,
                    DBConstants.STATUS_NAME };

            // Specify the fields we want to bind
            int[] to = new int[] { R.id.rss_row_text_content,
                    R.id.rss_row_text_title, R.id.rss_row_text_date,
                    R.id.rss_row_thumbImage };

            // Now create a simple cursor adapter and set it to display
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.news_row, mCursor, from, to);
            adapter.setViewBinder(new NewsViewBinder());
            setListAdapter(adapter);
        } catch (java.lang.IllegalStateException e) {
            Log.e(TAG, "Exception in fillData", e);
        } catch (java.lang.RuntimeException e) {
            Log.e(TAG, "Exception in fillData", e);
        }
    }

    /**
     * Returns the topics selected by the user in the preferences
     * @return
     */
    private Long[] getSelectedTopicIds() {
        List<Long> topics = new ArrayList<Long>();
        
        SharedPreferences sharedPref = getSharedPreferences(
                getResources().getString(R.string.pref_filename), 
                MODE_PRIVATE);
        String[] prefs = getResources().getStringArray(R.array.subscriptionoptions_keys);
        for(String pref : prefs) {
            if(sharedPref.getBoolean(pref, false))
                topics.add((long)PrefKeyManager.getInstance().keyToValue(pref));
        }
        return topics.toArray(new Long[]{});
    }

    /**
     * The message is to notify that the RSS data retrieval is complete. As soon
     * as we get the message, update the list.
     */
    protected class RefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Consts.REFRESH_NEWS_VIEW)) {
                Log.v(TAG, "RefreshBroadcastReceiver for news");
                fillData();
            }
        }
    }

}