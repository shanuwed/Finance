/**
 * 
 */
package edu.washington.shan.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import edu.washington.shan.AsyncTaskCompleteListener;
import edu.washington.shan.Consts;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.widget.Toast;

/**
 * @author shan@uw.edu
 *
 */
public class DownloadStockTask extends AsyncTask<String, Void, String> {
    
    private static final String TAG = "DownloadStockTask";
    
    private Context mContext;
    private AsyncTaskCompleteListener<String> mCallback;
    
    public DownloadStockTask(Context context, AsyncTaskCompleteListener<String> callback){
        mContext = context;
        mCallback = callback;
    }
    
    /**
     * 
     * @param params Stock symbols like AAPL, MSFT..
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        Log.v(TAG, "doInBackground");
        final String base = "http://www.google.com/ig/api?stock=";
        
        if(params.length < 1)
            return "";
        
        String result = "";
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            
            StringBuilder uri = new StringBuilder();
            uri.append(base);
            uri.append(params[0]);
            for(int n=1; n < params.length; n++){
                uri.append("&stock=");
                uri.append(params[n]);
            }
            
            request.setURI(new URI(uri.toString()));
            
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
            //Log.v(TAG, result);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
        return result;
    }
    
    /**
     * result == xml string to parse
     */
    protected void onPostExecute(String result) {
        Log.v(TAG, "onPostExecute");
        /*
        if (result == null || result.length() < 10) {
            Log.e(TAG, "onPostExecute: result is invalid");
            Toast.makeText(mContext, "Portfolio could not be updated.",
                    Toast.LENGTH_SHORT).show();
            return;
        }*/
        
        String ret = "fail"; // TODO make it a boolean??
        
        if(result != null && result.length() > 10){

            Stock[] stocks = null;
            try {
                stocks = Stock.parse(result);
            } catch (XmlPullParserException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (stocks != null){
                for (Stock stock : stocks)
                    stock.storeToDatabase(mContext);
                ret = "success";
            }
        }
        
        mCallback.onTaskComplete(ret); 
    }
}
