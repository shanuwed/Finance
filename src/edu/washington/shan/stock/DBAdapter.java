/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.washington.shan.stock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Adapter based on Notepad example from developer.android.com
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the items database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DBAdapter open() throws SQLException {
        Log.v(TAG, "opening database...");
    	mDbHelper = new DBHelper(mCtx, 
        		DBConstants.DATABASE_NAME,
        		null,
        		DBConstants.DATABASE_VERSION);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        Log.v(TAG, "closing database...");
        mDbHelper.close();
    }


    /**
     * Create a new item using the title and body provided. If the item is
     * successfully created return the new rowId for that item, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title 
     * @param url
     * @param time
     * @param topicId
     * @param status
     * @return rowId or -1 if failed
     */
    public long createItem(Stock stock) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBConstants.symbol_NAME, stock.symbol);
        initialValues.put(DBConstants.pretty_symbol_NAME, stock.pretty_symbol);
        initialValues
                .put(DBConstants.symbol_lookup_url_NAME, stock.symbol_lookup_url);
        initialValues.put(DBConstants.company_NAME, stock.company);
        initialValues.put(DBConstants.exchange_NAME, stock.exchange);
        initialValues
                .put(DBConstants.exchange_timezone_NAME, stock.exchange_timezone);
        initialValues.put(DBConstants.exchange_utc_offset_NAME,
                stock.exchange_utc_offset);
        initialValues.put(DBConstants.exchange_closing_NAME, stock.exchange_closing);
        initialValues.put(DBConstants.divisor_NAME, stock.divisor);
        initialValues.put(DBConstants.currency_NAME, stock.currency);
        initialValues.put(DBConstants.last_NAME, stock.last);
        initialValues.put(DBConstants.high_NAME, stock.high);
        initialValues.put(DBConstants.low_NAME, stock.low);
        initialValues.put(DBConstants.volume_NAME, stock.volume);
        initialValues.put(DBConstants.avg_volume_NAME, stock.avg_volume);
        initialValues.put(DBConstants.market_cap_NAME, stock.market_cap);
        initialValues.put(DBConstants.open_NAME, stock.open);
        initialValues.put(DBConstants.y_close_NAME, stock.y_close);
        initialValues.put(DBConstants.change_NAME, stock.change);
        initialValues.put(DBConstants.perc_change_NAME, stock.perc_change);
        initialValues.put(DBConstants.delay_NAME, stock.delay);
        initialValues.put(DBConstants.trade_timestamp_NAME, stock.trade_timestamp);
        initialValues.put(DBConstants.trade_date_utc_NAME, stock.trade_date_utc);
        initialValues.put(DBConstants.trade_time_utc_NAME, stock.trade_time_utc);
        initialValues.put(DBConstants.current_date_utc_NAME, stock.current_date_utc);
        initialValues.put(DBConstants.current_time_utc_NAME, stock.current_time_utc);
        initialValues.put(DBConstants.symbol_url_NAME, stock.symbol_url);
        initialValues.put(DBConstants.chart_url_NAME, stock.chart_url);
        initialValues.put(DBConstants.disclaimer_url_NAME, stock.disclaimer_url);
        initialValues.put(DBConstants.ecn_url_NAME, stock.ecn_url);
        initialValues.put(DBConstants.isld_last_NAME, stock.isld_last);
        initialValues.put(DBConstants.isld_trade_date_utc_NAME,
                stock.isld_trade_date_utc);
        initialValues.put(DBConstants.isld_trade_time_utc_NAME,
                stock.isld_trade_time_utc);
        initialValues.put(DBConstants.brut_last_NAME, stock.brut_last);
        initialValues.put(DBConstants.brut_trade_date_utc_NAME,
                stock.brut_trade_date_utc);
        initialValues.put(DBConstants.brut_trade_time_utc_NAME,
                stock.brut_trade_time_utc);
        initialValues.put(DBConstants.daylight_savings_NAME, stock.daylight_savings);

        return mDb.insert(DBConstants.TABLE_NAME, null, initialValues);
    }

    /**
     * Delete the item with the given rowId
     * 
     * @param rowId id of item to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem(long rowId) {

        return mDb.delete(DBConstants.TABLE_NAME, 
        		DBConstants.KEY_ID + "=" + rowId, 
        		null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all items in the database
     * 
     * @return Cursor over all items
     */
    public Cursor fetchAllItems() {

        return mDb.query(DBConstants.TABLE_NAME, new String[] {
    		DBConstants.KEY_ID, 
    		DBConstants.symbol_NAME,
    		DBConstants.pretty_symbol_NAME,
    		DBConstants.symbol_lookup_url_NAME,
    		DBConstants.company_NAME,
    		DBConstants.exchange_NAME,
    		DBConstants.exchange_timezone_NAME,
    		DBConstants.exchange_utc_offset_NAME,
    		DBConstants.exchange_closing_NAME,
    		DBConstants.divisor_NAME,
    		DBConstants.currency_NAME,
    		DBConstants.last_NAME,
    		DBConstants.high_NAME,
    		DBConstants.low_NAME,
    		DBConstants.volume_NAME,
    		DBConstants.avg_volume_NAME,
    		DBConstants.market_cap_NAME,
    		DBConstants.open_NAME,
    		DBConstants.y_close_NAME,
    		DBConstants.change_NAME,
    		DBConstants.perc_change_NAME,
    		DBConstants.delay_NAME,
    		DBConstants.trade_timestamp_NAME,
    		DBConstants.trade_date_utc_NAME,
    		DBConstants.trade_time_utc_NAME,
    		DBConstants.current_date_utc_NAME,
    		DBConstants.current_time_utc_NAME,
    		DBConstants.symbol_url_NAME,
    		DBConstants.chart_url_NAME,
    		DBConstants.disclaimer_url_NAME,
    		DBConstants.ecn_url_NAME,
    		DBConstants.isld_last_NAME,
    		DBConstants.isld_trade_date_utc_NAME,
    		DBConstants.isld_trade_time_utc_NAME,
    		DBConstants.brut_last_NAME,
    		DBConstants.brut_trade_date_utc_NAME,
    		DBConstants.brut_trade_time_utc_NAME,
    		DBConstants.daylight_savings_NAME
    		}, 
    		null, 
    		null,
    		null,
    		null,
    		null);
    }
    
    /**
     * Return a Cursor positioned at the item that matches the given title
     * 
     * @param symbol
     * @return
     * @throws SQLException
     */
    public Cursor fetchItemsBySymbol(String symbol) throws SQLException {
    	
        Cursor cursor =

            mDb.query(true, DBConstants.TABLE_NAME, new String[] {
            		DBConstants.KEY_ID,
                    DBConstants.symbol_NAME}, 
                    DBConstants.symbol_NAME + "='" + symbol + "'", 
                    null,
                    null,
                    null,
                    null,
                    null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void updateItem(Stock stock) {
        // TODO void DBAdapter.updateItem(Stock stock)
        
    }
    
}
