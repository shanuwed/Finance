package edu.washington.shan.news;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * @author shan@uw.edu
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG="DBHelper";
	
	private static final String CREATE_TABLE="create table " +
		DBConstants.TABLE_NAME + " (" +
		DBConstants.KEY_ID + " integer primary key autoincrement, " +
		DBConstants.TITLE_NAME + " text not null, " +
		DBConstants.URL_NAME + " text not null, " +
		DBConstants.TIME_NAME + " integer, " +
		DBConstants.TOPICID_NAME + " integer, " +
		DBConstants.STATUS_NAME + " integer);";
			
    /**
     * Constructor 
     * 
     * @param ctx the Context within which to work
     * @param dbname the name of the database
     * @param factory may be null
     * @param dbversion the database version
     */
	public DBHelper(Context context, String dbname, CursorFactory factory, int dbversion){
		super(context, dbname, factory, dbversion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Creating all the tables");
		try{
			db.execSQL(CREATE_TABLE);
		}catch(SQLiteException e){
			Log.v(TAG, e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading from version " + oldVersion +
				" to " + newVersion +
				", which will destroy all old data.");
		db.execSQL("drop table if exists " + DBConstants.TABLE_NAME);
		onCreate(db);
	}
	
	/**
	 * Copies a database file in the assets directory to
	 * /data/data/<package>/databases
	 * 
	 * Files you you keep in the assets directory will be
	 * included in the package.
	 */
	static public boolean importDatabase(Context context)
	{
	    boolean result = false;
	    try
	    {
	        // Open local db as the input stream
	        InputStream inputStream =
	            context.getAssets().open(DBConstants.DATABASE_NAME);
	        
	        // Path to db; create if it doesn't exist already
	        File dbPath = new File(DBConstants.DB_PATH);
	        if(!dbPath.exists())
	        	dbPath.mkdir();
	        
	        // Full path to db
	        String outFilename = DBConstants.DB_PATH + DBConstants.DATABASE_NAME;
	        
	        // Open the empty db as the output stream
	        OutputStream outputStream =
	            new FileOutputStream(outFilename);
	            
	        // Transfer bytes from the inputfile to the outputfile
	        byte[] buffer = new byte[1024];
	        int length;
	        while((length = inputStream.read(buffer)) > 0)
	            outputStream.write(buffer, 0, length);
	        result = true;
	    }
	    catch(IOException e)
	    {
	        Log.e("importDatabase() failed", e.toString());
	    }    
	    return result;
	}
}
