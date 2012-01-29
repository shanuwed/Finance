package edu.washington.shan.news;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Subscription manager checks for connectivity to the server.
 * Also it retreives RSS feeds from the server, and checks to see
 * if feeds already exist in the database. It only adds new feeds
 * to the database (to prevent duplicates.)
 * 
 * This class is meant to be instantiated within a background worker thread.
 * 
 * @author shan@uw.edu
 *
 */
public class SubscriptionManager {
	
	private static final String TAG="SubscriptionManager";
    private DBAdapter mDbAdapter;
    private Context mContext;
	
    public SubscriptionManager(Context context) 
    {
    	mContext = context;
    	mDbAdapter = new DBAdapter(mContext);
    }
    
    /**
     * Check if the host can be resolved and connected.
     * @return
     */
    public boolean checkConnection()
    {
    	boolean result = false;
    	final String host = "finance.yahoo.com"; // "finance.yahoo.com:80"
    	
    	// Creates a socket and connects it to 
    	// the specified port number on the named host.
    	try 
    	{
			Socket socket = new Socket(host, 80);
			if(socket.isConnected())
			{
				result = true;
				socket.close();
			}
		} 
    	catch (UnknownHostException e) 
    	{
			Log.e(TAG, e.getMessage(), e);
		} 
        catch (SocketException e)
        {
        	Log.e(TAG, e.getMessage(), e);
        }
    	catch (IOException e) 
    	{
			Log.e(TAG, e.getMessage(), e);
		}
    	catch(Exception e)
    	{
            Log.e(TAG, e.getMessage(), e);
    	}
    	return result;
    }
    
    public boolean getRssFeed(String tabTag)
    {
    	assert tabTag != null && tabTag.length() > 0;
    	
        boolean ret = false;
        mDbAdapter.open();
        try 
        {
			ret = getRssFeedPrivate(Constants.RSS_BASE_URI + tabTag, 
					PrefKeyManager.getInstance().keyToValue(tabTag));
		} 
        catch (UnknownHostException e) 
        {
			Log.e(TAG, e.getMessage(), e);
		}
        catch(org.apache.http.conn.HttpHostConnectException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (SocketException e)
        {
        	Log.e(TAG, e.getMessage(), e);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        finally
        {
        	mDbAdapter.close();
        }
        return ret;
    }
    
    private boolean getRssFeedPrivate(String url, int topicId) 
    	throws java.net.UnknownHostException, java.net.SocketException
    {
		Log.v(TAG, "Requesting RSS feed for:" + url + " and topicId:" + topicId);
		
    	boolean ret = false;
        try 
        {
	        RSSReader reader = new RSSReader();
			RSSFeed feed = reader.load(url); // may throw RSSReaderException or UnknownHostException
			List<RSSItem> rssItems = feed.getItems();
			for(RSSItem rssItem: rssItems)
			{
				String title = rssItem.getTitle();
				String sqlTitle = title.replace("'", "''");
    			java.util.Date timestamp = rssItem.getPubDate();
    			long timeInMillisec = timestamp.getTime();
    			
    			// Sometimes server returns a negative time-in-milliseconds
    			if(timeInMillisec <= 0){
    				// Use today's date
    				timeInMillisec = Calendar.getInstance().getTimeInMillis();
    			}
    			
    			// Check to see if this title already exists
    			// TODO To ensure it's a unique title you can constrain
    			// the query with topicId.
    			Cursor cursor = mDbAdapter.fetchItemsByTitle(sqlTitle);
    			((Activity)mContext).startManagingCursor(cursor);
    			if(cursor.getCount() == 0)
    			{
	    			mDbAdapter.createItem(title, 
						rssItem.getLink().toString(), 
						timeInMillisec, 
						topicId, 
						0);// status
    			}
			}
			ret = true;
		} 
        catch (RSSReaderException e) 
        {
			Log.e(TAG, e.getMessage(), e);
		}
        catch (java.lang.NullPointerException e)
        {
			Log.e(TAG, e.getMessage(), e);
		}
		return ret;
    }
}
