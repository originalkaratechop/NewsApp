package com.example.android.newsapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<Articles> {

    private static final String LOCATION_SEPARATOR = "T";

    public NewsAdapter(Context context, List<Articles> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        Articles currentArticle = getItem(position);

        TextView section = (TextView) convertView.findViewById(R.id.category);
        section.setText(currentArticle.getSection());

        TextView titleTextView = (TextView) convertView.findViewById(R.id.article_title_text_view);
        titleTextView.setText(currentArticle.getTitle());

        TextView briefTextView = (TextView) convertView.findViewById(R.id.article_brief_text_view);
        briefTextView.setText(currentArticle.getBrief());

        TextView name = (TextView) convertView.findViewById(R.id.author);
        name.setText(currentArticle.getName());

        String originalDate = currentArticle.getDate();
        String date_yymmdd;

        String[] parts = originalDate.split(LOCATION_SEPARATOR);
        date_yymmdd = parts[0];

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(date_yymmdd);

        return convertView;
    }

}