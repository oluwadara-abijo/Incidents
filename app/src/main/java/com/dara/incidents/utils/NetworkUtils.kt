package com.dara.incidents.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.FragmentActivity

class NetworkUtils(
    private val activity: FragmentActivity?
) {

    /**
     * This function checks for network connectivity
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}