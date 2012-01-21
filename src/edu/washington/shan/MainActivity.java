package edu.washington.shan;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_SETTINGS = 0;
    private Thread mWorkerThread;
    private Handler mHandler;
    private ProgressBar mProgressBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	//Resources res = getResources(); // Resource object to get Drawables
	
	// Create an Intent to launch an Activity for the tab (to be reused)
	addTab("market", "Market", new Intent().setClass(this, MarketActivity.class));
	addTab("stock", "Stock", new Intent().setClass(this, StockActivity.class));
	addTab("news", "News", new Intent().setClass(this, NewsActivity.class));
    }
    
    /**
     * Add a new tab to TabActivity
     * @param tag
     * @param caption
     * @param intent
     */
    private void addTab(String tag, String caption, Intent intent){
	TabHost.TabSpec spec = getTabHost().newTabSpec(tag).setIndicator(createTabIndicator(this, caption)).setContent(intent);
	getTabHost().addTab(spec);
    }
    
    /**
     * To customize see http://joshclemm.com/blog/?p=136 and
     * http://androidworkz.com/2011/02/04/custom-menu-bar-tabs-how-to-hook-the-menu-button-to-showhide-a-custom-tab-bar/
     * @param context
     * @param caption
     * @return
     */
    private static View createTabIndicator(final Context context, final String caption) {
    	View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
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
	}

	// Returning true ensures that the menu event is not be further
	// processed.
	return true;
    }
}