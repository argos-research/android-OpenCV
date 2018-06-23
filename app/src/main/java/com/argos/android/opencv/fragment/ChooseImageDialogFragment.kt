package com.argos.android.opencv.fragment

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.argos.android.opencv.R
import com.argos.android.opencv.adapter.ImageListAdapter

/**
 * BottomSheet for choosing image. Refer [com.argos.android.opencv.activity.ImageLoadActivity]
 * Add images of resolution 640x480 inside drawables and include them in [.images]
 */

class ChooseImageDialogFragment : BottomSheetDialogFragment() {
    private var recyclerView: RecyclerView? = null
    private var imageListAdapter: ImageListAdapter? = null
    private val images = intArrayOf(
            R.drawable.scene_1,
            R.drawable.scene_2,
            R.drawable.track_01,
            R.drawable.track_02,
            R.drawable.track_03,
            R.drawable.track_04,
            R.drawable.track_05,
            R.drawable.track_06,
            R.drawable.speed_dreams1,
            R.drawable.speed_dreams3,
            R.drawable.speed_dreams4)

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.choose_image, null)
        dialog.setContentView(contentView)

        recyclerView = contentView.findViewById<View>(R.id.image_list) as RecyclerView
        imageListAdapter = ImageListAdapter(images, arguments!!.getString("feature")!!, context!!)

        recyclerView!!.adapter = imageListAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}
