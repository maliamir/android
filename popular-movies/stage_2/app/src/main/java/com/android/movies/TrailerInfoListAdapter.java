package com.android.movies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.net.Uri;

import com.android.movies.model.Trailer;

import com.android.movies.utils.RestUtils;

public class TrailerInfoListAdapter extends ArrayAdapter<Trailer> {

    private Context context;

    private Trailer[] trailers;

    public TrailerInfoListAdapter(Context context, Trailer[] trailers) {
        super(context, R.layout.trailers_list, trailers);
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final Trailer trailer = trailers[position];

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trailers_list, viewGroup, false);

        ImageView trailerImageView = rowView.findViewById(R.id.trailer_iv);
        TextView nameTextView = rowView.findViewById(R.id.trailer_name_tv);

        nameTextView.setText(trailer.getName());
        //trailerImageView.setTooltipText(trailer.getName());

        trailerImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                System.out.println("Trailer \"" + trailer.getName() + "\" is selected, playing it ...");
                try {

                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RestUtils.YOUTUBE_APP + trailer.getKey()));
                    context.startActivity(appIntent);

                } catch (ActivityNotFoundException ex) {

                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RestUtils.TRAILER_VIEWER_BASE_URL + trailer.getKey()));
                    context.startActivity(webIntent);

                }

            }

        });

        return rowView;

    }

}