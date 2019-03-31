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

        wv1.setWebViewClient(new MyBrowser(this));
        loadPage(WV_URL);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (wv1.getUrl().contains("error.html"))
                            loadPage(MyBrowser.path);
                        else
                            loadPage(wv1.getUrl());
                        swipe.setRefreshing(false); //signal to stop the refreshing indicator
                    }
                }
        );
    }

    public void loadPage(String url) {
        if (DetectConnection.isInternetAvailable(this)) {
            wv1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wv1.loadUrl(url);
        } else {
            if (isCacheAvailable()) {
                wv1.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
                wv1.loadUrl(url);
            } else {
                wv1.loadUrl("file:///android_asset/error.html");
            }
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Connect to a Network");
            alertDialog.setMessage("To use Ease, turn on mobile data or connect to Wi-Fi");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            });
            alertDialog.show();
        }
    }

    //Check in if there is cahc at all
    public boolean isCacheAvailable() {
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
