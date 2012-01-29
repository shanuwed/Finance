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

package edu.washington.shan.news;

import java.util.Calendar;

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
    public long createItem(String title, String url, long time, int topicId, int status) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBConstants.TITLE_NAME, title);
        initialValues.put(DBConstants.URL_NAME, url);
        initialValues.put(DBConstants.TIME_NAME, time);
        initialValues.put(DBConstants.TOPICID_NAME, topicId);
        initialValues.put(DBConstants.STATUS_NAME, status);

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
     * Delete items that are older than x days AND
     * not marked favorite
     * 
     * @param days
     * @return true if deleted, false otherwise
     */
    public boolean deleteItemsOlderThan(long days) {
    	Log.v(TAG, "Entering deleteItemsOlderThan()");
		if (days > 0) {
			Calendar cal = Calendar.getInstance(); // now
			Log.v(TAG, "Now: " + Long.toString(cal.getTimeInMillis()));
			long offset = days * 86400000L;//= 24 * 60 * 60 * 1000
			Log.v(TAG, "Offset: " + Long.toString(offset));
			long t = cal.getTimeInMillis() - offset;
			
			String whereClause = DBConstants.TIME_NAME
			+ "<" + Long.toString(t)
			+ " AND " + DBConstants.STATUS_NAME
			+ " = 0";
			Log.v(TAG, "Where clause: " + whereClause);

			return mDb.delete(DBConstants.TABLE_NAME, whereClause, null) > 0;
		}
		return false;
    }

    /**
     * Return a Cursor over the list of all items in the database
     * 
     * @return Cursor over all items
     */
    public Cursor fetchAllItems() {

        return mDb.query(DBConstants.TABLE_NAME, new String[] {
    		DBConstants.KEY_ID, 
    		DBConstants.TITLE_NAME,
    		DBConstants.URL_NAME,
    		DBConstants.TIME_NAME,
    		DBConstants.TOPICID_NAME,
    		DBConstants.STATUS_NAME}, 
    		null, 
    		null,
    		null,
    		null,
    		null);
    }
    
    /**
     * Return a Cursor positioned at the item that matches the given title
     * 
     * @param title
     * @return
     * @throws SQLException
     */
    public Cursor fetchItemsByTitle(String title) throws SQLException {
    	
        Cursor cursor =

            mDb.query(true, DBConstants.TABLE_NAME, new String[] {
            		DBConstants.KEY_ID}, 
                    DBConstants.TITLE_NAME + "='" + title + "'", 
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
    
    /**
     * Return a Cursor positioned at the item that matches the given title
     * 
     * @param title
     * @return
     * @throws SQLException
     */
    public Cursor fetchItemsByMatchingTitle(String title) throws SQLException {
    	
        Cursor cursor =

	        mDb.query(true, DBConstants.TABLE_NAME, new String[] {
	        		DBConstants.KEY_ID, 
	        		DBConstants.TITLE_NAME,
	        		DBConstants.URL_NAME,
	        		DBConstants.TIME_NAME,
	        		DBConstants.TOPICID_NAME,
	        		DBConstants.STATUS_NAME}, 
	        		DBConstants.TITLE_NAME + " LIKE '%" + title + "%'", 
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
    
    /**
     * Return a Cursor positioned at the item that matches the given topicId
     * 
     * @param topicId
     * @return Cursor positioned to matching item
     * @throws SQLException
     */
    public Cursor fetchItemsByTopicId(long topicId) throws SQLException {
    	
        Cursor cursor =

            mDb.query(true, // distinct
            		DBConstants.TABLE_NAME, // table 
            		new String[] { // columns
            		DBConstants.KEY_ID, 
            		DBConstants.TITLE_NAME,
            		DBConstants.URL_NAME,
            		DBConstants.TIME_NAME,
            		DBConstants.TOPICID_NAME,
            		DBConstants.STATUS_NAME}, 
                    DBConstants.TOPICID_NAME + "=" + topicId, 
                    null,
                    null,
                    null,
                    DBConstants.TIME_NAME + " DESC",
                    null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Return a Cursor positioned at the item that matches the given rowId
     * 
     * @param rowId id of Item to retrieve
     * @return Cursor positioned to matching item, if found
     * @throws SQLException if item could not be found/retrieved
     */
    public Cursor fetchItemsByRowId(long rowId) throws SQLException {

        Cursor cursor =

            mDb.query(true, DBConstants.TABLE_NAME, new String[] {
            		DBConstants.KEY_ID, 
            		DBConstants.TITLE_NAME,
            		DBConstants.URL_NAME,
            		DBConstants.TIME_NAME,
            		DBConstants.TOPICID_NAME,
            		DBConstants.STATUS_NAME}, 
                    DBConstants.KEY_ID + "=" + rowId, 
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

	//sqlite>UPDATE rssentries SET Status ='1' WHERE _Id=555;   
    public boolean setStatusByRowId(long rowId, int i) {

        ContentValues updateStatus = new ContentValues();
        updateStatus.put(DBConstants.STATUS_NAME, i);
        
		int numberOfRows = 
        	mDb.update(DBConstants.TABLE_NAME, 
        			updateStatus, 
        			DBConstants.KEY_ID + "==?", 
        			new String[]{Long.toString(rowId)});
        return (numberOfRows != 0);
	}
}
