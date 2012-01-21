/**
 * 
 */
package edu.washington.shan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author shan@uw.edu
 * 
 */
public class StockActivity extends ListActivity {
    private static final String TAG = "MainActivity";
    private MyAdapter mAdapter;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.stock);

	mAdapter = new MyAdapter(this);
	this.setListAdapter(mAdapter);
	
       /*
	setListAdapter(new SimpleAdapter(this, getData("some data "),
                android.R.layout.simple_list_item_1, 
                new String[] { "title" }, // from
                new int[] { android.R.id.text1 })); // to
                */
    }

    /*
    protected List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();


        for (int i = 0; i < 5; i++) {
            Map<String, Object> entries = new HashMap<String, Object>();
            entries.put("title", prefix + i);
            entries.put("intent", (i%2)==0);
            myData.add(entries);
        }

        return myData;
    }*/

    class MyAdapter extends BaseAdapter {
	private String[] symbols = { "MSFT", "IBM", "BAC", "FNF", "AAPL" };
	private LayoutInflater mInflater;

	public MyAdapter(Context context) {
	    mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
	    return symbols.length;
	}

	@Override
	public String getItem(int position) {
	    return symbols[position];
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    final ViewHolder holder;
	    View v = convertView;
	    if ((v == null) || v.getTag() == null) {
		v = mInflater.inflate(R.layout.stock_row, null);
		holder = new ViewHolder();
		holder.mTitle = (TextView) v.findViewById(R.id.stock_row_text1);
		v.setTag(holder);
	    } else {
		holder = (ViewHolder) v.getTag();
	    }

	    String item = getItem(position);
	    holder.mTitle.setText(item);
	    v.setTag(holder);
	    return v;
	}

	class ViewHolder {
	    TextView mTitle;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
	// TODO Auto-generated method stub
	super.onSaveInstanceState(outState);
    }

}
