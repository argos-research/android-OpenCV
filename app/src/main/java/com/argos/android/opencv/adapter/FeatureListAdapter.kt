package com.argos.android.opencv.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.argos.android.opencv.interfaces.MainActivityCallback
import com.argos.android.opencv.model.Feature
import com.argos.android.opencv.R
import java.util.*

/**
 * Adapter for the cards on the first screen
 */

class FeatureListAdapter(private val features: ArrayList<Feature>, context: Context) : RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mainActivityCallback: MainActivityCallback = context as MainActivityCallback

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var featureName: TextView = itemView.findViewById<View>(R.id.feature_name) as TextView
        var featureThumbnail: ImageView = itemView.findViewById<View>(R.id.feature_thumbnail) as ImageView
        var pictureButton: ImageButton = itemView.findViewById<View>(R.id.button_picture) as ImageButton
        var cameraButton: ImageButton = itemView.findViewById<View>(R.id.button_camera) as ImageButton
    }
}
