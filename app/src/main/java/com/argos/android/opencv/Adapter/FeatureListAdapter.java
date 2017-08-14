package com.argos.android.opencv.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.argos.android.opencv.Interface.MainActivityCallback;
import com.argos.android.opencv.Model.Feature;
import com.argos.android.opencv.R;
import java.util.ArrayList;

/**
 * Adapter for the cards on the first screen
 */

public class FeatureListAdapter extends RecyclerView.Adapter<FeatureListAdapter.ViewHolder>
{
    private ArrayList<Feature> features;
    private LayoutInflater inflater;
    private MainActivityCallback mainActivityCallback;

    public FeatureListAdapter(ArrayList<Feature> features, Context context)
    {
        this.features = features;
        inflater = LayoutInflater.from(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(inflater.inflate(R.layout.feature_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        holder.featureName.setText(features.get(position).getFeatureName());
        holder.featureThumbnail.setImageResource(features.get(position).getFeatureThumbnail());

        holder.pictureButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mainActivityCallback.chooseImage(features.get(position));
                    }
                }
        );

        holder.cameraButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mainActivityCallback.launchCameraDetection(features.get(position));
                    }
                }
        );
    }

    @Override
    public int getItemCount()
    {
        return features.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView featureName;
        ImageView featureThumbnail;
        ImageButton pictureButton;
        ImageButton cameraButton;

        public ViewHolder(View itemView)
        {
            super(itemView);
            featureName = (TextView) itemView.findViewById(R.id.feature_name);
            featureThumbnail = (ImageView) itemView.findViewById(R.id.feature_thumbnail);
            pictureButton = (ImageButton) itemView.findViewById(R.id.button_picture);
            cameraButton = (ImageButton) itemView.findViewById(R.id.button_camera);
        }
    }
}
