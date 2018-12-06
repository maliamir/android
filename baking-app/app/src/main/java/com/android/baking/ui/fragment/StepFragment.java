package com.android.baking.ui.fragment;

import java.util.ArrayList;

import android.app.Fragment;

import android.os.Bundle;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.android.baking.R;

import com.android.baking.model.Ingredient;
import com.android.baking.model.Step;

import com.android.baking.ui.adapter.IngredientAdapter;
import com.android.baking.ui.adapter.StepAdapter;
import com.android.baking.ui.FragmentListener;
import com.android.baking.ui.RecyclerListener;

public class StepFragment extends Fragment {

    int index, x1, x2;
    int[] trackers;
    boolean tablet;

    LinearLayoutManager ingredientsManager, stepsManager;
    FragmentListener dataFragmentListener;

    ArrayList<Step> steps;
    ArrayList<Ingredient> ingredients;

    @BindView(R.id.steps_rv)
    RecyclerView stepsRecyclerView;

    @BindView(R.id.ingredient_rv)
    RecyclerView ingredientsRecyclerView;

    public void setFragmentListener(FragmentListener listener) {
        this.dataFragmentListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.steps_fragment, container, false);
        ButterKnife.bind(this, rootView);

        stepsManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ingredientsManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        if (savedInstanceState == null) {

            Bundle extra = getArguments();
            tablet = extra.getBoolean("tablet", false);
            steps = extra.getParcelableArrayList("steps");
            ingredients = extra.getParcelableArrayList("ingredients");
            index = 0;

        } else {

            tablet = savedInstanceState.getBoolean("tablet", false);
            steps = savedInstanceState.getParcelableArrayList("steps");
            ingredients = savedInstanceState.getParcelableArrayList("ingredients");
            index = savedInstanceState.getInt("position");

            x1 = savedInstanceState.getInt("x1");
            x2 = savedInstanceState.getInt("x2");

        }

        trackers = new int[steps.size()];
        if (tablet) {
            trackers[index] = 1;
        }

        ingredientsRecyclerView.setLayoutManager(ingredientsManager);
        ingredientsRecyclerView.setAdapter(new IngredientAdapter(getActivity(), ingredients));
        ingredientsRecyclerView.addItemDecoration(new DividerItemDecoration(ingredientsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        if (x1 != 0) {
            ingredientsRecyclerView.getLayoutManager().scrollToPosition(x1);
        }

        stepsRecyclerView.setLayoutManager(stepsManager);
        stepsRecyclerView.setAdapter(new StepAdapter(getActivity(), steps, trackers));
        stepsRecyclerView.addItemDecoration(new DividerItemDecoration(stepsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        if (x2 != 0) {
            stepsRecyclerView.getLayoutManager().scrollToPosition(x2);
        }

        stepsRecyclerView.addOnItemTouchListener(new RecyclerListener(getActivity(), stepsRecyclerView, new RecyclerListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        dataFragmentListener.setStep(position, steps);
                        updateView(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                })
        );

        if (tablet) {
            updateView(index);
            dataFragmentListener.setStep(index, steps);
        }

        return rootView;

    }

    public void updateView(int index) {

        this.index = index;
        if (!tablet) {
            return;
        } else {

            trackers = new int[steps.size()];
            try {

                trackers[index] = 1;
                ((StepAdapter) stepsRecyclerView.getAdapter()).trackers = trackers;
                stepsRecyclerView.getAdapter().notifyDataSetChanged();
                stepsRecyclerView.scrollToPosition(index);

            } catch (ArrayIndexOutOfBoundsException E) {
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("steps", steps);
        outState.putParcelableArrayList("ingredients", ingredients);
        outState.putBoolean("tablet", tablet);
        outState.putInt("position", index);
        outState.putInt("x2", ((LinearLayoutManager) stepsRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        outState.putInt("x1", ((LinearLayoutManager) ingredientsRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());

    }

}