package com.android.movies;

import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.net.Uri;

import com.android.movies.model.Review;

public class ReviewInfoListAdapter extends ArrayAdapter<Review> {

    private Context context;

    private Review[] reviews;

    public ReviewInfoListAdapter(Context context, Review[] reviews) {
        super(context, R.layout.reviews_list, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final Review review = reviews[position];

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.reviews_list, viewGroup, false);

        TextView subContentTextView = rowView.findViewById(R.id.sub_content_tv);
        TextView authorTextView = rowView.findViewById(R.id.author_tv);

        subContentTextView.setText(review.getSubContent(context.getResources().getConfiguration().orientation));
        authorTextView.setText((context.getString(R.string.by) + review.getAuthor()));

        subContentTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            System.out.println("Review \"" + review.getSubContent(context.getResources().getConfiguration().orientation) +
                               "\" is selected, opening it in he browser ...");
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
            context.startActivity(webIntent);

            }

        });

        return rowView;

    }

}