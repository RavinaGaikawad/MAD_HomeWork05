package com.example.homework05;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterList extends ArrayAdapter<News> {

    private Context mContext;
    private List<News> newsList;
    public CustomAdapterList(Activity context, ArrayList<News> newsArrayList) {
        super(context, 0 , newsArrayList);
        mContext = context;
        newsList = newsArrayList;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.news_list,parent,false);

        News currentnews = newsList.get(position);

        ImageView image = listItem.findViewById(R.id.iv_icon);
        Picasso.get().load(currentnews.getUrlToImage()).into(image);

        TextView title = listItem.findViewById(R.id.tv_title);
        title.setText(currentnews.getTitle());

        TextView author = listItem.findViewById(R.id.tv_author);
        author.setText(currentnews.getAuthor());

        TextView tv_published = listItem.findViewById(R.id.tv_published);
        tv_published.setText(currentnews.getPublishedAt());

        return listItem;
    }
}
