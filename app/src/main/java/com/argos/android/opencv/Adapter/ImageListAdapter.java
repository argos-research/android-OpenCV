package com.argos.android.opencv.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.argos.android.opencv.Interface.DialogCallback;
import com.argos.android.opencv.R;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder>
{
    private int[] images;
    private LayoutInflater inflater;
    private String feature;
    private DialogCallback dialogCallback;

    public ImageListAdapter(int[] images, String feature, Context context)
    {
        this.images = images;
        this.feature = feature;
        inflater = LayoutInflater.from(context);
        dialogCallback = (DialogCallback) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(inflater.inflate(R.layout.image, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        holder.imageView.setImageResource(images[position]);

        holder.imageView.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialogCallback.launchImageDetection(feature, images[position]);
                    }
                }
        );
    }

    @Override
    public int getItemCount()
    {
        return images.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
