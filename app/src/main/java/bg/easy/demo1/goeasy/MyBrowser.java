package bg.easy.demo1.goeasy;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class MyBrowser extends WebViewClient {
    static String path= Uri.parse("https://demo1.easy.bg").toString();

     //Loads every clicked link inside my webVew
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
