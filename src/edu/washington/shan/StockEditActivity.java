/**
 * 
 */
package edu.washington.shan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.washington.shan.stock.DBAdapter;
import edu.washington.shan.stock.DBConstants;

/**
 * Stock edit activity allows a user to remove a stock item
 * @author shan@uw.edu
 */
public class StockEditActivity extends ListActivity {

    private static final String TAG = "StockEditActivity";

    private DBAdapter mDbAdapter;
    private Button mRemoveButton;
    private Button mDoneButton;
    private List<Map<String, Object>> mRows = new ArrayList<Map<String, Object>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_edit);

        // Wire buttons
        mRemoveButton = (Button) findViewById(R.id.stock_edit_button1);
        mRemoveButton.setOnClickListener(mRemoveButtonClickListener);
        mDoneButton = (Button) findViewById(R.id.stock_edit_button2);
        mDoneButton.setOnClickListener(mDoneButtonClickListener);

        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        getData();
        populateListView();
    }

    @Override
    protected void onDestroy() {
        mDbAdapter.close();
        super.onDestroy();
    }
    
    protected void populateListView(){
        // Populate the list view
        String[] from = new String[] { DBConstants.symbol_NAME,
                DBConstants.company_NAME };

        // and an array of the fields we want to bind those fields to
        int[] to = new int[] { R.id.stock_edit_row_text1,
                R.id.stock_edit_row_text2 };
        
        setListAdapter(new SimpleAdapter(this, mRows,
                R.layout.stock_edit_row, from, to));
        getListView().setTextFilterEnabled(true);
    }

    protected void getData() {
        try {
            // Get the mRows from the database and create the item list
            Cursor cursor = mDbAdapter.fetchAllItems();
            startManagingCursor(cursor);

            do {
                Map<String, Object> row = new HashMap<String, Object>();
                int columnIndex = cursor
                        .getColumnIndexOrThrow(DBConstants.symbol_NAME);
                row.put(DBConstants.symbol_NAME, 
                        cursor.getString(columnIndex));
                columnIndex = cursor
                        .getColumnIndexOrThrow(DBConstants.company_NAME);
                row.put(DBConstants.company_NAME, 
                        cursor.getString(columnIndex));
                mRows.add(row);
            } while (cursor.moveToNext());

        } catch (java.lang.IllegalStateException e) {
            Log.e(TAG, "Exception caught in getData", e);
        } catch (java.lang.RuntimeException e) {
            Log.e(TAG, "Exception caught in getData", e);
        }
    }

    private OnClickListener mRemoveButtonClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            //Log.v(TAG, "getChildCount: " + getListView().getChildCount());
            int max = getListView().getChildCount();
            for(int index=max-1; index >= 0; index--){
                View childView = getListView().getChildAt(index);
                CheckBox checkBox = (CheckBox)childView.findViewById(R.id.stock_edit_row_checkBox1);
                if(checkBox != null && checkBox.isChecked()){
                    TextView textView = (TextView)childView.findViewById(R.id.stock_edit_row_text1);
                    mDbAdapter.removeItemsBySymbols(new String[] {textView.getText().toString()});
                    //Log.v(TAG, "removing item " + textView.getText());
                    mRows.remove(index);
                }
            }
            populateListView();
        }
    };

    private OnClickListener mDoneButtonClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
