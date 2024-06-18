package com.daniel_linge.viewreferee

import java.util.Date
import java.util.UUID

data class Referee(
    val id: String = UUID.randomUUID().toString(), // Generiert eine eindeutige ID
    val name: String = "",
    var observationCount: Int = 0,
    var lastObservationDate: Date? = null
){
    constructor() : this("", "", 0, null)
}
