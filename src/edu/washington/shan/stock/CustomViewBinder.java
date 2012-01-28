package edu.washington.shan.stock;

import edu.washington.shan.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class CustomViewBinder implements ViewBinder {
    
    private final Context mContext;
    
    public CustomViewBinder(Context context){
        mContext = context;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (view instanceof TextView) {
            final String text = cursor.getString(columnIndex);
            int id = view.getId();
            if (id == R.id.stock_row_text1) {
                ((TextView) view).setText(text);
            } else if (id == R.id.stock_row_text2) {
                ((TextView) view).setText(text);
            } else if (id == R.id.stock_row_text3) {
                setText(view, text, text);
            } else if (id == R.id.stock_row_text4) {
                setText(view, text, "(" + text + "%)");
            } else if(id == R.id.stock_row_text_company_name){
                ((TextView) view).setText(text);
                ((TextView) view).
                    setTextColor(mContext.getResources().getColor(R.color.grey));
            }
            return true;
        }
        return false; // binding didn't occur
    }
    
    /**
     * If text < 0 the text shows in red. Otherwise the text shows in green.
     * @param view
     * @param text
     */
    private void setText(View view, String text, String formattedText){
        try {
            float val = Float.parseFloat(text);
            if (val > 0) {
                ((TextView) view).setText(formattedText);
                ((TextView) view).
                    setTextColor(mContext.getResources().getColor(R.color.green));
            } else if (val < 0) {
                ((TextView) view).setText(formattedText);
                ((TextView) view).
                    setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                ((TextView) view).setText(formattedText);
                ((TextView) view).
                    setTextColor(mContext.getResources().getColor(R.color.green));
            }
        } catch (NumberFormatException e) {
            ((TextView) view).setText("n/a");
        }
    }
}
