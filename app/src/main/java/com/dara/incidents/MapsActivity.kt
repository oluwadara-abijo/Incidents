package com.dara.incidents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dara.incidents.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var incidentPosition: LatLng
    private lateinit var incidents: Array<Incident>
    private lateinit var selectedIncident: Incident

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(6.5244, 3.3792)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // Set click listener on map
        mMap.setOnMapClickListener {
            incidentPosition = LatLng(it.latitude, it.longitude)
            showIncidentDialog()
        }

    }

    private fun showIncidentDialog() {
        incidents = arrayOf(
            Incident("Accident", R.drawable.ic_baseline_dangerous),
            Incident("Road blocked", R.drawable.ic_baseline_block),
            Incident("Crime/Theft", R.drawable.ic_baseline_directions_run),
            Incident("Road construction", R.drawable.ic_baseline_construction),
            Incident("Fire", R.drawable.ic_baseline_local_fire_department),
            Incident("Others", R.drawable.ic_baseline_not_listed_location)
        )
        val checkedItem = 0
        selectedIncident = incidents[checkedItem]
        val incidentNames = incidents.map { it.name }
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.select_incident_type))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                // Add marker to Map
                addMarker()
                dialog.dismiss()
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(incidentNames.toTypedArray(), checkedItem) { dialog, which ->
                // Respond to item chosen
                selectedIncident = incidents[which]
                Toast.makeText(this, selectedIncident.name, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun addMarker() {
        mMap.addMarker(MarkerOptions().apply {
            position(incidentPosition)
            icon(bitmapDescriptorFromVector(this@MapsActivity, selectedIncident.icon))
        })

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}