package com.udacity.ui;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.content.Intent;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import android.net.Uri;

import com.android.ui.R;

public class DisplayJokeActivity extends AppCompatActivity {

    private String jokeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_joke);

        TextView textview = findViewById(R.id.joke_text);

        Intent intent = getIntent();
        jokeResult = intent.getStringExtra(getString(R.string.jokeEnvelope));

        if (jokeResult != null) {
            textview.setText(jokeResult);
        } else {
            textview.setText(R.string.joke_retrieve_error);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_share) {
            composeEmail(jokeResult);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void composeEmail(String subject) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

}