/**
 * 
 */
package edu.washington.shan;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import edu.washington.shan.stock.StockViewBinder;
import edu.washington.shan.stock.DBAdapter;
import edu.washington.shan.stock.DBConstants;

/**
 * @author shan@uw.edu
 *
 */
public class StockSearchActivity extends ListActivity {

    private static final String TAG="StockSearchActivity";
    
    //private DBAdapter mDbAdapter;
    private EditText mEditText;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_search);
        
        // Wire edittext
        mEditText = (EditText)findViewById(R.id.search_edittext);
        
        //mDbAdapter = new DBAdapter(this);
        //mDbAdapter.open();
    }
    
   /**
     * Handle the button click
     * @param v
     */
    public void onButtonClick(View v)
    {
        Log.v(TAG, "onButtonClick");
        
        Intent intent = getIntent();
        intent.putExtra(Consts.NEW_TICKER_ADDED, mEditText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}