package edu.washington.shan.news;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.washington.shan.R;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Custom adapter for the list view inside a tab.
 * This adapter is used to format date and URL.
 * @author shan@uw.edu
 *
 */
public class NewsViewBinder implements SimpleCursorAdapter.ViewBinder
{
	//private int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	//private SimpleDateFormat longDateFormat = new SimpleDateFormat("dd MMM yyyy");
	private SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd MMM");
	
	/**
	 * Return value:
	 * true if the data was bound to the view, false otherwise
	 * 
	 * If the returned value is false and the view to bind is 
	 * a TextView, setViewText(TextView, String) is invoked. 
	 * If the returned value is false and the view to bind is 
	 * an ImageView, setViewImage(ImageView, String) is invoked. 
	 * If no appropriate binding can be found, 
	 * an IllegalStateException is thrown.
	 */
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if (view instanceof TextView) {
			setViewText((TextView) view, cursor.getString(columnIndex));
			return true;
		} else if (view instanceof ImageView) {
			int res = R.drawable.star_orange_frame;

			if (0 == cursor.getInt(cursor
					.getColumnIndexOrThrow(DBConstants.STATUS_NAME)))
				res = R.drawable.star_orange_frame;
			else
				res = R.drawable.star_orange_filled;
			((ImageView) view).setImageResource(res);
			return true; // to indicate that the binding occurred
		}
		return false; // binding didn't occur
	}
	
    void setViewText(TextView v, String text) {
    	int id = v.getId();
    	if(id == R.id.rss_row_text_content){
      		v.setText(text);
    	}
    	else if (id == R.id.rss_row_text_date){
    		String dateStr = "";
    		try{
    			long timeInMillisec = Long.parseLong(text);
    			if(timeInMillisec > 0){
    				
    				// Sometimes server returns a negative number so
    				// make sure it's valid.
    				Calendar calendar= Calendar.getInstance();
    				calendar.setTimeInMillis(timeInMillisec);
    				
    				// display the date like this: 15 Dec
    				java.util.Date date = new java.util.Date(timeInMillisec);
    				dateStr = shortDateFormat.format(date);
    			}
    		}
    		catch(NumberFormatException e){
    			// Long.parseLong may throw an exception.
    			// Just swallow it.
    		}
			v.setText(dateStr);
    	}
    	else if(id == R.id.rss_row_text_title){
			v.setText(text);
    	}
    }
}

