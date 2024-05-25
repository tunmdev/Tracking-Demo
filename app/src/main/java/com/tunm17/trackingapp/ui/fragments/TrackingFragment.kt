package com.tunm17.trackingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType.ROUND
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.SquareCap
import com.tunm17.trackingapp.Constants
import com.tunm17.trackingapp.R
import com.tunm17.trackingapp.databinding.FragmentTrackingBinding
import com.tunm17.trackingapp.services.Polyline
import com.tunm17.trackingapp.services.TrackingService
import com.tunm17.trackingapp.ui.viewmodels.MainViewModel


class TrackingFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null
    companion object {
        fun newInstance() = TrackingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync {
                map = it
                addAllPolylines()
            }
        }

        binding.startBtn.setOnClickListener {
            toggleStart()
        }

        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            if (pathPoints.isNotEmpty() && pathPoints.last().size == 1) {
                Log.v("tunm17", "add start marker")
                var startPoint: LatLng = pathPoints.last().last()
                map?.addMarker(
                    MarkerOptions()
                        .position(startPoint)
                        .title("Hi!!!")
                        .snippet("Population: 4,137,400")
                        .zIndex(1.0f)
                )
            }
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }
    

    private fun toggleStart() {
        if (isTracking) {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            binding.startBtn.text = "Start"
            binding.finishBtn.visibility = View.VISIBLE
        } else {
            binding.startBtn.text = "Stop"
            binding.finishBtn.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        Log.v("tunm1", "pathPoint size: ${pathPoints.size}, last: ${pathPoints.last().size}")
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .startCap(SquareCap())
                .endCap(SquareCap())
                .jointType(ROUND)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .startCap(SquareCap())
                .endCap(SquareCap())
                .jointType(ROUND)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)

    }
}