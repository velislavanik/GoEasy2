package bg.easy.demo1.goeasy;

import android.net.Uri;
import android.webkit.WebViewClient;

class MyBrowser extends WebViewClient {
    static String path= Uri.parse("https://demo1.easy.bg").toString();

}
