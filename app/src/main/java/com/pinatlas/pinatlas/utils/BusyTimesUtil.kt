package com.pinatlas.pinatlas.utils

import android.net.Uri
import android.util.Log
import com.pinatlas.pinatlas.model.BusyData
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import java.io.Reader

object BusyTimesUtil {
    var TAG = BusyTimesUtil::class.java.simpleName

    val BT_BASE_URL: String = "us-central1-comp3004-1578511896868.cloudfunctions.net"

    fun buildBusyTimesURI(placeId: String): String {
        return Uri.Builder()
            .scheme("https")
            .authority(BT_BASE_URL)
            .appendPath("on_place_create")
            .appendQueryParameter("placeId", placeId)
            .build()
            .toString()
    }

    fun fetchBusyTimesData(placeId: String, responseHandler: (result: BusyData?) -> Any?) {
        buildBusyTimesURI(placeId).httpGet().responseObject(BusyTimesDeserializer()) {_, _, result ->
            when (result) {
                is Result.Failure -> {
                    Log.w(TAG, "Error when fetching busy times: ${result.getException()}")
                    responseHandler(null)
                }

                is Result.Success -> {
                    val (data, _) = result
                    responseHandler(data)
                }
            }
        }
    }

    class BusyTimesDeserializer: ResponseDeserializable<BusyData> {
        override fun deserialize(reader: Reader): BusyData? {
            return Gson().fromJson(reader, BusyData::class.java)
        }
    }
}