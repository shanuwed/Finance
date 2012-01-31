/**
 * 
 */
package edu.washington.shan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author shan@uw.edu
 * 
 */
public class StockDetailActivity extends Activity {

    private static final String TAG = "StockDetailActivity";
    // append stock symbol
    private static final String mStartUrl = "http://www.google.com/finance?q=NASDAQ:";
    private WebView mWebView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);

        // wire the progress bar
        // mProgressBar = (ProgressBar) findViewById(R.id.main_progressBar1);

        String url = mStartUrl;
        Intent intent = this.getIntent();
        if(intent != null){
            String symbol = intent.getStringExtra(Consts.STOCK_SYMBOL);
            if(symbol != null && symbol.length() >= 0){
                url += symbol;
            }else{
                url = "http://www.google.com/finance"; // fallback
            }
        }
        
        Log.v(TAG, "Navigating to url:" + url);

        // wire the webview
        mWebView = (WebView) findViewById(R.id.stock_detail_webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HelloWebViewClient());
        mWebView.loadUrl(url);
    }

    /**
     * to handle the back button inside the webview
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // mProgressBar.setVisibility(ProgressBar.GONE);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            // mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }
}