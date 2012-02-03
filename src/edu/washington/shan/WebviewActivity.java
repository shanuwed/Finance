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
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Displays the stock detail in a web view
 * @author shan@uw.edu
 * 
 */
public class WebviewActivity extends Activity {

    private static final String TAG = "WebviewActivity";
    
    private WebView mWebView;
    private TextView mTextView; // title
    private ProgressBar mProgressBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        // wire the webview
        mWebView = (WebView) findViewById(R.id.webview_webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HelloWebViewClient());
        
        // wire the progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.webview_progressBar1);
        
        // wire the textview
        mTextView = (TextView) findViewById(R.id.webview_title);

        // Extract the arguments from the intent
        Intent intent = this.getIntent();
        if(intent != null){
            String title = intent.getStringExtra(Consts.WEBVIEW_TITLE);
            if(title != null && title.length() >= 0){
                mTextView.setText(title);
            }else{
                mTextView.setText("Browse");
            }
            
            String url = intent.getStringExtra(Consts.WEBVIEW_URL);
            if(url != null && url.length() >=0){
                Log.v(TAG, "Navigating to url:" + url);
                mWebView.loadUrl(url);
            }
        }
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

            mProgressBar.setVisibility(ProgressBar.GONE);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }
}