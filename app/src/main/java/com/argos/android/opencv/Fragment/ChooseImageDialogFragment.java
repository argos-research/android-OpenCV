package com.argos.android.opencv.Fragment;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.argos.android.opencv.R;
import com.argos.android.opencv.Adapter.ImageListAdapter;

/**
 * BottomSheet for choosing image. Refer {@link com.argos.android.opencv.Activity.ImageLoadActivity}
 * Add images of resolution 640x480 inside drawables and include them in {@link #images}
 */

public class ChooseImageDialogFragment extends BottomSheetDialogFragment
{
    private RecyclerView recyclerView;
    private ImageListAdapter imageListAdapter;
    private int[] images = {R.drawable.scene_1, R.drawable.scene_2};

    @Override
    public void setupDialog(Dialog dialog, int style)
    {
        View contentView = View.inflate(getContext(), R.layout.choose_image, null);
        dialog.setContentView(contentView);

        recyclerView = (RecyclerView) contentView.findViewById(R.id.image_list);
        imageListAdapter = new ImageListAdapter(images, getArguments().getString("feature"), getContext());

        recyclerView.setAdapter(imageListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
    }
}
