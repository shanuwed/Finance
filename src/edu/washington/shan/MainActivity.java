package edu.washington.shan;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    
    private static final String TAG="MainActivity";
    private static final int ACTIVITY_SETTINGS = 0;
    private Thread mWorkerThread;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, MarketActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("market").setIndicator("Market",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, StockActivity.class);
        spec = tabHost.newTabSpec("stock").setIndicator("Stock",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, NewsActivity.class);
        spec = tabHost.newTabSpec("news").setIndicator("News",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        if(item.getItemId() == R.id.mainmenu_settings)
        {
            // Launch to SettingsPrefActivity screen
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, ACTIVITY_SETTINGS);
        }
        else if(item.getItemId() == R.id.mainmenu_help)
        {
            //Utils.showAboutDialogBox(this);
        }
        
        // Returning true ensures that the menu event is not be further processed.
        return true;
    }
}