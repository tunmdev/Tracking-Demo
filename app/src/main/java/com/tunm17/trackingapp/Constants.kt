package com.tunm17.trackingapp

import android.graphics.Color

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "tracking"
    const val NOTIFICATION_ID = 1

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_UPDATE_INTERVAL = 2000L

    const val POLYLINE_COLOR =  Color.RED
    const val POLYLINE_WIDTH =  10f

    const val MAP_ZOOM =  16f
}