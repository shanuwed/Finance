package edu.washington.shan;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity  implements AsyncTaskCompleteListener<String> {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_SETTINGS = 0;
    private static final int MENU_ADDTICKER = 1;
    private static final int MENU_SUBSCRIPTION = 2;
    private static final int MENU_SEARCH = 3;
    private static final int MENU_REFRESH = 4;

    private SyncManager mStockSyncMan;
    private AtomicBoolean mStockNewDataAvailable; // lock-free thread-safe boolean

    // TODO get the symbols dynamically...
    private static final String[] symbols = 
        new String[]{"IBM","MSFT","YHOO","GOOG","AMZN"};
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
    	mStockNewDataAvailable = new AtomicBoolean(false);
    
    	// Create an Intent to launch an Activity for the tab (to be reused)
    	addTab(Consts.TABTAB_MARKET, "Market", new Intent().setClass(this, MarketActivity.class));
    	addTab(Consts.TABTAB_STOCK, "Stock", new Intent().setClass(this, StockActivity.class));
    	addTab(Consts.TABTAB_NEWS, "News", new Intent().setClass(this, NewsActivity.class));
	
        // Check to see if we're restarting with a sync manager.
        // If so, restore the sync manager instance.
        if(null != (mStockSyncMan = (SyncManager)getLastNonConfigurationInstance())){
            mStockSyncMan.setContext(this, this);
        }else{
            mStockSyncMan = new SyncManager(this, this);
            mStockSyncMan.sync(symbols); // TODO DEBUG ONLY
        }
        
    	// TODO Check network connectivity
    	//...
    	
        // When a tab changes check to see if new RSS feeds are available for
        // the tab. Then sends a broadcast message to refresh the tab.
        getTabHost().setOnTabChangedListener(mOnTabChangeListener);
    }
    
    private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener() {

        @Override
        public void onTabChanged(String tabId) {
            // tabId == tabTag
            if (tabId.equals(Consts.TABTAB_STOCK)) {

                if (mStockNewDataAvailable.get()) {
                    Log.v(TAG, "sending a broadcast to stock tab");
                    // Send a refresh stock view message. Upon receiving
                    // the message, the Stock activity will refresh the list
                    // view.
                    Intent intent = new Intent(Consts.REFRESH_STOCK_VIEW);
                    sendBroadcast(intent);
                    mStockNewDataAvailable.set(false);
                }
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
        sendBroadcast(intent);
        
        // Broadcast message is received only if the Stock activity is active.
        // If it's not currently active we need to force it to refresh its 
        // list when it becomes active.
        mStockNewDataAvailable.compareAndSet(false, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMeListnu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String tabTag = getTabHost().getCurrentTabTag();
        if(tabTag.equals(Consts.TABTAB_MARKET)){
            // Add a tab specific menu if needed
        }else if(tabTag.equals(Consts.TABTAB_NEWS)){
            // TODO add to string resource
            menu.add(Menu.NONE, MENU_SUBSCRIPTION, 0, "Subscription"). 
                setIcon(R.drawable.ic_menu_pref);
            menu.add(Menu.NONE, MENU_SEARCH, 0, "Search").
                setIcon(R.drawable.ic_menu_search);
            menu.add(Menu.NONE, MENU_REFRESH, 0, "Refresh").
                setIcon(R.drawable.ic_menu_refresh);
        }else if(tabTag.equals(Consts.TABTAB_STOCK)){
            menu.add(Menu.NONE, MENU_ADDTICKER, 0, "Add Ticker").
                setIcon(R.drawable.ic_menu_plus);
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
            
        } else if (item.getItemId() == MENU_SUBSCRIPTION) {
            
            Log.v(TAG, "menu id:" + MENU_SUBSCRIPTION);
            
        } else if (item.getItemId() == MENU_ADDTICKER) {
            
            Log.v(TAG, "menu id:" + MENU_ADDTICKER);
            Intent intent = new Intent(this, StockSearchActivity.class);
            startActivityForResult(intent, MENU_ADDTICKER);
            
        } else if (item.getItemId() == MENU_SEARCH) {
            
            Log.v(TAG, "menu id:" + MENU_SEARCH);
            
        } else if (item.getItemId() == MENU_REFRESH) {
            
            Log.v(TAG, "menu id:" + MENU_REFRESH);
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
        
        if(requestCode == MENU_ADDTICKER && resultCode == RESULT_OK){
            String symbol = data.getExtras().getString(Consts.NEW_TICKER_ADDED);
            // TODO get the symbol user wants to add
            mStockSyncMan.syncForce(symbol);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}