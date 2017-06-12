package com.argos.android.opencv.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.argos.android.opencv.R;

import java.util.ArrayList;

public class FeatureListAdapter extends RecyclerView.Adapter<FeatureListAdapter.ViewHolder>
{
    private ArrayList<String> features;
    private LayoutInflater inflater;

    public FeatureListAdapter(ArrayList<String> features, Context context)
    {
        this.features = features;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(inflater.inflate(R.layout.feature_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.featureName.setText(features.get(position));
    }

    @Override
    public int getItemCount()
    {
        return features.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView featureName;
        SwitchCompat featureState;

        public ViewHolder(View itemView)
        {
            super(itemView);
            featureName = (TextView) itemView.findViewById(R.id.feature_name);
            featureState = (SwitchCompat) itemView.findViewById(R.id.feature_state);
        }
    }
}
