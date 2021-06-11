package com.dara.incidents

import com.google.android.gms.maps.model.LatLng

/**
 * This data class represents an incident. Each incident is a JSON object which
 * is stored on Firebase
 * @param position - The latitude and longitude coordinates of the incident
 * @param incidentType - The incident type
 */
data class Incident (val position : LatLng, val incidentType : IncidentType) {
}