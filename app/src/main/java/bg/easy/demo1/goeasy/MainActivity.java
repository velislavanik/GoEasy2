package bg.easy.demo1.goeasy;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    WebView wv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv1=findViewById(R.id.webView);
        wv1.setWebViewClient(new MyBrowser());
        wv1.loadUrl(MyBrowser.path);
    }
}
