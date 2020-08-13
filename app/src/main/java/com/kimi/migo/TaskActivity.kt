package com.kimi.migo

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        requestByNetworkStatus()
    }

    fun requestByNetworkStatus(){

        val publicApi = "https://code-test.migoinc-dev.com/status"
        val privateApi = "http://192.168.2.2/status"
        var requestApi: String? = null

        // Get Network ConnectivityManager
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val allNetworksInfo = connectivityManager.activeNetworkInfo


        allNetworksInfo?.let {
            if (it.isConnected) {
                requestApi = publicApi

                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo

                wifiInfo?.let {
                    if (TextUtils.isEmpty(it.ssid).not()) {
                        requestApi = privateApi
                    }
                }
            }
        }

        requestApi?.let {url ->
            if (url.isNotEmpty()) {
                runHttpRequest(url)
            }
        }
    }


    fun runHttpRequest(url: String) {


        // Use Fuel to do httpRequest
        // Fuel: The easiest HTTP networking library for Kotlin/Android.
        url.httpGet()
            .responseString{ request, response, result ->

                result.success {
                    Log.d("kimi", "onCreate: ${it}")
                    runOnUiThread {
                        tv_result.text = "Request Success Result: \n${it}"
                    }

                }

                result.failure {
                    Log.d("kimi", "failure: ${it.response}")
                    runOnUiThread {
                        tv_result.text = "Request Failure code: ${it}"
                        tv_result.textSize = 14f
                    }
                }

            }
    }
}
