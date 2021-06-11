package com.dara.incidents.data

/**
 * This data class represents an incident. Each incident is a JSON object which
 * is stored on Firebase
 * @param latitude - The latitude coordinate of the incident
 * @param longitude - The longitude coordinate of the incident
 * @param incidentType - The incident type
 */
data class Incident(
    val latitude : Double? = 0.0,
    val longitude : Double? = 0.0,
    val incidentType: IncidentType? = IncidentType()
)