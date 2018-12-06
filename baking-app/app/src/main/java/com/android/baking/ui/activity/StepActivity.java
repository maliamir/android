package com.android.baking.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.baking.R;
import com.android.baking.ui.fragment.StepDetailFragment;

public class StepActivity extends AppCompatActivity{

    StepDetailFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_data_activity);

        if(savedInstanceState == null)
        {
            Bundle bundle = getIntent().getExtras();

            if(bundle.containsKey("name"))
            {
                getSupportActionBar().setTitle(bundle.getString("name")+" Step");
            }
            bundle.putBoolean("tablet",false);

            dataFragment = new StepDetailFragment();
            dataFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.data_fragment, dataFragment)
                    .commit();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getFragmentManager().putFragment(outState,"fragment", dataFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dataFragment = (StepDetailFragment) getFragmentManager().getFragment(savedInstanceState,"fragment");
        if(dataFragment.isAdded())
        {
            return;
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.data_fragment, dataFragment)
                .commit();
    }
}
