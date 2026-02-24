package com.nextbridge.internetcheckapp.core.utils

import android.content.Context
import android.net.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import android.net.*
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class NetworkMonitor(context: Context) {

    private val tag = "NetworkMonitor"

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected: Flow<Boolean> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial state
        val activeNetwork = connectivityManager.activeNetwork
        val isCurrentlyConnected = connectivityManager
            .getNetworkCapabilities(activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        trySend(isCurrentlyConnected)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    val isInternetAvailable: Flow<Boolean> = callbackFlow {

        val scope = CoroutineScope(Dispatchers.IO)

        suspend fun validate() {
            Log.d(tag, "validate: ")
            trySend(hasRealInternet())
        }

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                scope.launch { validate() }
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial check
        scope.launch { validate() }

        /**
         * Ping after every 5 seconds to check Internet Availability
         */
        scope.launch {
            while (true) {
                validate()
                delay(5000)
            }
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
            scope.cancel()
        }
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    suspend fun hasRealInternet(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://clients3.google.com/generate_204")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 1500
                readTimeout = 1500
                instanceFollowRedirects = false
                useCaches = false
            }
            connection.connect()
            connection.responseCode == 204
        } catch (e: Exception) {
            false
        }
    }

}