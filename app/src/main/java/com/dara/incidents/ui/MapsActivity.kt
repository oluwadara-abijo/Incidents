package com.dara.incidents.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dara.incidents.R
import com.dara.incidents.data.Incident
import com.dara.incidents.data.IncidentType
import com.dara.incidents.databinding.ActivityMapsBinding
import com.dara.incidents.utils.NetworkUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var incidentPosition: LatLng
    private lateinit var incidentTypes: Array<IncidentType>
    private lateinit var selectedIncidentType: IncidentType
    private lateinit var allIncidents: ArrayList<Incident>
    private lateinit var firebaseDatabaseReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        networkUtils = NetworkUtils(this)

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

        // Add a marker in Lagos and move the camera
        val lagos = LatLng(6.5244, 3.3792)
        mMap.addMarker(MarkerOptions().position(lagos).title("Marker in Lagos"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lagos))

        // Set click listener on map
        mMap.setOnMapClickListener {
            incidentPosition = LatLng(it.latitude, it.longitude)
            showIncidentDialog()
        }

        retrieveIncidents()

    }

    private fun retrieveIncidents() {
        if (networkUtils.isNetworkAvailable()) {
            firebaseDatabaseReference = Firebase.database.reference.child("incidents")
            allIncidents = arrayListOf()
            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val incident = snapshot.getValue(Incident::class.java)
                    if (incident != null) {
                        allIncidents.add(incident)
                    }
                    for (everyIncident in allIncidents) {
                        addMarker(everyIncident)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

            }
            firebaseDatabaseReference.addChildEventListener(childEventListener)
        } else {
            Toast.makeText(
                this,
                getString(R.string.connect_to_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showIncidentDialog() {
        incidentTypes = arrayOf(
            IncidentType(getString(R.string.accident), R.drawable.ic_baseline_dangerous),
            IncidentType(getString(R.string.road_blocked), R.drawable.ic_baseline_block),
            IncidentType(getString(R.string.crime), R.drawable.ic_baseline_directions_run),
            IncidentType(getString(R.string.construction), R.drawable.ic_baseline_construction),
            IncidentType(getString(R.string.fire), R.drawable.ic_baseline_local_fire_department),
            IncidentType(getString(R.string.others), R.drawable.ic_baseline_not_listed_location)
        )
        val checkedItem = 0
        selectedIncidentType = incidentTypes[checkedItem]
        val incidentNames = incidentTypes.map { it.name }
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.select_incident_type))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                // Respond to neutral button press
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                // Add marker to Map
                val incident = Incident(
                    incidentPosition.latitude,
                    incidentPosition.longitude,
                    selectedIncidentType
                )
                addMarker(incident)
                saveIncident(incident)
                dialog.dismiss()
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(incidentNames.toTypedArray(), checkedItem) { _, which ->
                // Respond to item chosen
                selectedIncidentType = incidentTypes[which]
            }
            .show()
    }

    private fun addMarker(incident: Incident) {
        mMap.addMarker(MarkerOptions().apply {
            position(LatLng(incident.latitude!!, incident.longitude!!))
            icon(bitmapDescriptorFromVector(this@MapsActivity, incident.incidentType!!.icon!!))
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

    private fun saveIncident(incident: Incident) {
        firebaseDatabaseReference.push().setValue(incident).addOnCompleteListener {
            Toast.makeText(this, getString(R.string.incident_saved), Toast.LENGTH_SHORT).show()
        }

    }
}