package com.example.ais3.moneytaps.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ais3.moneytaps.R;

public class WikiPage extends AppCompatActivity {

    String title,url;
    WebView wikiPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_page);


        wikiPage=(WebView)findViewById(R.id.wiki);

        Intent i= getIntent();
        title = i.getStringExtra("pageTitle");

        title = title.replaceAll(" ", "%20");

        url = "https://en.wikipedia.org/wiki/"+title;

        wikiPage.setWebViewClient(new MyBrowser());

        wikiPage.getSettings().setLoadsImagesAutomatically(true);
        wikiPage.getSettings().setJavaScriptEnabled(true);
        wikiPage.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        wikiPage.loadUrl(url);

    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
