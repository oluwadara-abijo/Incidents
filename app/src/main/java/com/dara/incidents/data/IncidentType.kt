package com.dara.incidents.data

import com.dara.incidents.R

/**
 * This data class represents an incident type
 * @param name - The name of the incident e.g accident, fire etc
 * @param icon - The icon for the incident type
 */
class IncidentType(
    val name: String? = "Others",
    val icon: Int? = R.drawable.ic_baseline_not_listed_location
) {
}