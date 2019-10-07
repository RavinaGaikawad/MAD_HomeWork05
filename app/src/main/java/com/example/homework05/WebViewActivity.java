package com.example.homework05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    WebView wv_news;
    ProgressBar pb_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        pb_loading = findViewById(R.id.pb_loading);
        setTitle("News");

        if(isConnected()){

            pb_loading.setVisibility(View.VISIBLE);
            String url = getIntent().getExtras().getString(NewsActivity.KEY_URL);


            wv_news = findViewById(R.id.wv_news);
            wv_news.setWebViewClient(new WebViewClient());
            wv_news.loadUrl(url);

            pb_loading.setVisibility(View.GONE);
        }
        else
        {
            Toast.makeText(this, "No internet Connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if(networkCapabilities != null)
        {
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(wv_news.canGoBack()){
            wv_news.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
