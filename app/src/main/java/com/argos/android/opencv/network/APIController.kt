package com.argos.android.opencv.network

import org.json.JSONObject

class APIController constructor(serviceInjection: ServiceInterface): ServiceInterface {
    private val service: ServiceInterface = serviceInjection

    override fun request(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit,method:Int) {
        service.request(path, params, completionHandler,method)
    }
}