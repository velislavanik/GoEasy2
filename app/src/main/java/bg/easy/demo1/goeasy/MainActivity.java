package bg.easy.demo1.goeasy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static ActionBar ab;
    private static String WV_URL = MyBrowser.path;
    WebView wv1;
    private SwipeRefreshLayout swipe;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ab = getSupportActionBar();
        wv1 = findViewById(R.id.webView);
        swipe = findViewById(R.id.swiperefresh);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.bar_title);
        //Webview settings; defaults are customized for best performance
        WebSettings webSettings = wv1.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (DetectConnection.isInternetAvailable(this)) {
            wv1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wv1.loadUrl(WV_URL);
        } else if (isCacheAvailable(wv1.getUrl())) {
            wv1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            wv1.loadUrl(WV_URL);

        } else {
            //no connection & no cache on onCreate
            wv1.loadUrl("file:///android_asset/error.html");
            wv1.clearCache(true);
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }
        wv1.setWebViewClient(new MyBrowser() {

            @Override
            public void onReceivedError(final WebView webView, int errorCode, String description, final String failingUrl) {

                try {
                    webView.stopLoading();
                } catch (Exception e) {
                    //
                }
                if (isCacheAvailable(wv1.getUrl())) {
                    if (DetectConnection.isInternetAvailable(MainActivity.this)) {
                        wv1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    } else {
                        wv1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
                        alert();
                    }
                }
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }
        });

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */

        swipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (DetectConnection.isInternetAvailable(getApplicationContext())) {
                            wv1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);             //+
                            if (wv1.getUrl().contains("error.html")) {
                                // WV_URL = "https:\\jysk.bg";
                                // wv1.loadUrl(wv1.getUrl());
                                wv1.clearCache(true);
                                wv1.loadUrl(MyBrowser.path);

                            } else {
                                wv1.loadUrl(wv1.getUrl());

                            }
                            swipe.setRefreshing(false); //signal to stop the refreshing indicator

                        } else {
                            alert();
                        }
                        swipe.setRefreshing(false);//signal to stop the refreshing indicator
                    }
                }
        );
    }

    //shows No Internet  message
    private void alert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Connect to a Network");
        alertDialog.setMessage("To use Ease, turn on mobile data or connect to Wi-Fi.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        alertDialog.show();
    }

    //Check in if there is cache at all
    public boolean isCacheAvailable(String url) {
        File dir = getApplicationContext().getCacheDir();

        if (dir.exists())
            return dir.listFiles().length > 0;
        else {
            return false;
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
