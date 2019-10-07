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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lv_news;
    String keyword = "apiKey";
    String key = "60eca0ab2fab4b5f85ba4d65689caa04";
    ArrayList<Source> sources = new ArrayList<>();
    ArrayList<String> newsTitles = new ArrayList<String>();
    public  static  String KEY_SOURCE = "SOURCE";
    ProgressBar pb_loading;
    TextView tv_loadingText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Main Activity");

        lv_news = findViewById(R.id.lv_news);
        pb_loading = findViewById(R.id.pb_loading);
        tv_loadingText = findViewById(R.id.tv_loadingText);

        if(isConnected()){
            pb_loading.setVisibility(View.VISIBLE);
            tv_loadingText.setText("Loading Stories");
            RequestParams params = new RequestParams();
            params.addParams(keyword, key);
            new GetNewsTitles(params).execute("https://newsapi.org/v2/sources");
        }
        else
        {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }

        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Source source = new Source();
                source.setId(sources.get(i).id);
                source.setName(sources.get(i).name);
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_SOURCE, source);
                intent.putExtra(KEY_SOURCE, bundle);
                startActivity(intent);
            }
        });
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

    public class GetNewsTitles extends AsyncTask<String, Void, ArrayList<Source>> {
        RequestParams requestParams;

        public GetNewsTitles(RequestParams requestParams) {
            this.requestParams = requestParams;
        }

        @Override
        protected ArrayList<Source> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Source> newsTitle = new ArrayList<>();
            URL url = null;
            try {
                url = new URL(requestParams.GetEncodedUrl(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
                    String status = root.getString("status");

                    if (status.equals("ok")){
                        JSONArray sourcesJson = root.getJSONArray("sources");
                        for (int i=0;i<sourcesJson.length();i++) {
                            JSONObject articleJson = sourcesJson.getJSONObject(i);
                            Source source = new Source();
                            source.id = articleJson.getString("id");
                            source.name = articleJson.getString("name");
                            newsTitle.add(source);
                            sources.add(source);
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

            return newsTitle;
        }

        @Override
        protected void onPostExecute(ArrayList<Source> sources) {
            super.onPostExecute(sources);

            for ( int i=0; i< sources.size(); i++) {
                newsTitles.add(sources.get(i).name);
            }

            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, newsTitles);
            lv_news.setAdapter(adapter);
            pb_loading.setVisibility(View.GONE);
            tv_loadingText.setText("");
        }
    }
}
