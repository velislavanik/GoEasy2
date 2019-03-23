package bg.easy.demo1.goeasy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {


    private static String WV_URL = MyBrowser.path;
    WebView wv1;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv1 = findViewById(R.id.webView);
        swipe = findViewById(R.id.swiperefresh);

        //Webview settings; defaults are customized for best performance
        WebSettings webSettings = wv1.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);

        wv1.setWebViewClient(new MyBrowser());
        loadPage(WV_URL);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        loadPage(wv1.getUrl());
                        swipe.setRefreshing(false); //signal to stop the refreshing indicator
                    }
                }
        );
    }

    public void loadPage(String url) {
        final String url2 = url;
        if (DetectConnection.isInternetAvailable(this)) {
            wv1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wv1.loadUrl(url2);
        } else {
            wv1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            wv1.loadUrl(url2);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing
                }
            });
            alertDialog.show();
        }
    }

    /*The WebView maintains a browsing history just like a normal browser.
      If there is no history then it will result in the default behavior of back button i.e. exiting the app. */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv1.canGoBack()) {
                        wv1.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
