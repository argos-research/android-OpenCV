package com.argos.android.opencv.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.argos.android.opencv.R
import com.argos.android.opencv.adapter.FeatureListAdapter
import com.argos.android.opencv.fragment.ChooseImageDialogFragment
import com.argos.android.opencv.interfaces.DialogCallback
import com.argos.android.opencv.interfaces.MainActivityCallback
import com.argos.android.opencv.model.Feature
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity(), MainActivityCallback, DialogCallback {

    private var recyclerView: RecyclerView? = null
    private var bottomSheet: BottomSheetDialogFragment? = null
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: FeatureListAdapter? = null
    private var mFeatureList: ArrayList<Feature> = ArrayList()
    private var cascadeFile: File? = null
    private var inputStream: InputStream? = null
    private var outputStream: FileOutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            version = "v" + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        initView()
        initIO()
        initList()
        checkPermission()
    }

    private fun initView() {
        recyclerView = findViewById<View>(R.id.feature_list) as RecyclerView
        bottomSheet = ChooseImageDialogFragment()
    }

    private fun initIO() {
        try {
            inputStream = resources.assets.open("cascade.xml")
            val cascadeDir = getDir("cascade", Context.MODE_PRIVATE)
            cascadeFile = File(cascadeDir, "cascade.xml")
            outputStream = FileOutputStream(cascadeFile!!)

            val buffer = ByteArray(4096)
            var bytesRead: Int = inputStream!!.read(buffer)
            while ( bytesRead != -1) {
                outputStream!!.write(buffer, 0, bytesRead)
                bytesRead = inputStream!!.read(buffer)
            }

            inputStream!!.close()
            outputStream!!.close()
        } catch (e: IOException) {
            CASCADE_FILE_LOADED = false
            Log.e(TAG, "Failed to load cascade file")
            e.printStackTrace()
        }

    }

    private fun initList() {
        mFeatureList.add(Feature(Feature.OVERTAKING, R.drawable.vehicle_detection_thumbnail))
        mFeatureList.add(Feature(Feature.LANE_DETECTION, R.drawable.lane_detection_thumbnail))

        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager

        adapter = FeatureListAdapter(mFeatureList, this)
        recyclerView!!.adapter = adapter
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.info)
            showAboutDialog()

        return true
    }

    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_about, null)
        builder.setView(dialogView)

        dialogView.findViewById<View>(R.id.button_github).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.about_link_github))
            startActivity(intent)
        }

        (dialogView.findViewById<View>(R.id.version) as TextView).text = version

        val dialog = builder.create()
        dialog.show()
    }

    override fun chooseImage(feature: Feature) {
        val bundle = Bundle()
        bundle.putString("feature", feature.featureName)
        bottomSheet!!.arguments = bundle
        bottomSheet!!.show(supportFragmentManager, bottomSheet!!.tag)
    }

    override fun launchCameraDetection(feature: Feature) {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putExtra("cascadeFilePath", cascadeFile!!.absolutePath)
        intent.putExtra("feature", feature.featureName)
        startActivity(intent)

    }

    override fun launchImageDetection(feature: String, @DrawableRes image: Int) {
        System.gc()
        val intent = Intent(this@MainActivity, ImageLoadActivity::class.java)
        intent.putExtra("cascadeFilePath", cascadeFile!!.absolutePath)
        intent.putExtra("feature", feature)
        intent.putExtra("image", image)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        private var version: String? = null
        private var CASCADE_FILE_LOADED = true
    }
}
