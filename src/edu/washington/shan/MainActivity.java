package edu.washington.shan;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.Toast;
import edu.washington.shan.news.Constants;
import edu.washington.shan.news.NewsSyncManager;
import edu.washington.shan.news.PrefKeyManager;
import edu.washington.shan.news.SubscriptionPrefActivity;
import edu.washington.shan.stock.DBConstants;
import edu.washington.shan.stock.StockSyncManager;

/**
 * Main activity hosts the tab view
 * @author shan@uw.edu
 *
 */
public class MainActivity extends TabActivity  implements AsyncTaskCompleteListener<String> {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_SETTINGS = 0;
    private static final int MENU_STOCK_ADDTICKER = 1;
    private static final int MENU_NEWS_SUBSCRIPTION = 2;
    private static final int MENU_NEWS_SEARCH = 3;
    private static final int MENU_NEWS_REFRESH = 4;
    private static final int MENU_STOCK_REFRESH = 5;
    private static final int MENU_STOCK_EDITTICKER = 6;

    public static final String TABTAG_MARKET = "market";
    public static final String TABTAG_STOCK = "stock";
    public static final String TABTAG_NEWS = "news";
    
    private StockSyncManager mStockSyncMan;
    private NewsSyncManager mNewsSyncMan;
    private PrefKeyManager mPrefKeyManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        // Check to see if we're restarting with an instance of stock sync manager.
        // If so, restore the sync manager instance.
        if(null != (mStockSyncMan = (StockSyncManager)getLastNonConfigurationInstance())){
            mStockSyncMan.setContext(this, this);
        }else{
            mStockSyncMan = new StockSyncManager(this, this);
        }
        
        mNewsSyncMan = new NewsSyncManager(this, new Handler(mCallback));
        mPrefKeyManager = PrefKeyManager.getInstance();
        mPrefKeyManager.initialize(
                // be sure to initialize PrefKeyManager before using it
                getResources().getStringArray(R.array.subscriptionoptions_keys));
        
        
        initialize();
        
