package com.argos.android.opencv.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.argos.android.opencv.Fragment.ChooseImageDialogFragment;
import com.argos.android.opencv.Interface.DialogCallback;
import com.argos.android.opencv.Interface.MainActivityCallback;
import com.argos.android.opencv.Model.Feature;
import com.argos.android.opencv.R;
import com.argos.android.opencv.Adapter.FeatureListAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityCallback, DialogCallback
{
    private static final String TAG = "MainActivity";
    private static String version;
    private RecyclerView recyclerView;
    private BottomSheetDialogFragment bottomSheet;
    private LinearLayoutManager layoutManager;
    private FeatureListAdapter adapter;
    private ArrayList<Feature> features;
    private File cascadeFile;
    private InputStream inputStream;
    private FileOutputStream outputStream;
    public static boolean CASCADE_FILE_LOADED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "v" + pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        initView();
        initIO();
        initList();
        checkPermission();
    }

    public void initView()
    {
        recyclerView = (RecyclerView) findViewById(R.id.feature_list);
        bottomSheet = new ChooseImageDialogFragment();
    }

    public void initIO()
    {
        try
        {
            inputStream = getResources().getAssets().open("cascade.xml");
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            cascadeFile = new File(cascadeDir, "cascade.xml");
            outputStream = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
        }
        catch (IOException e)
        {
            CASCADE_FILE_LOADED = false;
            Log.e(TAG, "Failed to load cascade file");
            e.printStackTrace();
        }
    }

    public void initList()
    {
        features = new ArrayList<>();
        features.add(new Feature(getString(R.string.feature_lane), R.drawable.lane_detection_thumbnail));
        features.add(new Feature(getString(R.string.feature_vehicle), R.drawable.vehicle_detection_thumbnail));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FeatureListAdapter(features, this);
        recyclerView.setAdapter(adapter);
    }

    public void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.info)
            showAboutDialog();

        return true;
    }

    public void showAboutDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(dialogView);

        (dialogView.findViewById(R.id.button_github)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(getString(R.string.about_link_github)));
                        startActivity(intent);
                    }
                }
        );

        ((TextView)(dialogView.findViewById(R.id.version))).setText(version);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void chooseImage(Feature feature)
    {
        Bundle bundle = new Bundle();
        bundle.putString("feature", feature.getFeatureName());
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void launchCameraDetection(Feature feature)
    {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra("cascadeFilePath", cascadeFile.getAbsolutePath());
        intent.putExtra("feature", feature.getFeatureName());
        startActivity(intent);
    }

    @Override
    public void launchImageDetection(String feature, @DrawableRes int image)
    {
        Intent intent = new Intent(MainActivity.this, ImageLoadActivity.class);
        intent.putExtra("cascadeFilePath", cascadeFile.getAbsolutePath());
        intent.putExtra("feature", feature);
        intent.putExtra("image", image);
        startActivity(intent);
    }
}
