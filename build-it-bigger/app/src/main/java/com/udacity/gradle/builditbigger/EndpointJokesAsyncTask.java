package com.udacity.gradle.builditbigger;

import java.io.IOException;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

public class EndpointJokesAsyncTask extends AsyncTask<com.udacity.gradle.builditbigger.MainActivityFragment, Void, String> {

    public static final int NORMAL_JOKES = 1;
    public static final int COMPUTER_JOKES = 2;

    private static MyApi myApiService = null;

    private int jokesType = NORMAL_JOKES;

    private MainActivityFragment mainActivityFragment;

    static {

        if(myApiService == null) {

            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                                                      new AndroidJsonFactory(), null)
                                                      .setRootUrl(BuildConfig.API_URL)
                                                      .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                                            @Override
                                                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                                                   throws IOException {
                                                                        abstractGoogleClientRequest.setDisableGZipContent(true);
                                                            }
                                    });

            myApiService = builder.build();

        }

    }

    public EndpointJokesAsyncTask(int jokesType) {
        this.jokesType = jokesType;
    }

    @Override
    protected String doInBackground(com.udacity.gradle.builditbigger.MainActivityFragment ... params) {

        mainActivityFragment = params[0];

        try {

            if (this.jokesType == NORMAL_JOKES) {
                return myApiService.getNormalJoke().execute().getData();
            } else {
                return myApiService.getComputerJoke().execute().getData();
            }

        } catch (IOException e) {
            return e.getMessage();
        }

    }

    @Override
    protected void onPostExecute(String result) {
        mainActivityFragment.loadedJoke = result;
        mainActivityFragment.launchDisplayJokeActivity();
    }

}