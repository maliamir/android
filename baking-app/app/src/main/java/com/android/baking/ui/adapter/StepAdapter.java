package com.android.baking.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.baking.R;
import com.android.baking.model.Step;

import java.util.ArrayList;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.RecyclerHolder> {

    Context context;
    private ArrayList<Step> steps;
    public int[] trackers;


    public StepAdapter(Context context, ArrayList<Step> steps, int[] trackers) {
        this.context = context;
        this.steps = steps;
        this.trackers = trackers;
    }


    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.steps_list_item, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, final int position) {

        holder.title.setText(steps.get(position).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout root;

        RecyclerHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            root = (RelativeLayout) itemView.findViewById(R.id.root);
        }
    }
}