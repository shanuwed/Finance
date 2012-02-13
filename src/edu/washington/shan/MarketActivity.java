package edu.washington.shan;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import edu.washington.shan.stock.DBAdapter;
import edu.washington.shan.stock.DBConstants;
import edu.washington.shan.stock.StockViewBinder;
import edu.washington.shan.util.UIUtilities;

/**
 * Activity for the Market tab
 * @author shan@uw.edu
*/
public class MarketActivity extends ListActivity {
    
    private static final String TAG = "MarketActivity";
    private RefreshBroadcastReceiver refreshBroadcastReceiver;
    private DBAdapter mDbAdapter;
    private ImageDownloadTask mDownloadTask;
    private Map<String, Object> mConfigInstance; // to use onRetainNonConfigurationInstance
    private final String DBADAPTER_KEY = "dbadapter";
    private final String DOWNLOADTASK_KEY = "downloadtask";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market);
        
        if(null != (mConfigInstance = (Map<String, Object>) getLastNonConfigurationInstance ())){
            // Restore the adapter
            mDbAdapter = (DBAdapter)mConfigInstance.get(DBADAPTER_KEY);
            mDbAdapter.open();
            
            // Restore the download task
            mDownloadTask = (ImageDownloadTask) mConfigInstance.get(DOWNLOADTASK_KEY);
            mDownloadTask.setContext(this); // give it a new context reference
            if(mDownloadTask.getStatus() == AsyncTask.Status.FINISHED )
                mDownloadTask.setImageInView();
        }else{
            // Always use the application context instead of activity context.
            mDbAdapter = new DBAdapter(getApplicationContext());
            mDbAdapter.open();
            
            mDownloadTask = new ImageDownloadTask(this);
            mDownloadTask
                .execute("http://www.google.com/finance/chart?cht=c&q=INDEXDJX:.DJI,INDEXSP:.INX,INDEXNASDAQ:.IXIC&tlf=12h");
        }
        fillData();
        
        
        refreshBroadcastReceiver = new RefreshBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).
        registerReceiver(refreshBroadcastReceiver, 
                new IntentFilter(Consts.REFRESH_STOCK_VIEW));
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
        mConfigInstance = new Hashtable<String, Object>();
        mConfigInstance.put(DBADAPTER_KEY, mDbAdapter);
        mConfigInstance.put(DOWNLOADTASK_KEY, mDownloadTask);
        return mConfigInstance;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        browse(id);
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
            browse(info.id);
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
        // Using the id get the symbol from the db
        Cursor cursor = mDbAdapter.fetchItemsByRowId(id);
        startManagingCursor(cursor);
        if (cursor != null) {
            int colIndex = cursor.getColumnIndex(DBConstants.symbol_NAME);
            String symbol = cursor.getString(colIndex);
            if (symbol != null && symbol.length() > 0) {
                return Consts.STOCK_URL + symbol;
            }
        }
        return "http://www.google.com/finance"; // default
    }
    
    private void browse(long id){
        // Using the id get the symbol from the db
        Cursor cursor = mDbAdapter.fetchItemsByRowId(id);
        startManagingCursor(cursor);
        if (cursor != null) {
            int colIndex = cursor.getColumnIndex(DBConstants.symbol_NAME);
            String symbol = cursor.getString(colIndex);
            if (symbol != null && symbol.length() > 0) {
                UIUtilities.browse(this, symbol, Consts.STOCK_URL + symbol);
            }else{
                Log.e(TAG, "symbol retreived from the database is invalid");
            }
        }
    }
     
    /**
     * Populate the list view from the data in db
     */
    private void fillData() {
        try {
            // Get the rows from the database and create the item list
            Cursor cursor = mDbAdapter.fetchMarketIndexes();
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
            StockViewBinder stockViewBinder = new StockViewBinder(this);
            adapter.setViewBinder(stockViewBinder);
            setListAdapter(adapter);
        } catch (java.lang.IllegalStateException e) {
            Log.e(TAG, "Exception in fillData", e);
        } catch (java.lang.RuntimeException e) {
            Log.e(TAG, "Exception in fillData", e);
        }
    }
    
    private void refreshImage() {
        if (mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mDownloadTask = new ImageDownloadTask(this);
            mDownloadTask
                    .execute("http://www.google.com/finance/chart?cht=c&q=INDEXDJX:.DJI,INDEXSP:.INX,INDEXNASDAQ:.IXIC&tlf=12h");
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
                Log.v(TAG, "RefreshBroadcastReceiver for market");
                fillData();
                refreshImage();
            }
        }
    }
    
    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        private static final String TAG = "ImageDownloadTask";

        private Context mContext;
        private Bitmap mBitmap;

        public ImageDownloadTask(Context context) {
            mContext = context;
            mBitmap = null;
        }

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                String url = args[0];
                HttpUriRequest request = new HttpGet(url.toString());
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    byte[] bytes = EntityUtils.toByteArray(entity);

                    mBitmap = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    return mBitmap;
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage(),e); 
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(),e);
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                ImageView imageView = (ImageView) ((Activity) mContext)
                        .findViewById(R.id.market_imageView1);
                imageView.setImageBitmap(mBitmap);
            }
            ProgressBar progressBar = (ProgressBar) ((Activity) mContext)
                .findViewById(R.id.market_progressBar1);
            progressBar.setVisibility(ProgressBar.GONE);
        }
        
        public void setImageInView() {
            if (mBitmap != null) {
                ImageView imageView = (ImageView) ((Activity) mContext)
                        .findViewById(R.id.market_imageView1);
                imageView.setImageBitmap(mBitmap);
            }
            ProgressBar progressBar = (ProgressBar) ((Activity) mContext)
                    .findViewById(R.id.market_progressBar1);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }
}
