package com.udacity.gradle.builditbigger;

import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.udacity.ui.DisplayJokeActivity;

public class MainActivityFragment extends Fragment {

    public boolean testFlag = false;
    public String loadedJoke;

    @BindView(R.id.computer_joke_button) Button computerJokeButton;
    @BindView(R.id.normal_joke_button) Button normalJokeButton;

    @BindView(R.id.joke_progressbar) ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        normalJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                new EndpointJokesAsyncTask(EndpointJokesAsyncTask.NORMAL_JOKES).execute(MainActivityFragment.this);
            }
        });

        computerJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new EndpointJokesAsyncTask(EndpointJokesAsyncTask.COMPUTER_JOKES).execute(MainActivityFragment.this);
            }
        });

        progressBar.setVisibility(View.GONE);

        return view;

    }

    public void launchDisplayJokeActivity(){

        if (!testFlag) {

            Context context = getActivity();

            Intent intent = new Intent(context, DisplayJokeActivity.class);
            intent.putExtra(context.getString(R.string.jokeEnvelope), loadedJoke);

            context.startActivity(intent);
            progressBar.setVisibility(View.GONE);
        }

    }

}