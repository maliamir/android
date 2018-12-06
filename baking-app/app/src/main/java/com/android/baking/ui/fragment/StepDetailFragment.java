package com.android.baking.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.android.baking.R;
import com.android.baking.model.Step;
import com.android.baking.ui.FragmentListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StepDetailFragment extends Fragment implements View.OnClickListener {

    private int currentIndex;
    private int width;
    private int height;

    private Boolean tablet;

    private long currentPosition;
    private boolean playWhenReady = true;

    private ArrayList<Step> steps;

    private FragmentListener listener;
    private SimpleExoPlayer player;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.currentStep)
    TextView current;

    @BindView(R.id.next_button)
    FloatingActionButton next;

    @BindView(R.id.back_button)
    FloatingActionButton back;

    @BindView(R.id.mediaView)
    SimpleExoPlayerView playerView;

    @BindView(R.id.emptyView)
    ImageView empty;

    @BindView(R.id.root)
    LinearLayout layout;

    @BindView(R.id.dataFrame1)
    FrameLayout dataFrame1;

    @BindView(R.id.dataFrame2)
    FrameLayout dataFrame2;

    @BindView(R.id.image)
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.recipe_data_fragment, container, false);
        ButterKnife.bind(this, root);
        Bundle bundle;
        if(savedInstanceState == null) {

            bundle = getArguments();
            steps = bundle.getParcelableArrayList("steps");
            currentIndex = bundle.getInt("current");
            tablet = bundle.getBoolean("tablet");

        } else {

            steps = savedInstanceState.getParcelableArrayList("steps");
            currentIndex = savedInstanceState.getInt("index");
            tablet = savedInstanceState.getBoolean("tablet");

        }

        show();
        back.setOnClickListener(this);
        next.setOnClickListener(this);
        onRestoreInstanceState(savedInstanceState);

        return root;

    }

    public void show() {

        if (currentIndex <= 0) {
            back.setVisibility(GONE);
        } else {
            back.setVisibility(View.VISIBLE);
        }

        if (listener != null) {
            listener.setCurrent(currentIndex);
        }

        if (currentIndex >= steps.size() - 1) {
            next.setVisibility(GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }

        if (currentIndex >= 0 && currentIndex < steps.size()) {

            String thumbnailUrl = steps.get(currentIndex).getThumbnailURL().trim();
            String videoUrl = steps.get(currentIndex).getVideoURL().trim();
            if (videoUrl.isEmpty() && thumbnailUrl.isEmpty()) {

                playerView.setVisibility(GONE);
                imageView.setVisibility(GONE);
                empty.setVisibility(View.VISIBLE);

                Glide.with(getActivity()).load(R.drawable.recipe_step_placeholder).into(empty);

            } else if (!videoUrl.isEmpty()) {

                empty.setVisibility(View.GONE);
                imageView.setVisibility(GONE);
                playerView.setVisibility(View.VISIBLE);
                initializePlayer(Uri.parse(videoUrl));

            } else {

                empty.setVisibility(View.GONE);
                playerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                Glide.with(getActivity()).load(thumbnailUrl).into(imageView);

            }

            hideSystemUi();
            description.setText(steps.get(currentIndex).getDescription());
            current.setText((currentIndex + 1) + "/" + steps.size());

        }

    }

    public void initializePlayer(Uri uri) {

        MediaSource mediaSource = buildMediaSource(uri);
        if (player == null) {

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
                                                        new DefaultTrackSelector(), new DefaultLoadControl());
            playerView.setPlayer(player);
            player.prepare(mediaSource, false, true);

        } else if (player.getPlaybackParameters() == null) {

            player.prepare(mediaSource, true, false);
            player.setPlayWhenReady(true);

        } else if (player.getPlaybackParameters()!= null){

            player.prepare(mediaSource, false, true);
            player.setPlayWhenReady(true);
            player.getPlaybackState();

        }

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory("ua"),
                                        new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {

        if (player != null) {

            playWhenReady = player.getPlayWhenReady();
            currentPosition = player.getCurrentPosition();

            player.stop();
            player.release();
            player = null;

        }

    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if (tablet) {
            return;
        } else {

            hideSystemUi();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                dataFrame1.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                dataFrame2.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                dataFrame1.setLayoutParams(new LinearLayout.LayoutParams(height, width / 2));
                dataFrame2.setLayoutParams(new LinearLayout.LayoutParams(height, width / 2));
            }

        }

    }

    public void setFragmentListener(FragmentListener fragmentListener) {
        this.listener = fragmentListener;
    }

    private void startPlayer(){
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void pausePlayer(){
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.back_button) {

            if(currentIndex == 0) {
                return;
            } else {

                currentIndex--;
                show();
                if (player != null) {
                    pausePlayer();
                    show();
                }

            }

        } else if (id == R.id.next_button) {

            if(currentIndex == (steps.size() - 1)) {
                return;
            } else {

                currentIndex++;
                show();
                if (player != null) {
                    pausePlayer();
                    show();
                }

            }

        }

    }

    @Override
    public void onStart() {

        super.onStart();
        if (!tablet) {

            ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int track = getResources().getConfiguration().orientation;
                    if (track != 1) {
                        width = layout.getMeasuredWidth();
                        height = layout.getMeasuredHeight();
                        dataFrame1.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                        dataFrame2.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                    } else {
                        height = layout.getMeasuredWidth();
                        width = layout.getMeasuredHeight();
                    }

                }
            });

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if (playerView != null) {

            if (player != null) {
                player.seekTo(currentPosition);
                player.setPlayWhenReady(true);
            } else {
                startPlayer();
            }

        }

    }

    @Override
    public void onPause() {

        super.onPause();
        if (player != null && Util.SDK_INT <= 23) {
            player.setPlayWhenReady(playWhenReady);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {

        if(!tablet) {

            int systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                                      | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            playerView.setSystemUiVisibility(systemUiVisibility);
            empty.setSystemUiVisibility(systemUiVisibility);
            imageView.setSystemUiVisibility(systemUiVisibility);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("index",currentIndex);
        outState.putParcelableArrayList("steps", steps);
        outState.putBoolean("tablet", tablet);

        if (player != null) {
            outState.putLong("playback_position", player.getCurrentPosition());
            outState.putBoolean("playPause", player.getPlayWhenReady());
        }
        super.onSaveInstanceState(outState);

    }

    private void onRestoreInstanceState(Bundle savedState){

        if(savedState != null) {

            steps = savedState.getParcelableArrayList("steps");

            currentIndex = savedState.getInt("index", currentIndex);
            tablet = savedState.getBoolean("tablet", tablet);

            if (player != null) {
                player.seekTo(savedState.getLong("playback_position"));
                player.setPlayWhenReady(savedState.getBoolean("playPause"));
            }

        }

    }

}