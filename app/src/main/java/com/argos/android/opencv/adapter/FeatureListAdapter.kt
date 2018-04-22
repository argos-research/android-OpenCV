package com.argos.android.opencv.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.argos.android.opencv.Interface.MainActivityCallback
import com.argos.android.opencv.Model.Feature
import com.argos.android.opencv.R
import java.util.ArrayList

/**
 * Adapter for the cards on the first screen
 */

class FeatureListAdapter(private val features: ArrayList<Feature>, context: Context) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val mainActivityCallback: MainActivityCallback

    init {
        inflater = LayoutInflater.from(context)
        mainActivityCallback = context as MainActivityCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.feature_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.featureName.text = features[position].featureName
        holder.featureThumbnail.setImageResource(features[position].featureThumbnail)

        holder.pictureButton.setOnClickListener { mainActivityCallback.chooseImage(features[position]) }

        holder.cameraButton.setOnClickListener { mainActivityCallback.launchCameraDetection(features[position]) }
    }

    override fun getItemCount(): Int {
        return features.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var featureName: TextView
        var featureThumbnail: ImageView
        var pictureButton: ImageButton
        var cameraButton: ImageButton

        init {
            featureName = itemView.findViewById<View>(R.id.feature_name) as TextView
            featureThumbnail = itemView.findViewById<View>(R.id.feature_thumbnail) as ImageView
            pictureButton = itemView.findViewById<View>(R.id.button_picture) as ImageButton
            cameraButton = itemView.findViewById<View>(R.id.button_camera) as ImageButton
        }
    }
}
