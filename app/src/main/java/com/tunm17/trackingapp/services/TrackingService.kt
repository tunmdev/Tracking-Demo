package com.tunm17.trackingapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.tunm17.trackingapp.Constants
import com.tunm17.trackingapp.MainActivity
import com.tunm17.trackingapp.R
import com.tunm17.trackingapp.utils.TrackingUtils
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService: LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValue() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        postInitialValue()

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        Timber.d("Start service")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                        startForegroundService()
                    }
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pause service")
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtils.hasLocationPermission(this)) {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,Constants.FASTEST_UPDATE_INTERVAL)
                    .setWaitForAccurateLocation(true)
                    .setMinUpdateIntervalMillis(Constants.FASTEST_UPDATE_INTERVAL)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun getMainActivityPendingIntent() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java).also {
                    it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                },
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java).also {
                    it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                },
                FLAG_UPDATE_CURRENT,
            )
        }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions)
            .setContentTitle("Tracking App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build(), FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_DEFAULT
        )
        channel.description = "Channel description"
        notificationManager.createNotificationChannel(channel)
    }
}