        // Create an Intent to launch an Activity for the tab (to be reused)
        addTab(TABTAG_MARKET, "Market", 
                new Intent(this, MarketActivity.class),
                R.drawable.ic_tab_dollar);
        addTab(TABTAG_STOCK, "Stock", 
                new Intent(this, StockActivity.class),
                R.drawable.ic_tab_chart);
        addTab(TABTAG_NEWS, "News", 
                new Intent(this, NewsActivity.class),
                R.drawable.ic_tab_news);
    }
    
    /**
     * Add a new tab to TabActivity
     * 
     * @param tag
     * @param caption
     * @param intent
     */
    private void addTab(String tag, String caption, Intent intent, int resourceId) {
        TabHost.TabSpec spec = getTabHost().newTabSpec(tag).setIndicator(
                caption,getResources().getDrawable(resourceId))
                .setContent(intent);
        getTabHost().addTab(spec);
    }

    /**
     * This gets called before onDestroy(). 
     * Pass forward a reference to sync manager which contains async task
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return mStockSyncMan;
    }
    
    /**
     * Gets called when sync manager returns from updating the stock symbols in
     * a thread.
     */
    @Override
    public void onTaskComplete(String result) {
        Log.v(TAG, "onTaskComplete");
        
        // Send a refresh stock view message. Upon receiving
        // the message, the Stock activity will refresh the list view.
        Intent intent = new Intent(Consts.REFRESH_STOCK_VIEW);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
    /** 
     * Check for Network Availability
     * 
     * @return True if network is available
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null, otherwise
        // check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Inflate menu from the menu xml.
     * Additionally add tab-specific menu items.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String tabTag = getTabHost().getCurrentTabTag();
        if(tabTag.equals(TABTAG_MARKET)){
            // Add a tab specific menu
            // Stock and Market use the same function call to refresh 
            menu.add(Menu.NONE, MENU_STOCK_REFRESH, 0, "Refresh").
                setIcon(R.drawable.ic_menu_refresh);
        }else if(tabTag.equals(TABTAG_NEWS)){
            // TODO add to string resource
            menu.add(Menu.NONE, MENU_NEWS_SUBSCRIPTION, 0, "Subscription"). 
                setIcon(R.drawable.ic_menu_pref);
            menu.add(Menu.NONE, MENU_NEWS_SEARCH, 0, "Search").
                setIcon(R.drawable.ic_menu_search);
            menu.add(Menu.NONE, MENU_NEWS_REFRESH, 0, "Refresh").
                setIcon(R.drawable.ic_menu_refresh);
        }else if(tabTag.equals(TABTAG_STOCK)){
            menu.add(Menu.NONE, MENU_STOCK_EDITTICKER, 0, "Edit").
                setIcon(R.drawable.ic_menu_pencil);
            menu.add(Menu.NONE, MENU_STOCK_ADDTICKER, 0, "Add Ticker").
                setIcon(R.drawable.ic_menu_plus);
            menu.add(Menu.NONE, MENU_STOCK_REFRESH, 0, "Refresh").
                setIcon(R.drawable.ic_menu_refresh);
        }
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainmenu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, ACTIVITY_SETTINGS);

        } else if (item.getItemId() == R.id.mainmenu_help) {
            showAboutDialogBox();

        } else if (item.getItemId() == MENU_NEWS_SUBSCRIPTION) {
            Log.v(TAG, "menu id:" + MENU_NEWS_SUBSCRIPTION);
            Intent intent = new Intent(this, SubscriptionPrefActivity.class);
            startActivityForResult(intent, MENU_NEWS_SUBSCRIPTION);
            
        } else if(item.getItemId() == MENU_STOCK_EDITTICKER) {
            Log.v(TAG, "menu id:" + MENU_STOCK_EDITTICKER);
            Intent intent = new Intent(this, StockEditActivity.class);
            startActivityForResult(intent, MENU_STOCK_EDITTICKER);

        } else if (item.getItemId() == MENU_STOCK_ADDTICKER) {
            Log.v(TAG, "menu id:" + MENU_STOCK_ADDTICKER);
            Intent intent = new Intent(this, StockSearchActivity.class);
            startActivityForResult(intent, MENU_STOCK_ADDTICKER);

        } else if (item.getItemId() == MENU_NEWS_SEARCH) {
            Log.v(TAG, "menu id:" + MENU_NEWS_SEARCH);
            Intent intent = new Intent(this, NewsSearchActivity.class);
            startActivityForResult(intent, MENU_NEWS_SEARCH);

        } else if (item.getItemId() == MENU_NEWS_REFRESH) {
            Log.v(TAG, "menu id:" + MENU_NEWS_REFRESH);
            syncNews();
            
        } else if (item.getItemId() == MENU_STOCK_REFRESH) {
            Log.v(TAG, "menu id:" + MENU_STOCK_REFRESH);
            syncStocks();
        }

        // Returning true ensures that the menu event is not be further
        // processed.
        return true;
    }

    /**
     * Handles the activity result such as when returning from
     * Stock search activity, and a new ticker is added.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult " + requestCode);
        
        if(requestCode == MENU_STOCK_ADDTICKER && resultCode == RESULT_OK){
            String symbol = data.getExtras().getString(Consts.NEW_TICKER_ADDED);
            mStockSyncMan.syncForce(new String[]{symbol});
        }else if(requestCode == MENU_NEWS_SUBSCRIPTION){
            // signal the News activity that it needs to refresh the list view
            Intent intent = new Intent(Consts.REFRESH_NEWS_VIEW);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Callback function for the background thread. This is called by the news
     * update thread.
     */
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Log.v(TAG, "Handler.Callback entered");

            Bundle bundle = msg.getData();
            if (bundle != null) {
                boolean overallResult = true;
                boolean[] results = bundle
                        .getBooleanArray(Constants.KEY_STATUS);
                for (boolean result : results) {
                    if (!result) {
                        overallResult = false;
                        break;
                    }
                }

                if (overallResult) {
                    Log.v(TAG, "RSS retrieval succeeded");

                    // Send "refresh" message to the news tab.
                    Intent intent = new Intent(Consts.REFRESH_NEWS_VIEW);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(intent);
                } else {
                    Log.v(TAG, "RSS retrieval failed");
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(
                                    R.string.rss_retrieval_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };
    
    /**
     * Retrieve stock symbols from the db and update ticks 
     */
    private void syncStocks() {
        List<String> symbols = new ArrayList<String>();
        
        // Retrieve the stock symbols...
        edu.washington.shan.stock.DBAdapter dbAdapter = 
            new edu.washington.shan.stock.DBAdapter(this);
        dbAdapter.open();
        Cursor cursor = dbAdapter.fetchAllSymbols();
        if(cursor != null && cursor.getCount() > 0){
            do {
                int index = cursor.getColumnIndex(DBConstants.symbol_NAME);
                symbols.add(cursor.getString(index));
            }while(cursor.moveToNext());
        }
        dbAdapter.close();
        if(symbols.size() > 0)
            mStockSyncMan.syncForce(symbols.toArray(new String[]{}));
    }
    
    private void syncNews() {
        List<String> topics = new ArrayList<String>();
        
        SharedPreferences sharedPref = getSharedPreferences(
                getResources().getString(R.string.pref_filename), 
                MODE_PRIVATE);
        String[] prefs = getResources().getStringArray(R.array.subscriptionoptions_keys);
        for(String pref : prefs) {
            if(sharedPref.getBoolean(pref, false))
                topics.add(pref);
        }
        // pass in the topics of RSS feeds to retrieve
        if(topics.size() > 0)
            mNewsSyncMan.sync(topics.toArray(new String[]{})); 
    }
    
    /**
     * Run first time initialization after the install
     */
    private void initialize() {
        // Use these stock symbols during the first run after install
        final String[] symbols = 
            new String[]{"IBM","MSFT","YHOO","GOOG","AMZN",".DJI",".INX",".IXIC"};

        SharedPreferences sharedPref = getSharedPreferences(
                getResources().getString(R.string.pref_filename), 
                MODE_PRIVATE);
        
        // is this the first time running the app?
        if(!sharedPref.getBoolean("initialized", false)) {
            
            // Create preferences like <boolean name="usmarkets" value="true" />
            // so that we can download RSS news feeds for them.
            String[] prefList = getResources().getStringArray(R.array.subscriptionoptions_keys);
            setPreference(prefList[0], true); // 'US market' 
            setPreference(prefList[1], true); // 'Most Popular' 
            
            // If network is available sync the stock and news
            if(isNetworkAvailable()){
                // Sync the stocks
                mStockSyncMan.syncForce(symbols);
                // Sync the news
                syncNews();
            }
            
            showAboutDialogBox();
            setPreference("initialized", true);// Clear 'first time run' flag
            
        }else{
            
            // delete old rss feeds?
            if(sharedPref.getBoolean(
                    getResources().getString(R.string.settings_rss_feed_save_key), false)){
                edu.washington.shan.news.DBAdapter dbAdapter = 
                    new edu.washington.shan.news.DBAdapter(this);
                dbAdapter.open();
                dbAdapter.deleteItemsOlderThan(Constants.RETENTION_IN_DAYS); // x days
                dbAdapter.close();
            }
            
            // automatically sync?
            if(sharedPref.getBoolean(
                    getResources().getString(R.string.settings_auto_sync_key), false)){
                syncNews();
                syncStocks(); // TODO be sure to sync market graph aswell
            }
        }
    }

    /**
     * Given a preference key and a value it sets the shared preference
     * @param prefKey
     * @param value
     */
    private void setPreference(String prefKey, boolean value) {
        SharedPreferences sharedPref = getSharedPreferences(
                getResources().getString(R.string.pref_filename), 
                MODE_PRIVATE);
        
        Editor editor = sharedPref.edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
    }
    
    /**
     * 
     */
    private void showAboutDialogBox() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View dlgView = inflater.inflate(R.layout.help_dialog_layout, null);

        WebView webview = (WebView) dlgView.findViewById(R.id.help_dialog_layout_webView1);
        webview.loadUrl("file:///android_asset/readme.html");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.help_dialog_title));
        builder.setView(dlgView);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }
}