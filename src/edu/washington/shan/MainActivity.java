package edu.washington.shan;

import java.util.concurrent.atomic.AtomicBoolean;

import edu.washington.shan.news.Constants;
import edu.washington.shan.news.NewsSyncManager;
import edu.washington.shan.news.PrefKeyManager;
import edu.washington.shan.news.SubscriptionPrefActivity;
import edu.washington.shan.stock.StockSyncManager;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity  implements AsyncTaskCompleteListener<String> {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_SETTINGS = 0;
    private static final int MENU_STOCK_ADDTICKER = 1;
    private static final int MENU_NEWS_SUBSCRIPTION = 2;
    private static final int MENU_NEWS_SEARCH = 3;
    private static final int MENU_NEWS_REFRESH = 4;
    private static final int MENU_STOCK_REFRESH = 5;

    public static final String TABTAG_MARKET = "market";
    public static final String TABTAG_STOCK = "stock";
    public static final String TABTAG_NEWS = "news";
    
    private StockSyncManager mStockSyncMan;
    private NewsSyncManager mNewsSyncMan;
    private PrefKeyManager mPrefKeyManager;
    private Handler mHandler;

    // TODO get the symbols dynamically...
    private static final String[] symbols = 
        new String[]{"IBM","MSFT","YHOO","GOOG","AMZN",".DJI",".INX",".IXIC"};
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        // Check to see if we're restarting with a sync manager.
        // If so, restore the sync manager instance.
        if(null != (mStockSyncMan = (StockSyncManager)getLastNonConfigurationInstance())){
            mStockSyncMan.setContext(this, this);
        }else{
            mStockSyncMan = new StockSyncManager(this, this);
            mStockSyncMan.sync(symbols); // TODO DEBUG ONLY
        }
        
        
        // TODO need to wrap in getLastNonConfigurationInstance()...
        mHandler = new Handler(mCallback);
        mPrefKeyManager = PrefKeyManager.getInstance();
        mNewsSyncMan = new NewsSyncManager(this, mHandler);
        mPrefKeyManager.initialize(this); // be sure to initialize before using it
        
        
        //addTabsBasedOnPreferences();
        //cleanupOldFeeds();
        //clearFirstTimeRunFlag();
        //syncAtStartup();
        
        
    	// TODO Check network connectivity
    	//...
    	
        // Create an Intent to launch an Activity for the tab (to be reused)
        addTab(TABTAG_MARKET, "Market", 
                new Intent().setClass(this, MarketActivity.class));
        addTab(TABTAG_STOCK, "Stock", 
                new Intent().setClass(this, StockActivity.class));
        addTab(TABTAG_NEWS, "News", 
                new Intent().setClass(this, NewsActivity.class));
    
        // When a tab changes check to see if new RSS feeds are available for
        // the tab. Then sends a broadcast message to refresh the tab.
        getTabHost().setOnTabChangedListener(mOnTabChangeListener);
    }
    
    // TODO REMOVE??
    private TabHost.OnTabChangeListener mOnTabChangeListener = 
        new TabHost.OnTabChangeListener() {

        @Override
        public void onTabChanged(String tabId) {
            // tabId == tabTag
            if (tabId.equals(TABTAG_STOCK)) {

            } else if (tabId.equals(TABTAG_NEWS)) {

            }
        }
    };
            
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
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
     * Add a new tab to TabActivity
     * 
     * @param tag
     * @param caption
     * @param intent
     */
    private void addTab(String tag, String caption, Intent intent) {
        TabHost.TabSpec spec = getTabHost().newTabSpec(tag).setIndicator(
                createTabIndicator(this, caption)).setContent(intent);
        getTabHost().addTab(spec);
    }

    /**
     * To customize see http://joshclemm.com/blog/?p=136 and
     * http://androidworkz.
     * com/2011/02/04/custom-menu-bar-tabs-how-to-hook-the-menu
     * -button-to-showhide-a-custom-tab-bar/
     * 
     * @param context
     * @param caption
     * @return
     */
    private static View createTabIndicator(final Context context,
            final String caption) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.tab_indicator, null);
        TextView v = (TextView) view.findViewById(R.id.tab_indicator_text);
        v.setText(caption);
        return view;
    }

    /**
     * Inflate menu from the menu xml.
     * Additionally add tab-specific menu items.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String tabTag = getTabHost().getCurrentTabTag();
        if(tabTag.equals(TABTAG_MARKET)){
            // Add a tab specific menu if needed
        }else if(tabTag.equals(TABTAG_NEWS)){
            // TODO add to string resource
            menu.add(Menu.NONE, MENU_NEWS_SUBSCRIPTION, 0, "Subscription"). 
                setIcon(R.drawable.ic_menu_pref);
            menu.add(Menu.NONE, MENU_NEWS_SEARCH, 0, "Search").
                setIcon(R.drawable.ic_menu_search);
            menu.add(Menu.NONE, MENU_NEWS_REFRESH, 0, "Refresh").
                setIcon(R.drawable.ic_menu_refresh);
        }else if(tabTag.equals(TABTAG_STOCK)){
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
            // Utils.showAboutDialogBox(this);

        } else if (item.getItemId() == MENU_NEWS_SUBSCRIPTION) {
            Log.v(TAG, "menu id:" + MENU_NEWS_SUBSCRIPTION);
            Intent intent = new Intent(this, SubscriptionPrefActivity.class);
            startActivityForResult(intent, MENU_NEWS_SUBSCRIPTION);

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
            // TODO get the symbol user wants to add
            mStockSyncMan.syncForce(new String[]{symbol});
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
    
    private void syncNews() {
        String[] tabTags = new String[]{"usmarkets"}; 
        mNewsSyncMan.sync(tabTags);
    }
    
    private void syncStocks(){
        // TODO how does it work if there's another thread already working...??
        mStockSyncMan.syncForce(symbols); // TODO DEBUG ONLY
    }
}