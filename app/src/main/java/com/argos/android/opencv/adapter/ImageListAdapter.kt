package com.argos.android.opencv.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.argos.android.opencv.Interface.DialogCallback
import com.argos.android.opencv.R

class ImageListAdapter(private val images: IntArray, private val feature: String, context: Context) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val dialogCallback: DialogCallback

    init {
        inflater = LayoutInflater.from(context)
        dialogCallback = context as DialogCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])

        holder.imageView.setOnClickListener { dialogCallback.launchImageDetection(feature, images[position]) }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById<View>(R.id.image) as ImageView
        }
    }
}
