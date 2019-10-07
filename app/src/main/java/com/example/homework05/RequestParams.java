package com.example.homework05;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class RequestParams {

    private HashMap<String, String> params;
    StringBuilder stringBuilder;

    public RequestParams() {
        params = new HashMap<> ();
        stringBuilder = new StringBuilder();
    }

    public RequestParams addParams(String Key, String Value)
    {
        try {
            params.put(Key, URLEncoder.encode(Value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String GetEncodedParameteres(){
        for (String key: params.keySet() ) {
            if(stringBuilder.length() > 0){
                stringBuilder.append("&");
            }
            stringBuilder.append(key +"="+ params.get(key));
        }
        //stringBuilder.append("&apiKey=8a7212ba6d284e80acc5cf99fa8f65d2");
        return stringBuilder.toString();
    }

    public String GetEncodedUrl(String url){
        return url + "?" +  GetEncodedParameteres();
    }

    public void encodePostParameters(HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(GetEncodedParameteres());
        writer.flush();
    }
}
