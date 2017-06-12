package com.argos.android.opencv.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.argos.android.opencv.R;
import com.argos.android.opencv.UI.FeatureListAdapter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FeatureListAdapter adapter;
    private ArrayList<String> features;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initList();
        initListener();
        checkPermission();
    }

    public void initView()
    {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.property_list);
    }

    public void initList()
    {
        features = new ArrayList<>();
        features.add(getString(R.string.feature_lane));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FeatureListAdapter(features, this);
        recyclerView.setAdapter(adapter);
    }

    public void initListener()
    {
        fab.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        //Intent intent = new Intent(MainActivity.this, ImageLoadActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
}
