package com.avwaveaf.storyspace.view.home.ui.detail

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.databinding.ActivityDetailBinding
import com.avwaveaf.storyspace.helper.formatDate
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var googleMap: GoogleMap
    private var storyLocation: LatLng? = null

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the passed story data
        val story: ListStoryItem? = intent.getParcelableExtra("story_data")
        story?.let {
            // Set the title and description
            binding.tvStoryTitle.text = it.name
            binding.tvStoryDescription.text = it.description
            binding.textStoryDate.text = formatDate(dateString = story.createdAt.toString())

            // Load the story image
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivStoryImage)

            mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

            // Set the story location if it exists
            if (it.lat != null && it.lon != null) {
                storyLocation = LatLng(it.lat, it.lon)
                showLocationInfo(true) // Show the map section
                mapFragment.getMapAsync(this)
            } else {
                showLocationInfo(false) // Hide the map section
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMapStyle()
        with(googleMap) {
            with(uiSettings) {
                isZoomControlsEnabled = true
                isCompassEnabled = true
            }
        }

        storyLocation?.let { location ->
            // Add marker at the story location
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(binding.tvStoryTitle.text.toString())
            )
            // Move the camera to the story location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        }
    }

    private fun setupMapStyle() {
        try {
            val success =
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(
                    this,
                    getString(R.string.style_parsing_failed_error), Toast.LENGTH_SHORT
                ).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, getString(R.string.cannot_find_style_error), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showLocationInfo(show: Boolean) {
        with(binding) {
            tvLocationTitle.visibility = if (show) View.VISIBLE else View.GONE
            divider.visibility = if (show) View.VISIBLE else View.GONE
        }
        if (show) {
            supportFragmentManager.beginTransaction().show(mapFragment).commit()
        } else {
            supportFragmentManager.beginTransaction().hide(mapFragment).commit()
        }
    }
}