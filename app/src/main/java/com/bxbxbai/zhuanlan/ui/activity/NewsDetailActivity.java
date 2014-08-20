package com.bxbxbai.zhuanlan.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.bxbxbai.zhuanlan.R;

/**
 * Created by baia on 14-8-20.
 * @author bxbxbai
 * @since 2014.08.20
 */
public class NewsDetailActivity extends Activity {

    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        mWebView = (WebView)findViewById(R.id.ww_news_detail);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

//        mWebView.addJavascriptInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
