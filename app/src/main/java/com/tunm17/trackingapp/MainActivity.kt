package com.tunm17.trackingapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tunm17.trackingapp.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.tunm17.trackingapp.databinding.ActivityMainBinding
import com.tunm17.trackingapp.ui.fragments.TrackingFragment
import com.tunm17.trackingapp.utils.TrackingUtils
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks


class MainActivity : AppCompatActivity(), PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        setSupportActionBar(binding.toolbar)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        binding.apply {
            bottomNavigationView.setupWithNavController(navHostFragment!!.findNavController())
        }

        navHostFragment!!.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.runFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
        navigateToTrackingFragmentIfNeeded(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }


    private fun requestPermissions() {
        if (TrackingUtils.hasLocationPermission(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment?.findNavController()?.navigate(R.id.action_global_trackingFragment)
        }
    }
}