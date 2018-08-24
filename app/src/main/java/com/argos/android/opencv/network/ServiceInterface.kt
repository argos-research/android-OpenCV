package com.argos.android.opencv.network

import com.android.volley.Request
import org.json.JSONObject

interface ServiceInterface {
    fun request(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit,method:Int = Request.Method.POST)
}