package com.avwaveaf.storyspace.view.storymaps

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mapsViewModel: MapsViewModel by viewModels()

    private var locations: List<ListStoryItem> = emptyList()

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initial fetch
        fetchPoints()

        setupObserver()
    }

    private fun fetchPoints() {
        mapsViewModel.fetchStoriesWithLocation()
    }

    private fun setupObserver() {
        mapsViewModel.stories.observe(this){
            locations = it
            addManyMarkerSpot()
        }
        mapsViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        with(mMap) {
            with(uiSettings) {
                isZoomControlsEnabled = true
                isCompassEnabled = true
            }
        }

        setupMapStyle()
        addManyMarkerSpot()
    }

    private fun setupMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this,
                    getString(R.string.style_parsing_failed_error), Toast.LENGTH_SHORT).show()
            }
        }catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, getString(R.string.cannot_find_style_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addManyMarkerSpot() {
        if (locations.isNotEmpty()) {
            locations.forEach{ data->
                val latLng = LatLng(data.lat!!, data.lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.name)
                        .snippet(data.description)
                        .icon(vectorToBitmap(R.drawable.baseline_menu_book_24, Color.parseColor("#3DDC84")))
                )
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            ))
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}