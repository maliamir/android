package com.android.baking.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.android.baking.R;
import com.android.baking.model.Step;
import com.android.baking.ui.FragmentListener;
import com.android.baking.ui.fragment.StepFragment;
import com.android.baking.ui.fragment.StepDetailFragment;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity implements FragmentListener {

    private boolean isRunningOnTablet;

    private String name;

    private ArrayList<Step> steps;

    private FrameLayout dataFragment;

    private StepFragment fragment;

    private StepDetailFragment detailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_data_activity);

        dataFragment = findViewById(R.id.second_fragment);
        isRunningOnTablet = true;
        Bundle extras = getIntent().getBundleExtra("bundle");
        name = extras.getString("recipe_name");
        getSupportActionBar().setTitle(name);
        steps = extras.getParcelableArrayList("steps");
        extras.putBoolean("tablet", (dataFragment != null));

        if (savedInstanceState == null) {

            fragment = new StepFragment();
            fragment.setFragmentListener(this);
            fragment.setArguments(extras);
            getFragmentManager().beginTransaction().add(R.id.first_fragment, fragment).commit();

            //Checking if screen size greater than 600dp
            if (dataFragment == null) {
                isRunningOnTablet = false;
            } else {
                this.setStep(0, steps);
            }

        } else {
            fragment= (StepFragment) getFragmentManager().getFragment(savedInstanceState,"main");
            fragment.setFragmentListener(this);


            if (!fragment.isAdded())
                getFragmentManager().beginTransaction().add(R.id.first_fragment, fragment).commit();

            if(detailsFragment !=null)
            {
                detailsFragment= (StepDetailFragment) getFragmentManager().getFragment(savedInstanceState,"detail");
                getFragmentManager().beginTransaction().replace(R.id.second_fragment, detailsFragment).commit();
            }
        }
    }

    @Override
    public void setStep(int index, ArrayList<Step> steps) {
        if (!isRunningOnTablet) {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra("steps", steps);
            intent.putExtra("current", index);
            intent.putExtra("name", name);
            startActivity(intent);
        } else {
            detailsFragment = new StepDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("steps", steps);
            detailsFragment.setFragmentListener(this);
            bundle.putInt("current", index);
            bundle.putBoolean("tablet", true);
            detailsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.second_fragment, detailsFragment).commit();
        }
    }

    @Override
    public void setCurrent(int index) {
        if (isRunningOnTablet) {
            fragment.updateView(index);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, "main", fragment);

        if (isRunningOnTablet && dataFragment !=null) {
            try{
                getFragmentManager().putFragment(outState, "detail", detailsFragment);
            }catch (NullPointerException e) {}

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (dataFragment == null) {
            isRunningOnTablet = false;
        }
    }
}