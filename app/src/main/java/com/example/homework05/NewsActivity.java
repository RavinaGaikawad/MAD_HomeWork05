package com.example.homework05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
    ListView lv_news;
    String sources = "sources";
    String keyword = "apiKey";
    String key = "60eca0ab2fab4b5f85ba4d65689caa04";
    ArrayList<News> newsList = new ArrayList<>();
    TextView tv_title;
    TextView tv_author;
    TextView tv_published;
    ImageView iv_icon;
    public  static  String KEY_URL = "URL";
    ProgressBar pb_loading;
    TextView tv_loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        tv_author = findViewById(R.id.tv_author);
        tv_published = findViewById(R.id.tv_published);
        tv_title = findViewById(R.id.tv_title);
        iv_icon = findViewById(R.id.iv_icon);
        pb_loading = findViewById(R.id.pb_loading);
        tv_loadingText = findViewById(R.id.tv_loadingText);

        final Bundle extrasFromMain = getIntent().getExtras().getBundle(MainActivity.KEY_SOURCE);
        Source source = (Source) extrasFromMain.getSerializable(MainActivity.KEY_SOURCE);

        setTitle(source.name);
        lv_news = findViewById(R.id.lv_news);

        if(isConnected()){
            pb_loading.setVisibility(View.VISIBLE);
            tv_loadingText.setText("Loading Stories");
            RequestParams params = new RequestParams();
            params.addParams(sources, source.id);
            params.addParams(keyword, key);
            new NewsActivity.GetNews(params).execute("https://newsapi.org/v2/top-headlines");
        }
        else
        {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
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

    public class GetNews extends AsyncTask<String, Void, ArrayList<News>>{
        RequestParams requestParams;

        public GetNews(RequestParams requestParams) {
            this.requestParams = requestParams;
        }

        @Override
        protected ArrayList<News> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<News> newsArrayList = new ArrayList<>();
            URL url = null;
            try {
                url = new URL(requestParams.GetEncodedUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
                    String status = root.getString("status");
                    String totalResults = root.getString("totalResults");

                    if (status.equals("ok") && totalResults.length() > 0 ){
                        JSONArray articles = root.getJSONArray("articles");
                        for (int i=0;i<articles.length();i++) {
                            JSONObject articleJson = articles.getJSONObject(i);
                            News news = new News();
                            news.author = articleJson.getString("author");
                            news.title = articleJson.getString("title");
                            news.publishedAt = articleJson.getString("publishedAt");
                            news.urlToImage = articleJson.getString("urlToImage");
                            news.setUrl(articleJson.getString("url"));
                            newsList.add(news);
                            newsArrayList.add(news);
                        }
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);
            final CustomAdapterList adapter=new CustomAdapterList(NewsActivity.this, news);
            lv_news=findViewById(R.id.lv_news);
            lv_news.setAdapter(adapter);
            pb_loading.setVisibility(View.GONE);
            tv_loadingText.setText("");


            lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String url = adapter.getItem(position).url;
                    Intent intent = new Intent(NewsActivity.this, WebViewActivity.class);
                    intent.putExtra(KEY_URL, url);
                    startActivity(intent);

                }
            });
        }
    }
}
